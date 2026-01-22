package com.wwd.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.finance.entity.ExpenseRecord;
import com.wwd.finance.service.ExpenseRecordService;
import com.wwd.financeapi.api.ExpenseRecordServiceClient;
import com.wwd.financeapi.dto.expense.ExpenseQueryDTO;
import com.wwd.financeapi.dto.expense.ExpenseRecordDTO;
import com.wwd.financeapi.dto.expense.ExpenseStatisticsDTO;
import com.wwd.financeapi.dto.expense.ExpenseTrendQueryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支出流水控制器 - 实现Feign接口
 */
@Slf4j
@RestController
@RequestMapping("/api/finance/expense")
@RequiredArgsConstructor
@Tag(name = "支出管理", description = "支出流水管理接口")
public class ExpenseRecordController implements ExpenseRecordServiceClient {

    private final ExpenseRecordService expenseRecordService;

    @Override
    @Operation(summary = "分页查询支出流水")
    @GetMapping("/page")
    public Result<PageResult<ExpenseRecordDTO>> pageExpense(ExpenseQueryDTO expenseQueryDTO) {
        try {
            IPage<ExpenseRecordDTO> page = expenseRecordService.pageExpense(expenseQueryDTO);
            PageResult pageResult = new PageResult(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询支出流水失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取支出详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getExpenseDetail(@PathVariable Long id) {
        try {
            ExpenseRecord expenseRecord = expenseRecordService.getExpenseDetail(id);
            if (expenseRecord == null) {
                return Result.error("支出记录不存在");
            }

            return Result.success(convertToMap(expenseRecord));
        } catch (Exception e) {
            log.error("获取支出详情失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "创建或更新支出流水")
    @PostMapping("/operate")
    public Result<Long> saveOrUpdateExpense(@RequestBody ExpenseRecordDTO dto) {
        try {
            Long expenseId = expenseRecordService.saveOrUpdateExpense(dto);
            return Result.success(expenseId);
        } catch (Exception e) {
            log.error("保存支出流水失败", e);
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "删除支出流水")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteExpense(@PathVariable Long id) {
        try {
            boolean success = expenseRecordService.deleteExpense(id);
            return success ? Result.success(true) : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除支出流水失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "批量删除支出流水")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteExpense(@RequestBody List<Long> ids) {
        try {
            boolean success = expenseRecordService.batchDeleteExpense(ids);
            return success ? Result.success(true) : Result.error("批量删除失败");
        } catch (Exception e) {
            log.error("批量删除支出流水失败", e);
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "关联预算项目")
    @PostMapping("/link-budget")
    public Result<Boolean> linkBudgetItem(
            @RequestParam Long expenseId,
            @RequestParam Long budgetItemId,
            @RequestParam BigDecimal budgetAmount) {
        try {
            boolean success = expenseRecordService.linkBudgetItem(expenseId, budgetItemId, budgetAmount);
            return success ? Result.success(true) : Result.error("关联失败");
        } catch (Exception e) {
            log.error("关联预算项目失败", e);
            return Result.error("关联失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "批量关联预算项目")
    @PostMapping("/batch-link-budget")
    public Result<Boolean> batchLinkBudgetItem(
            @RequestParam List<Long> expenseIds,
            @RequestParam Long budgetItemId,
            @RequestParam BigDecimal budgetAmount) {
        try {
            boolean success = expenseRecordService.batchLinkBudgetItem(expenseIds, budgetItemId, budgetAmount);
            return success ? Result.success(true) : Result.error("批量关联失败");
        } catch (Exception e) {
            log.error("批量关联预算项目失败", e);
            return Result.error("批量关联失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取支出统计信息")
    @GetMapping("/statistics")
    public Result<ExpenseStatisticsDTO> getExpenseStatistics(ExpenseQueryDTO queryDTO) {
        try {
            Map<String, Object> params = convertQueryDTOToMap(queryDTO);
            Map<String, Object> statistics = expenseRecordService.getExpenseStatistics(params);

            ExpenseStatisticsDTO result = new ExpenseStatisticsDTO();
            // 使用BeanUtils复制属性
            BeanUtils.copyProperties(convertStatisticsMap(statistics), result);

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取支出统计信息失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取支出趋势数据")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getExpenseTrend(ExpenseTrendQueryDTO queryDTO) {
        try {
            Map<String, Object> params = new HashMap<>();
            BeanUtils.copyProperties(queryDTO, params);

            List<Map<String, Object>> trendData = expenseRecordService.getExpenseTrend(params);
            return Result.success(trendData);
        } catch (Exception e) {
            log.error("获取支出趋势数据失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取差异分析数据")
    @GetMapping("/difference-analysis")
    public Result<Map<String, Object>> getDifferenceAnalysis(ExpenseQueryDTO queryDTO) {
        try {
            Map<String, Object> params = convertQueryDTOToMap(queryDTO);
            Map<String, Object> analysisData = expenseRecordService.getDifferenceAnalysis(params);

            return Result.success(analysisData);
        } catch (Exception e) {
            log.error("获取差异分析数据失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取支付方式选项")
    @GetMapping("/payment-methods")
    public Result<List<Map<String, Object>>> getPaymentMethods() {
        try {
            // 返回支付方式枚举
            List<Map<String, Object>> methods = Arrays.asList(
                    createMethod("WECHAT", "微信支付"),
                    createMethod("ALIPAY", "支付宝"),
                    createMethod("CASH", "现金"),
                    createMethod("BANK_CARD", "银行卡"),
                    createMethod("CREDIT_CARD", "信用卡"),
                    createMethod("OTHER", "其他")
            );
            return Result.success(methods);
        } catch (Exception e) {
            log.error("获取支付方式失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取预算项目选项")
    @GetMapping("/budget/items/list")
    public Result<List<Map<String, Object>>> getBudgetItems(ExpenseQueryDTO queryDTO) {
        try {
            // 这里应该调用预算服务获取预算项目
            // 暂时返回模拟数据
            List<Map<String, Object>> items = Arrays.asList(
                    createBudgetItem(1L, "办公设备采购", "EQUIPMENT", new BigDecimal("5000.00")),
                    createBudgetItem(2L, "员工培训", "TRAINING", new BigDecimal("3000.00")),
                    createBudgetItem(3L, "市场推广", "MARKETING", new BigDecimal("10000.00")),
                    createBudgetItem(4L, "差旅费用", "TRAVEL", new BigDecimal("8000.00"))
            );
            return Result.success(items);
        } catch (Exception e) {
            log.error("获取预算项目失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取基金账户选项")
    @GetMapping("/fund/account/list")
    public Result<List<Map<String, Object>>> getFundAccounts() {
        try {
            // 这里应该调用基金服务获取账户
            // 暂时返回模拟数据
            List<Map<String, Object>> accounts = Arrays.asList(
                    createAccount(1L, "工资卡"),
                    createAccount(2L, "储蓄卡"),
                    createAccount(3L, "支付宝"),
                    createAccount(4L, "微信钱包"),
                    createAccount(5L, "投资账户")
            );
            return Result.success(accounts);
        } catch (Exception e) {
            log.error("获取基金账户失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 导出支出数据（不在Feign接口中，但保留原功能）
     */
    @Operation(summary = "导出支出数据")
    @GetMapping("/export")
    public void exportExpense(ExpenseQueryDTO expenseQueryDTO,
            HttpServletResponse response) {
        try {
            List<ExpenseRecord> expenseRecords = expenseRecordService.exportExpense(expenseQueryDTO);

            // 设置响应头
            String fileName = "expense_export_" + System.currentTimeMillis() + ".csv";
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            // 写入CSV文件
            writeExpenseToCsv(response.getWriter(), expenseRecords);

        } catch (Exception e) {
            log.error("导出支出数据失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("导出失败: " + e.getMessage());
            } catch (IOException ex) {
                log.error("写入错误信息失败", ex);
            }
        }
    }

    /**
     * 将查询DTO转换为Map参数
     */
    private Map<String, Object> convertQueryDTOToMap(ExpenseQueryDTO dto) {
        Map<String, Object> params = new HashMap<>();
        try {
            // 使用反射获取所有字段
            java.lang.reflect.Field[] fields = ExpenseQueryDTO.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(dto);
                if (value != null) {
                    params.put(field.getName(), value);
                }
            }
        } catch (IllegalAccessException e) {
            log.error("转换查询DTO失败", e);
        }
        return params;
    }

    /**
     * 将实体转换为Map
     */
    private Map<String, Object> convertToMap(ExpenseRecord expenseRecord) {
        Map<String, Object> map = new HashMap<>();

        try {
            // 复制基本属性
            map.put("expenseId", expenseRecord.getExpenseId());
            map.put("userId", expenseRecord.getUserId());
            map.put("accountId", expenseRecord.getAccountId());
            map.put("budgetItemId", expenseRecord.getBudgetItemId());
            map.put("amount", expenseRecord.getAmount());
            map.put("currency", expenseRecord.getCurrency());
            map.put("expenseDate", expenseRecord.getExpenseDate());
            map.put("paymentMethod", expenseRecord.getPaymentMethod());
            map.put("payee", expenseRecord.getPayee());
            map.put("description", expenseRecord.getDescription());
            map.put("remark", expenseRecord.getRemark());
            map.put("budgetAmount", expenseRecord.getBudgetAmount());
            map.put("differenceAmount", expenseRecord.getDifferenceAmount());
            map.put("differencePercent", expenseRecord.getDifferencePercent());
            map.put("createdAt", expenseRecord.getCreatedAt());
            map.put("updatedAt", expenseRecord.getUpdatedAt());

            // 添加额外的显示字段
            map.put("paymentMethodName", getPaymentMethodName(expenseRecord.getPaymentMethod()));

        } catch (Exception e) {
            log.error("转换实体到Map失败", e);
        }

        return map;
    }

    /**
     * 转换统计Map为对象
     */
    private ExpenseStatisticsDTO convertStatisticsMap(Map<String, Object> statistics) {
        ExpenseStatisticsDTO dto = new ExpenseStatisticsDTO();

        try {
            if (statistics != null) {
                dto.setTotalExpense(convertToBigDecimal(statistics.get("totalExpense")));
                dto.setCurrentMonthExpense(convertToBigDecimal(statistics.get("currentMonthExpense")));
                dto.setAvgExpense(convertToBigDecimal(statistics.get("avgExpense")));
                dto.setBudgetLinkedRate(convertToBigDecimal(statistics.get("budgetLinkedRate")));
                dto.setTotalCount(convertToLong(statistics.get("totalCount")));
                dto.setLinkedCount(convertToLong(statistics.get("linkedCount")));
            }
        } catch (Exception e) {
            log.error("转换统计Map失败", e);
        }

        return dto;
    }

    /**
     * 获取支付方式名称
     */
    private String getPaymentMethodName(String code) {
        if (code == null) {
            return null;
        }
        Map<String, String> methodMap = new HashMap<>();
        methodMap.put("WECHAT", "微信支付");
        methodMap.put("ALIPAY", "支付宝");
        methodMap.put("CASH", "现金");
        methodMap.put("BANK_CARD", "银行卡");
        methodMap.put("CREDIT_CARD", "信用卡");
        methodMap.put("OTHER", "其他");
        return methodMap.getOrDefault(code, code);
    }

    /**
     * 创建支付方式Map
     */
    private Map<String, Object> createMethod(String code, String name) {
        Map<String, Object> method = new HashMap<>();
        method.put("code", code);
        method.put("name", name);
        return method;
    }

    /**
     * 创建预算项目Map
     */
    private Map<String, Object> createBudgetItem(Long id, String title, String category, BigDecimal amount) {
        Map<String, Object> item = new HashMap<>();
        item.put("budgetId", id);
        item.put("title", title);
        item.put("category", category);
        item.put("budgetAmount", amount);
        return item;
    }

    /**
     * 创建账户Map
     */
    private Map<String, Object> createAccount(Long id, String name) {
        Map<String, Object> account = new HashMap<>();
        account.put("id", id);
        account.put("name", name);
        return account;
    }

    /**
     * 转换为BigDecimal
     */
    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            log.error("转换为BigDecimal失败: {}", value, e);
            return null;
        }
    }

    /**
     * 转换为Long
     */
    private Long convertToLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            log.error("转换为Long失败: {}", value, e);
            return null;
        }
    }

    /**
     * 将支出记录写入CSV格式
     */
    private void writeExpenseToCsv(java.io.PrintWriter writer, List<ExpenseRecord> records) {
        // 写入表头
        writer.println("支出ID,用户ID,账户ID,预算项目ID,金额,货币,支出日期,支付方式,收款方,描述,备注,预算金额,差异金额,差异百分比,创建时间");

        // 写入数据
        for (ExpenseRecord record : records) {
            writer.println(String.format("%d,%d,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    record.getExpenseId(),
                    record.getUserId(),
                    record.getAccountId(),
                    record.getBudgetItemId() != null ? record.getBudgetItemId() : "",
                    record.getAmount(),
                    record.getCurrency(),
                    record.getExpenseDate(),
                    record.getPaymentMethod(),
                    escapeCsvField(record.getPayee()),
                    escapeCsvField(record.getDescription()),
                    escapeCsvField(record.getRemark()),
                    record.getBudgetAmount() != null ? record.getBudgetAmount() : "",
                    record.getDifferenceAmount() != null ? record.getDifferenceAmount() : "",
                    record.getDifferencePercent() != null ? record.getDifferencePercent() : "",
                    record.getCreatedAt()
            ));
        }

        writer.flush();
    }

    /**
     * 转义CSV字段中的特殊字符
     */
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // 如果字段包含逗号、双引号或换行符，需要用双引号括起来，并且双引号需要转义
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }


    /**
     * DTO 转 Entity
     */
    private ExpenseRecord convertToEntity(ExpenseRecordDTO dto) {
        if (dto == null) {
            return null;
        }
        ExpenseRecord entity = new ExpenseRecord();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    /**
     * Entity 转 DTO
     */
    private ExpenseRecordDTO convertToDTO(ExpenseRecord expenseRecord) {
        if (expenseRecord == null) {
            return null;
        }
        ExpenseRecordDTO dto = new ExpenseRecordDTO();
        BeanUtils.copyProperties(expenseRecord, dto);
        return dto;
    }
}