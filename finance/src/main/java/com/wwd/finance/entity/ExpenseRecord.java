// finance/src/main/java/com/wwd/finance/entity/ExpenseRecord.java
package com.wwd.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 支出流水实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("expense_record")
public class ExpenseRecord {

    /**
     * 支出ID
     */
    @TableId(value = "expense_id", type = IdType.AUTO)
    private Long expenseId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 账户ID
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 关联的预算项目ID
     */
    @TableField("budget_item_id")
    private Long budgetItemId;

    /**
     * 支出金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 货币类型
     */
    @TableField("currency")
    private String currency;

    /**
     * 支出日期
     */
    @TableField("expense_date")
    private LocalDate expenseDate;

    /**
     * 支付方式
     */
    @TableField("payment_method")
    private String paymentMethod;

    /**
     * 收款方
     */
    @TableField("payee")
    private String payee;

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
     * 对应的预算金额
     */
    @TableField("budget_amount")
    private BigDecimal budgetAmount;

    /**
     * 差异金额 = budget - actual
     */
    @TableField("difference_amount")
    private BigDecimal differenceAmount;

    /**
     * 差异百分比
     */
    @TableField("difference_percent")
    private BigDecimal differencePercent;

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
    @TableField("deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}