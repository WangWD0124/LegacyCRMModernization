package com.wwd.customerapi.dto;

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
public class UserQueryDTO {

    // 精确查询
    private String username;          // 用户名（模糊）
    private String email;             // 邮箱（精确）
    private String phone;             // 手机号（模糊）
    private Integer status;           // 状态
    private Long deptId;              // 部门ID

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