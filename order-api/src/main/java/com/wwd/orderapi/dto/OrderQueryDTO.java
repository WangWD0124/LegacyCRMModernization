package com.wwd.orderapi.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customerapi.dto.UserQueryDTO
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-12-13
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-12-13     wangwd7          v1.0.0               创建
 */
@Data
public class OrderQueryDTO {

    // 精确查询
    private Long order_id;
    private String order_no;
    private String remark;
    private String order_source;
    private String order_status;
    private Long payment_id;
    private Long consignee_id;
    private Long shipping_id;
    private Long userId;

    // 列表查询
    private List<Integer> statusList; // 状态列表
    private List<Long> deptIdList;    // 部门ID列表

    // 时间范围
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;
    private LocalDateTime updateTimeStart;
    private LocalDateTime updateTimeEnd;

    // 分页参数
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    // 排序参数
    private String orderBy = "create_time";
    private Boolean asc = false; // 默认降序
}