package com.wwd.finance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算项目实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("budget_item")
public class BudgetItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预算ID
     */
    @TableId(value = "budget_id", type = IdType.AUTO)
    private Long budgetId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 基金账户ID
     */
    @TableField("fund_account_id")
    private Long fundAccountId;

    /**
     * 项目标题
     */
    private String title;

    /**
     * 预算分类代码
     */
    @TableField("category_code")
    private String categoryCode;

    /**
     * 消费类型
     */
    @TableField("consumption_type")
    private String consumptionType;

    /**
     * 预算金额
     */
    @TableField("budget_amount")
    private BigDecimal budgetAmount;

    /**
     * 实际金额
     */
    @TableField("actual_amount")
    private BigDecimal actualAmount;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 旧况描述
     */
    @TableField("past_situation")
    private String pastSituation;

    /**
     * 核心需求
     */
    @TableField("core_need")
    private String coreNeed;

    /**
     * 决策过程
     */
    @TableField("decision_process")
    private String decisionProcess;

    /**
     * 替代方案
     */
    private String alternatives;

    /**
     * 预计决策时间（分钟）
     */
    @TableField("expected_decision_minutes")
    private Integer expectedDecisionMinutes;

    /**
     * 实际决策时间（分钟）
     */
    @TableField("actual_decision_minutes")
    private Integer actualDecisionMinutes;

    /**
     * 研究平台数量
     */
    @TableField("research_platforms_count")
    private Integer researchPlatformsCount;

    /**
     * 预算调整次数
     */
    @TableField("budget_adjustment_count")
    private Integer budgetAdjustmentCount;

    /**
     * 纠结评分
     */
    @TableField("struggle_score")
    private Integer struggleScore;

    /**
     * 决策开始时间
     */
    @TableField("decision_start_time")
    private LocalDateTime decisionStartTime;

    /**
     * 决策结束时间
     */
    @TableField("decision_end_time")
    private LocalDateTime decisionEndTime;

    /**
     * 执行时间
     */
    @TableField("execute_time")
    private LocalDateTime executeTime;

    /**
     * 状态：PENDING-待审批, APPROVED-已批准, EXECUTED-已执行, CANCELLED-已取消
     */
    private String status;

    /**
     * 状态变更原因
     */
    @TableField("status_reason")
    private String statusReason;

    /**
     * 满意度评分
     */
    @TableField("satisfaction_score")
    private Integer satisfactionScore;

    /**
     * 事后感想
     */
    @TableField("after_thought")
    private String afterThought;

    /**
     * 购买渠道
     */
    @TableField("purchase_channel")
    private String purchaseChannel;

    /**
     * 物品生命周期
     */
    @TableField("item_lifecycle")
    private String itemLifecycle;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean deleted;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}