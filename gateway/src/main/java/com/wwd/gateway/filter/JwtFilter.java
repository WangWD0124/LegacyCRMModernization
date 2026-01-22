package com.wwd.gateway.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.wwd.common.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.gateway.filter.JwtFilter
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-10
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-10     wangwd7          v1.0.0               创建
 */
@Component
public class JwtFilter implements GlobalFilter, Ordered {

    @Resource
    private JwtUtil jwtUtil;

    // 不需要认证的路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/customer/user/login","/api/customer/user/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 白名单直接放行
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // 2. 获取Token（从Authorization头）
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "缺少或格式错误的Token");
        }

        // 去掉"Bearer "
        String token = authHeader.substring(7);

        // 3. 简单验证Token（这里只是检查格式，真实验证需要解析）
        if (!isValidTokenFormat(token)) {
            return unauthorized(exchange, "无效的Token");
        }

        // 4. 模拟解析用户ID（实际要从Token中解析）
        // 这里先写死，下一步会实现真正的解析
        Long userId = extractUserIdFromToken(token);

        // 5. 将用户ID放入请求头，传递给下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private Long extractUserIdFromToken(String token) {
        try {
            // 使用JWT工具类解析
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Token解析失败", e);
        }
    }

    private boolean isValidTokenFormat(String token) {
        return jwtUtil.validateToken(token);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
