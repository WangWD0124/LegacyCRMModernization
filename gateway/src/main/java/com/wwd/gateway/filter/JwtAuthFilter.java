package com.wwd.gateway.filter;

import com.wwd.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.gateway.filter.JwtAuthFilter
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-07
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-07     wangwd7          v1.0.0               创建
 */
@Component
@Slf4j
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private ErrorResponseHandler errorHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        String path = exchange.getRequest().getURI().getPath();

        // 1. 检查白名单
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 2. Token不存在
        if (StringUtils.isEmpty(token)) {
            return errorHandler.handleError(
                    exchange,
                    ErrorResponseHandler.ErrorCode.TOKEN_MISSING
            );
        }

        try {
            // 3. 验证Token
            Claims claims = jwtUtil.validateToken(token);

            // 4. 检查Token是否被拉黑（登出/注销场景）
            if (tokenBlacklistService.isBlacklisted(token)) {
                return errorHandler.handleError(
                        exchange,
                        ErrorResponseHandler.ErrorCode.TOKEN_INVALID,
                        "令牌已被撤销"
                );
            }

            // 5. 检查用户状态（如是否被禁用）
            UserStatus userStatus = userService.getUserStatus(claims.getSubject());
            if (userStatus == UserStatus.DISABLED) {
                return errorHandler.handleError(
                        exchange,
                        ErrorResponseHandler.ErrorCode.ACCESS_DENIED,
                        "账户已被禁用"
                );
            }

            // 6. 检查IP是否被限制
            String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            if (ipBlacklistService.isBlocked(clientIp)) {
                log.warn("黑名单IP访问被拒绝: {}, path: {}", clientIp, path);
                return errorHandler.handleError(
                        exchange,
                        ErrorResponseHandler.ErrorCode.IP_BLOCKED
                );
            }

            // 7. 检查访问频率（防刷）
            if (!rateLimitService.tryAcquire(clientIp, path)) {
                return errorHandler.handleError(
                        exchange,
                        ErrorResponseHandler.ErrorCode.RATE_LIMITED
                );
            }

            // 8. 所有检查通过，传递用户信息
            ServerHttpRequest mutatedRequest = addUserHeaders(exchange.getRequest(), claims);
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            // Token过期特殊处理：可以返回401，并提示客户端刷新token
            exchange.getResponse().getHeaders().add("X-Token-Expired", "true");
            return errorHandler.handleError(
                    exchange,
                    ErrorResponseHandler.ErrorCode.TOKEN_EXPIRED
            );

        } catch (JwtException e) {
            log.warn("JWT验证失败: {}, path: {}", e.getMessage(), path);
            return errorHandler.handleError(
                    exchange,
                    ErrorResponseHandler.ErrorCode.TOKEN_INVALID
            );

        } catch (Exception e) {
            log.error("认证过程异常", e);
            // 返回通用错误，避免泄露内部信息
            return errorHandler.handleError(
                    exchange,
                    ErrorResponseHandler.ErrorCode.ACCESS_DENIED,
                    "认证服务暂时不可用"
            );
        }
    }
}