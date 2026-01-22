package com.wwd.financeapi.api;

import com.wwd.common.constant.ServiceNamesConstant;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.financeapi.dto.IncomeQueryDTO;
import com.wwd.financeapi.dto.IncomeRecordDTO;
import com.wwd.financeapi.dto.IncomeStatisticsDTO;
import com.wwd.financeapi.dto.IncomeTrendQueryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
        contextId = "IncomeRecordServiceClient",
        path = "/api/finance/income"
)
public interface IncomeRecordServiceClient {


    @Operation(summary = "分页查询收入流水")
    @GetMapping("/page")
    Result<PageResult<IncomeRecordDTO>> pageIncomeRecord(@Parameter(description = "查询参数") @ModelAttribute IncomeQueryDTO incomeQueryDTO);

    @Operation(summary = "获取收入详情")
    @GetMapping("/{id}")
    Result<IncomeRecordDTO> getIncomeDetail(@Parameter(description = "收入ID") @PathVariable Long id);

    @Operation(summary = "创建或更新收入流水")
    @PostMapping("/operate")
    Result<Long> saveOrUpdateIncome(@Parameter(description = "收入流水数据") @RequestBody IncomeRecordDTO dto);

    @Operation(summary = "删除收入流水")
    @DeleteMapping("/delete/{id}")
    Result<Boolean> deleteIncome(@Parameter(description = "收入ID") @PathVariable Long id);

    @Operation(summary = "批量删除收入流水")
    @DeleteMapping("/batch")
    Result<Boolean> batchDeleteIncome(@Parameter(description = "收入ID列表") @RequestBody List<Long> ids);

    @Operation(summary = "更新结算状态")
    @PutMapping("/settle")
    Result<Boolean> updateSettleStatus(
    @Parameter(description = "收入ID") @RequestParam Long id,
    @Parameter(description = "结算状态(Y/N)") @RequestParam String isSettled);

    @Operation(summary = "批量更新结算状态")
    @PutMapping("/batch/settle")
    Result<Boolean> batchUpdateSettleStatus(
    @Parameter(description = "收入ID列表") @RequestBody List<Long> ids,
    @Parameter(description = "结算状态(Y/N)") @RequestParam String isSettled);

    @Operation(summary = "获取收入统计信息")
    @GetMapping("/statistics")
    Result<IncomeStatisticsDTO> getIncomeStatistics(@Parameter(description = "查询参数") @ModelAttribute IncomeQueryDTO queryDTO);

    @Operation(summary = "获取收入趋势数据")
    @GetMapping("/trend")
    Result<List<Map<String, Object>>> getIncomeTrend(@Parameter(description = "查询参数") @ModelAttribute IncomeTrendQueryDTO queryDTO);

    @Operation(summary = "获取收入来源分类选项")
    @GetMapping("/api/income/source-category/list")
    Result<List<Map<String, Object>>> getSourceCategories();

    @Operation(summary = "获取基金账户选项")
    @GetMapping("/api/fund/account/list")
    Result<List<Map<String, Object>>> getFundAccounts();
}
