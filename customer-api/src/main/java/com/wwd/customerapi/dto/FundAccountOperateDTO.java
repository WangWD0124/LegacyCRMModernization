package com.wwd.customerapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.entity.FundAccount
 * @Description: 基金账户实体类
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-04
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-04     wangwd7          v1.0.0               创建
 */
@Data
public class FundAccountOperateDTO {
    private Long accountId;
    private Long userId;
    private String accountCode;
    private String accountName;
    private String accountType;
    private String description;
    private String balance;
    private String monthlyTarget;
    private String icon;
    private String color;
    private Integer sortOrder;
    private Boolean isActive;
}
