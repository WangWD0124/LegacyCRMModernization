package com.wwd.gateway.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.gateway.handler.ErrorResponseHandler
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-08
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-08     wangwd7          v1.0.0               创建
 */
@Component
public class ErrorResponseHandler {

    @Autowired
    private MessageSource messageSource;

    private static final Map<String, HttpStatus> ERROR_STATUS_MAP = new HashMap<>();

    static {
        ERROR_STATUS_MAP.put("token_expired", HttpStatus.UNAUTHORIZED);
        ERROR_STATUS_MAP.put("token_invalid", HttpStatus.UNAUTHORIZED);
        ERROR_STATUS_MAP.put("access_denied", HttpStatus.FORBIDDEN);
        ERROR_STATUS_MAP.put("rate_limit", HttpStatus.TOO_MANY_REQUESTS);
    }

    public Mono<Void> handleError(ServerWebExchange exchange, ErrorCode errorCode,
                                  Object... args) {
        ServerHttpResponse response = exchange.getResponse();

        // 根据错误码确定HTTP状态
        HttpStatus status = ERROR_STATUS_MAP.getOrDefault(
                errorCode.getCode(), HttpStatus.UNAUTHORIZED);
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 国际化消息（可选）
        Locale locale = getLocaleFromRequest(exchange.getRequest());
        String message = messageSource.getMessage(
                "error." + errorCode.getCode(), args, errorCode.getDefaultMessage(), locale);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(message)
                .timestamp(System.currentTimeMillis())
                .path(exchange.getRequest().getURI().getPath())
                .requestId(exchange.getRequest().getId()) // 请求追踪ID
                .build();

        // 记录安全日志
        logSecurityEvent(exchange, errorCode, message);

        return writeJsonResponse(response, errorResponse);
    }

    private Mono<Void> writeJsonResponse(ServerHttpResponse response, Object body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            byte[] bytes = mapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return response.setComplete();
        }
    }

    // 错误码枚举
    public enum ErrorCode {
        TOKEN_MISSING("token_missing", "缺少访问令牌"),
        TOKEN_EXPIRED("token_expired", "访问令牌已过期"),
        TOKEN_INVALID("token_invalid", "无效的访问令牌"),
        ACCESS_DENIED("access_denied", "访问被拒绝"),
        RATE_LIMITED("rate_limit", "请求过于频繁，请稍后再试"),
        IP_BLOCKED("ip_blocked", "IP地址已被限制访问");

        private final String code;
        private final String defaultMessage;

        ErrorCode(String code, String defaultMessage) {
            this.code = code;
            this.defaultMessage = defaultMessage;
        }

        public String getCode() { return code; }
        public String getDefaultMessage() { return defaultMessage; }
    }
}
