package com.wwd.customer.entity;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.entity.MessageIdempotent
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

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息幂等性记录表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("message_idempotent")
public class MessageIdempotent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息唯一ID
     */
    @TableField(value = "message_id")
    private String messageId;

    /**
     * 业务ID
     */
    @TableField(value = "business_id")
    private String businessId;

    /**
     * 业务类型:ORDER/PAYMENT/DEDUCT
     */
    @TableField(value = "business_type")
    private String businessType;

    /**
     * 状态:PROCESSING/SUCCESS/FAILED
     */
    @TableField(value = "status")
    private String status;

    /**
     * 重试次数
     */
    @TableField(value = "retry_count")
    private Integer retryCount;

    /**
     * 最后重试时间
     */
    @TableField(value = "last_try_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastTryTime;

    /**
     * 处理结果
     */
    @TableField(value = "process_result")
    private String processResult;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField(value = "version")
    private Integer version;

    /**
     * 扩展字段
     */
    @TableField(value = "extra_info")
    private String extraInfo;
}
