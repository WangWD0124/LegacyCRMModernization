package com.wwd.customerapi.dto;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customerapi.dto.MessageIdempotentQueryDTO
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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 消息幂等性查询条件
 */
@Data
public class MessageIdempotentQueryDTO {

    private String messageId;
    private String businessId;
    private String businessType;
    private String status;
    private String serviceName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    // 分页参数
    private Integer page = 1;
    private Integer size = 20;
    private String sortField = "created_time";
    private String sortOrder = "DESC";

    // 扩展查询条件
    private Integer minRetryCount;
    private Integer maxRetryCount;
    private Boolean includeDeleted = false;
}
