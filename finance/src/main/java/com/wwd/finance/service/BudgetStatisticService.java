package com.wwd.finance.service;

import com.wwd.financeapi.dto.budget.BudgetItemQueryDTO;
import com.wwd.financeapi.dto.budget.BudgetStatisticDTO;

/**
 * 预算统计服务
 */
public interface BudgetStatisticService {

    /**
     * 获取预算统计信息
     */
    BudgetStatisticDTO getStatistics(BudgetItemQueryDTO query);
}