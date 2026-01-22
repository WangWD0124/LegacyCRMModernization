package com.wwd.finance.enums;

import com.alibaba.nacos.client.config.http.ServerHttpAgent;
import lombok.Getter;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.finance.enums.BudgetStatus
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-14
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-14     wangwd7          v1.0.0               创建
 */
/**
 * 预算状态枚举
 */
@Getter
public enum BudgetStatus {
    PENDING("PENDING", "待审批"),
    APPROVED("APPROVED", "已批准"),
    EXECUTED("EXECUTED", "已执行"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String description;

    BudgetStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static BudgetStatus getByCode(String code) {
        for (BudgetStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
