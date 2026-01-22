package com.wwd.financeapi.api;
import com.wwd.common.constant.ServiceNamesConstant;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.financeapi.dto.expense.ExpenseRecordDTO;
import com.wwd.financeapi.dto.expense.ExpenseQueryDTO;
import com.wwd.financeapi.dto.expense.ExpenseStatisticsDTO;
import com.wwd.financeapi.dto.expense.ExpenseTrendQueryDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.financeapi.api.IncomeRecordServiceClient
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-18
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-18     wangwd7          v1.0.0               创建
 */
@FeignClient(
        name = ServiceNamesConstant.FINANCE_SERVICE,
        contextId = "ExpenseRecordServiceClient",
        path = "/api/finance/expense"
)
public interface ExpenseRecordServiceClient {

    @Operation(summary = "分页查询支出流水")
    @GetMapping("/api/finance/expense/page")
    Result<PageResult<ExpenseRecordDTO>> pageExpense(@ModelAttribute ExpenseQueryDTO expenseQueryDTO);

    @Operation(summary = "获取支出详情")
    @GetMapping("/api/finance/expense/{id}")
    Result<Map<String, Object>> getExpenseDetail(@PathVariable Long id);

    @Operation(summary = "创建或更新支出流水")
    @PostMapping("/api/finance/expense/operate")
    Result<Long> saveOrUpdateExpense(@RequestBody ExpenseRecordDTO dto);

    @Operation(summary = "删除支出流水")
    @DeleteMapping("/api/finance/expense/delete/{id}")
    Result<Boolean> deleteExpense(@PathVariable Long id);

    @Operation(summary = "批量删除支出流水")
    @DeleteMapping("/api/finance/expense/batch")
    Result<Boolean> batchDeleteExpense(@RequestBody List<Long> ids);

    @Operation(summary = "关联预算项目")
    @PostMapping("/api/finance/expense/link-budget")
    Result<Boolean> linkBudgetItem(
            @RequestParam Long expenseId,
            @RequestParam Long budgetItemId,
            @RequestParam BigDecimal budgetAmount);

    @Operation(summary = "批量关联预算项目")
    @PostMapping("/api/finance/expense/batch-link-budget")
    Result<Boolean> batchLinkBudgetItem(
            @RequestParam List<Long> expenseIds,
            @RequestParam Long budgetItemId,
            @RequestParam BigDecimal budgetAmount);

    @Operation(summary = "获取支出统计信息")
    @GetMapping("/api/finance/expense/statistics")
    Result<ExpenseStatisticsDTO> getExpenseStatistics(@ModelAttribute ExpenseQueryDTO queryDTO);

    @Operation(summary = "获取支出趋势数据")
    @GetMapping("/api/finance/expense/trend")
    Result<List<Map<String, Object>>> getExpenseTrend(@ModelAttribute ExpenseTrendQueryDTO queryDTO);

    @Operation(summary = "获取差异分析数据")
    @GetMapping("/api/finance/expense/difference-analysis")
    Result<Map<String, Object>> getDifferenceAnalysis(@ModelAttribute ExpenseQueryDTO queryDTO);

    @Operation(summary = "获取支付方式选项")
    @GetMapping("/api/finance/expense/payment-methods")
    Result<List<Map<String, Object>>> getPaymentMethods();

    @Operation(summary = "获取预算项目选项")
    @GetMapping("/api/finance/expense/budget/items/list")
    Result<List<Map<String, Object>>> getBudgetItems(@ModelAttribute ExpenseQueryDTO queryDTO);

    @Operation(summary = "获取基金账户选项")
    @GetMapping("/api/finance/expense/fund/account/list")
    Result<List<Map<String, Object>>> getFundAccounts();
}
