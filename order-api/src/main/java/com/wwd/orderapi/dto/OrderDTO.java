package com.wwd.orderapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.entity.User
 * @Description: 数据传输对象
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-10-12
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-10-12     wangwd7          v1.0.0               创建
 */
@Data
public class OrderDTO {
    private Long order_id;
    private String order_no;
    private String remark;
    private String order_source;
    private String order_status;
    private Long payment_id;
    private Long consignee_id;
    private Long shipping_no;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
