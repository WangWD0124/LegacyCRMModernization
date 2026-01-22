package com.wwd.financeapi.dto.budget;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算项目VO（供Feign接口使用）
 */
@Data
public class BudgetItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long budgetId;
    private Long userId;
    private Long fundAccountId;
    private String fundAccountName;
    private String title;
    private String categoryCode;
    private String categoryName;
    private String consumptionType;
    private String consumptionTypeName;
    private BigDecimal budgetAmount;
    private BigDecimal actualAmount;
    private String currency;
    private String pastSituation;
    private String coreNeed;
    private String decisionProcess;
    private String alternatives;
    private Integer expectedDecisionMinutes;
    private Integer actualDecisionMinutes;
    private Integer researchPlatformsCount;
    private Integer budgetAdjustmentCount;
    private Integer struggleScore;
    private LocalDateTime decisionStartTime;
    private LocalDateTime decisionEndTime;
    private LocalDateTime executeTime;
    private String status;
    private String statusName;
    private String statusReason;
    private Integer satisfactionScore;
    private String afterThought;
    private String purchaseChannel;
    private String itemLifecycle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}