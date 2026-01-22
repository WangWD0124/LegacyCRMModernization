package com.wwd.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wwd.finance.entity.ExpenseRecord;
import com.wwd.financeapi.dto.expense.ExpenseQueryDTO;
import com.wwd.financeapi.dto.expense.ExpenseRecordDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 支出流水服务接口
 */
public interface ExpenseRecordService extends IService<ExpenseRecord> {

    /**
     * 分页查询支出流水
     */
    IPage<ExpenseRecordDTO> pageExpense(ExpenseQueryDTO expenseQueryDTO);

    /**
     * 获取支出详情
     */
    ExpenseRecord getExpenseDetail(Long expenseId);

    /**
     * 创建或更新支出流水
     */
    Long saveOrUpdateExpense(ExpenseRecordDTO dto);

    /**
     * 删除支出流水
     */
    boolean deleteExpense(Long expenseId);

    /**
     * 批量删除支出流水
     */
    boolean batchDeleteExpense(List<Long> ids);

    /**
     * 获取支出统计信息
     */
    Map<String, Object> getExpenseStatistics(Map<String, Object> params);

    /**
     * 获取支出趋势数据
     */
    List<Map<String, Object>> getExpenseTrend(Map<String, Object> params);

    /**
     * 获取差异分析数据
     */
    Map<String, Object> getDifferenceAnalysis(Map<String, Object> params);

    /**
     * 导出支出数据
     */
    List<ExpenseRecord> exportExpense(ExpenseQueryDTO expenseQueryDTO);

    /**
     * 获取当前用户ID
     */
    Long getCurrentUserId();

    /**
     * 关联预算项目
     */
    boolean linkBudgetItem(Long expenseId, Long budgetItemId, BigDecimal budgetAmount);

    /**
     * 批量关联预算项目
     */
    boolean batchLinkBudgetItem(List<Long> expenseIds, Long budgetItemId, BigDecimal budgetAmount);
}