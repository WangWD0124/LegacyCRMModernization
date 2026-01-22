package com.wwd.customerapi.dto;

import lombok.Data;


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
public class FundAccountQueryDTO {
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
    private String is_active;

    // 时间范围
    private String createdAtStart;
    private String createdAtEnd;
    private String updatedAtStart;
    private String updatedAtEnd;
    
    // 分页参数
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    // 排序参数
    private String orderBy = "created_at";
    private Boolean asc = false; // 默认降序
}
