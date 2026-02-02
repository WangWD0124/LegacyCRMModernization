package com.wwd.financeapi.dto.budget;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 更新预算项目DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BudgetItemOperateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预算ID
     */
    private Long budgetId;

    /**
     * 项目标题
     */
    private String title;

    /**
     * 基金账户ID
     */
    private Long fundAccountId;

    /**
     * 预算分类代码
     */
    private String categoryCode;

    /**
     * 消费类型
     */
    private String consumptionType;

    /**
     * 预算金额
     */
    private BigDecimal budgetAmount;

    /**
     * 实际金额
     */
    private BigDecimal actualAmount;

    /**
     * 货币类型
     */
    private String currency;

    /**
     * 旧况描述
     */
    private String pastSituation;

    /**
     * 核心需求
     */
    private String coreNeed;

    /**
     * 决策过程
     */
    private String decisionProcess;

    /**
     * 替代方案
     */
    private String alternatives;

    /**
     * 预计决策时间（分钟）
     */
    private Integer expectedDecisionMinutes;

    /**
     * 实际决策时间（分钟）
     */
    private Integer actualDecisionMinutes;

    /**
     * 研究平台数量
     */
    private Integer researchPlatformsCount;

    /**
     * 预算调整次数
     */
    private Integer budgetAdjustmentCount;

    /**
     * 纠结评分
     */
    private Integer struggleScore;

    /**
     * 决策开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime decisionStartTime;

    /**
     * 决策结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime decisionEndTime;

    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executeTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态变更原因
     */
    private String statusReason;

    /**
     * 满意度评分
     */
    private Integer satisfactionScore;

    /**
     * 事后感想
     */
    private String afterThought;

    /**
     * 购买渠道
     */
    private String purchaseChannel;

    /**
     * 物品生命周期
     */
    private String itemLifecycle;
}