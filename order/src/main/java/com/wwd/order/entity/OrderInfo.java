package com.wwd.order.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.entity.User
 * @Description: 业务实体类
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
public class OrderInfo {
    private Long orderId;
    private String orderNo;
    private String remark;
    private String orderSource;
    private Integer orderStatus;
    private Long paymentId;
    private Long consigneeId;
    private Long shippingId;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
