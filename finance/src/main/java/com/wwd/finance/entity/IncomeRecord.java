// finance/src/main/java/com/wwd/finance/entity/IncomeRecord.java
package com.wwd.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 收入流水实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("income_record")
public class IncomeRecord {

    /**
     * 收入ID
     */
    @TableId(value = "income_id", type = IdType.AUTO)
    private Long incomeId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 收入入账的基金账户ID
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 货币类型
     */
    @TableField("currency")
    private String currency;

    /**
     * 收入日期
     */
    @TableField("income_date")
    private LocalDate incomeDate;

    /**
     * 收入来源类型代码
     */
    @TableField("source_type_code")
    private String sourceTypeCode;

    /**
     * 收入来源详情
     */
    @TableField("source_detail")
    private String sourceDetail;

    /**
     * 工作时长（兼职相关）
     */
    @TableField("work_hours")
    private BigDecimal workHours;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 分配规则（JSON格式）
     */
    @TableField("allocation_rule")
    private String allocationRule;

    /**
     * 是否已结算（Y/N）
     */
    @TableField("is_settled")
    private String isSettled;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标志（0-未删除，1-已删除）
     */
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private String isDeleted;
}