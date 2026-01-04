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
public class FundAccountDTO {
    private Long account_id;
    private Long user_id;
    private String account_code;
    private String account_name;
    private String account_type;
    private String description;
    private String balance;
    private String monthly_target;
    private String icon;
    private String color;
    private Integer sort_order;
    private Boolean is_active;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
