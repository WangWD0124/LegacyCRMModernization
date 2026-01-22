package com.wwd.financeapi.dto.budget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预算统计DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BudgetStatisticDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总预算
     */
    private BigDecimal totalBudget = BigDecimal.ZERO;

    /**
     * 总实际支出
     */
    private BigDecimal totalActual = BigDecimal.ZERO;

    /**
     * 待审批数量
     */
    private Integer pendingCount = 0;

    /**
     * 平均纠结评分
     */
    private Double avgStruggleScore = 0.0;

    /**
     * 已批准数量
     */
    private Integer approvedCount = 0;

    /**
     * 已执行数量
     */
    private Integer executedCount = 0;

    /**
     * 已取消数量
     */
    private Integer cancelledCount = 0;

    /**
     * 超预算项目数
     */
    private Integer overBudgetCount = 0;

    /**
     * 在预算内项目数
     */
    private Integer underBudgetCount = 0;
}