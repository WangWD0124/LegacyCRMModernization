package com.wwd.finance.interceptor;

import com.wwd.finance.context.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.interceptor.UserContextInterceptor
 * @Description:    当前用户上下文拦截器
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
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从请求头获取网关传递的用户ID
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr != null) {
            UserContext.setCurrentUserId(Long.parseLong(userIdStr));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        //请求结束后清理ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
