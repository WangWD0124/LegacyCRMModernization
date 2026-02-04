package com.wwd.common.enums;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.enums.BusinessType
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-02-02
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-02-02     wangwd7          v1.0.0               创建
 */

import lombok.Getter;

/**
 * 业务类型枚举
 */
@Getter
public enum BusinessType {

    ACCOUNT_DEDUCT("ACCOUNT_DEDUCT", "账户扣款"),
    BUDGET_EXECUTE("BUDGET_EXECUTE", "预算执行"),
    TRANSFER("TRANSFER", "转账"),
    BILL_PAYMENT("BILL_PAYMENT", "账单支付"),
    SYSTEM_JOB("SYSTEM_JOB", "系统任务");

    private final String code;
    private final String desc;

    BusinessType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BusinessType getByCode(String code) {
        for (BusinessType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
