package com.wwd.finance.service.impl;

import com.wwd.finance.context.UserContext;
import com.wwd.finance.mapper.BudgetItemMapper;
import com.wwd.finance.service.BudgetStatisticService;
import com.wwd.financeapi.dto.budget.BudgetItemQueryDTO;
import com.wwd.financeapi.dto.budget.BudgetStatisticDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预算统计服务实现
 */
@Service
public class BudgetStatisticServiceImpl implements BudgetStatisticService {

    @Autowired
    private BudgetItemMapper budgetItemMapper;

    @Override
    public BudgetStatisticDTO getStatistics(BudgetItemQueryDTO query) {
        Long userId = UserContext.getCurrentUserId(); // 获取当前用户ID
        BudgetStatisticDTO statistic = budgetItemMapper.selectStatistics(userId, query);

        // 处理平均分，保留一位小数
        if (statistic.getAvgStruggleScore() != null) {
            double avgScore = Math.round(statistic.getAvgStruggleScore() * 10.0) / 10.0;
            statistic.setAvgStruggleScore(avgScore);
        } else {
            statistic.setAvgStruggleScore(0.0);
        }

        return statistic;
    }

}