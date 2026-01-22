package com.wwd.financeapi.dto.budget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 预算项目查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BudgetItemQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 项目标题
     */
    private String title;

    /**
     * 预算分类代码
     */
    private String categoryCode;

    /**
     * 消费类型
     */
    private String consumptionType;

    /**
     * 状态
     */
    private String status;

    /**
     * 基金账户ID
     */
    private Long fundAccountId;

    /**
     * 创建时间开始
     */
    private String createTimeStart;

    /**
     * 创建时间结束
     */
    private String createTimeEnd;

    /**
     * 决策时间开始
     */
    private String decisionTimeStart;

    /**
     * 决策时间结束
     */
    private String decisionTimeEnd;

    /**
     * 执行时间开始
     */
    private String executeTimeStart;

    /**
     * 执行时间结束
     */
    private String executeTimeEnd;

    /**
     * 最小预算金额
     */
    private BigDecimal budgetAmountMin;

    /**
     * 最大预算金额
     */
    private BigDecimal budgetAmountMax;

    /**
     * 最小纠结评分
     */
    private Integer struggleScoreMin;

    /**
     * 最小满意度评分
     */
    private Integer satisfactionScoreMin;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 是否升序
     */
    private Boolean asc;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}