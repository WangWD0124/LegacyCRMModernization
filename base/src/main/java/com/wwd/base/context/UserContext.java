package com.wwd.base.context;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.context.UserContext
 * @Description:    用户上下文：存储当前请求的用户信息
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-10
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-10     wangwd7          v1.0.0               创建
 */
public class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    public static Long getCurrentUserId() {
        return CURRENT_USER.get();
    }

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
