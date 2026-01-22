// finance/src/main/java/com/wwd/finance/controller/IncomeRecordController.java
package com.wwd.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.finance.entity.IncomeRecord;
import com.wwd.finance.service.IncomeRecordService;
import com.wwd.financeapi.api.IncomeRecordServiceClient;
import com.wwd.financeapi.dto.IncomeRecordDTO;
import com.wwd.financeapi.dto.IncomeQueryDTO;
import com.wwd.financeapi.dto.IncomeStatisticsDTO;
import com.wwd.financeapi.dto.IncomeTrendQueryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收入流水控制器 - 实现Feign接口
 */
@Slf4j
@RestController
@RequestMapping("/api/finance/income")
@RequiredArgsConstructor
@Tag(name = "收入管理", description = "收入流水管理接口")
public class IncomeRecordController implements IncomeRecordServiceClient {

    private final IncomeRecordService incomeRecordService;

    @Override
    @Operation(summary = "分页查询收入流水")
    @GetMapping("/page")
    public Result<PageResult<IncomeRecordDTO>> pageIncomeRecord(IncomeQueryDTO incomeQueryDTO) {
        try {
            IPage<IncomeRecordDTO> page = incomeRecordService.pageIncome(incomeQueryDTO);
            PageResult pageResult = new PageResult(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询收入流水失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取收入详情")
    @GetMapping("/{id}")
    public Result<IncomeRecordDTO> getIncomeDetail(@PathVariable Long id) {
        try {
            IncomeRecord incomeRecord = incomeRecordService.getIncomeDetail(id);
            if (incomeRecord == null) {
                return Result.error("收入记录不存在");
            }
            IncomeRecordDTO incomeRecordDTO = new IncomeRecordDTO();
            BeanUtils.copyProperties(incomeRecord, incomeRecordDTO);
            return Result.success(incomeRecordDTO);
        } catch (Exception e) {
            log.error("获取收入详情失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "创建或更新收入流水")
    @PostMapping("/operate")
    public Result<Long> saveOrUpdateIncome(@RequestBody IncomeRecordDTO dto) {
        try {
            Long incomeId = incomeRecordService.saveOrUpdateIncome(dto);
            return Result.success(incomeId);
        } catch (Exception e) {
            log.error("保存收入流水失败", e);
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "删除收入流水")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteIncome(@PathVariable Long id) {
        try {
            boolean success = incomeRecordService.deleteIncome(id);
            return success ? Result.success(true) : Result.error("删除失败");
        } catch (Exception e) {
            log.error("删除收入流水失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "批量删除收入流水")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteIncome(@RequestBody List<Long> ids) {
        try {
            boolean success = incomeRecordService.batchDeleteIncome(ids);
            return success ? Result.success(true) : Result.error("批量删除失败");
        } catch (Exception e) {
            log.error("批量删除收入流水失败", e);
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "更新结算状态")
    @PutMapping("/settle")
    public Result<Boolean> updateSettleStatus(
            @RequestParam Long id,
            @RequestParam String isSettled) {
        try {
            boolean success = incomeRecordService.updateSettleStatus(id, isSettled);
            return success ? Result.success(true) : Result.error("更新失败");
        } catch (Exception e) {
            log.error("更新结算状态失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "批量更新结算状态")
    @PutMapping("/batch/settle")
    public Result<Boolean> batchUpdateSettleStatus(
            @RequestBody List<Long> ids,
            @RequestParam String isSettled) {
        try {
            boolean success = incomeRecordService.batchUpdateSettleStatus(ids, isSettled);
            return success ? Result.success(true) : Result.error("批量更新失败");
        } catch (Exception e) {
            log.error("批量更新结算状态失败", e);
            return Result.error("批量更新失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取收入统计信息")
    @GetMapping("/statistics")
    public Result<IncomeStatisticsDTO> getIncomeStatistics(IncomeQueryDTO queryDTO) {
        try {
            Map<String, Object> params = convertQueryDTOToMap(queryDTO);
            Map<String, Object> statistics = incomeRecordService.getIncomeStatistics(params);

            IncomeStatisticsDTO result = new IncomeStatisticsDTO();
            // 使用BeanUtils复制属性
            BeanUtils.copyProperties(convertStatisticsMap(statistics), result);

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取收入统计信息失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取收入趋势数据")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> getIncomeTrend(IncomeTrendQueryDTO queryDTO) {
        try {
            Map<String, Object> params = new HashMap<>();
            BeanUtils.copyProperties(queryDTO, params);

            List<Map<String, Object>> trendData = incomeRecordService.getIncomeTrend(params);
            return Result.success(trendData);
        } catch (Exception e) {
            log.error("获取收入趋势数据失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取收入来源分类选项")
    @GetMapping("/source-category/list")
    public Result<List<Map<String, Object>>> getSourceCategories() {
        try {
            // 这里调用其他服务或查询数据库获取分类
            // 暂时返回模拟数据
            List<Map<String, Object>> categories = Arrays.asList(
                    createCategory("SALARY", "工资", false),
                    createCategory("BONUS", "奖金", false),
                    createCategory("INVESTMENT", "投资收益", false),
                    createCategory("PART_TIME", "兼职收入", true),
                    createCategory("FREELANCE", "自由职业", true),
                    createCategory("GIFT", "礼金红包", false),
                    createCategory("OTHER", "其他收入", false)
            );
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取收入来源分类失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取基金账户选项")
    @GetMapping("/fund/account/list")
    public Result<List<Map<String, Object>>> getFundAccounts() {
        try {
            // 这里调用基金服务或查询数据库获取账户
            // 暂时返回模拟数据
            List<Map<String, Object>> accounts = Arrays.asList(
                    createAccount(1L, "工资卡", true),
                    createAccount(2L, "储蓄卡", true),
                    createAccount(3L, "支付宝", false),
                    createAccount(4L, "微信钱包", false),
                    createAccount(5L, "投资账户", true)
            );
            return Result.success(accounts);
        } catch (Exception e) {
            log.error("获取基金账户失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 导出收入数据（不在Feign接口中，但保留原功能）
     */
    @Operation(summary = "导出收入数据")
    @GetMapping("/export")
    public void exportIncome(IncomeQueryDTO incomeQueryDTO,
            HttpServletResponse response) {
        try {
            List<IncomeRecord> incomeRecords = incomeRecordService.exportIncome(incomeQueryDTO);

            // 设置响应头
            String fileName = "income_export_" + System.currentTimeMillis() + ".csv";
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            // 写入CSV文件
            writeIncomeToCsv(response.getWriter(), incomeRecords);

        } catch (Exception e) {
            log.error("导出收入数据失败", e);
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
    private Map<String, Object> convertQueryDTOToMap(IncomeQueryDTO dto) {
        Map<String, Object> params = new HashMap<>();
        try {
            // 使用反射获取所有字段
            Field[] fields = IncomeQueryDTO.class.getDeclaredFields();
            for (Field field : fields) {
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
     * 转换统计Map为对象
     */
    private IncomeStatisticsDTO convertStatisticsMap(Map<String, Object> statistics) {
        IncomeStatisticsDTO dto = new IncomeStatisticsDTO();

        try {
            if (statistics != null) {
                // 手动设置每个字段
                if (statistics.containsKey("totalIncome")) {
                    dto.setTotalIncome(convertToBigDecimal(statistics.get("totalIncome")));
                }
                if (statistics.containsKey("settledIncome")) {
                    dto.setSettledIncome(convertToBigDecimal(statistics.get("settledIncome")));
                }
                if (statistics.containsKey("unsettledIncome")) {
                    dto.setUnsettledIncome(convertToBigDecimal(statistics.get("unsettledIncome")));
                }
                if (statistics.containsKey("currentMonthIncome")) {
                    dto.setCurrentMonthIncome(convertToBigDecimal(statistics.get("currentMonthIncome")));
                }
                if (statistics.containsKey("totalCount")) {
                    dto.setTotalCount(convertToLong(statistics.get("totalCount")));
                }
                if (statistics.containsKey("settledCount")) {
                    dto.setSettledCount(convertToLong(statistics.get("settledCount")));
                }
                if (statistics.containsKey("unsettledCount")) {
                    dto.setUnsettledCount(convertToLong(statistics.get("unsettledCount")));
                }
                if (statistics.containsKey("avgHourlyRate")) {
                    dto.setAvgHourlyRate(convertToBigDecimal(statistics.get("avgHourlyRate")));
                }

                // 计算平均收入
                if (dto.getTotalCount() != null && dto.getTotalCount() > 0
                        && dto.getTotalIncome() != null) {
                    BigDecimal avgIncome = dto.getTotalIncome()
                            .divide(BigDecimal.valueOf(dto.getTotalCount()), 2, BigDecimal.ROUND_HALF_UP);
                    dto.setAvgIncome(avgIncome);
                }
            }
        } catch (Exception e) {
            log.error("转换统计Map失败", e);
        }

        return dto;
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
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            log.error("转换为Long失败: {}", value, e);
            return null;
        }
    }

    /**
     * 获取收入来源类型名称
     */
    private String getSourceTypeName(String code) {
        if (code == null) return null;
        Map<String, String> sourceTypeMap = new HashMap<>();
        sourceTypeMap.put("SALARY", "工资");
        sourceTypeMap.put("BONUS", "奖金");
        sourceTypeMap.put("INVESTMENT", "投资收益");
        sourceTypeMap.put("PART_TIME", "兼职收入");
        sourceTypeMap.put("FREELANCE", "自由职业");
        sourceTypeMap.put("GIFT", "礼金红包");
        sourceTypeMap.put("OTHER", "其他收入");
        return sourceTypeMap.getOrDefault(code, code);
    }

    /**
     * 创建分类Map
     */
    private Map<String, Object> createCategory(String code, String name, boolean requiresWorkHours) {
        Map<String, Object> category = new HashMap<>();
        category.put("code", code);
        category.put("name", name);
        category.put("requiresWorkHours", requiresWorkHours);
        return category;
    }

    /**
     * 创建账户Map
     */
    private Map<String, Object> createAccount(Long id, String name, boolean supportsAllocation) {
        Map<String, Object> account = new HashMap<>();
        account.put("id", id);
        account.put("name", name);
        account.put("supportsAllocation", supportsAllocation);
        return account;
    }

    /**
     * 将收入记录写入CSV格式
     */
    private void writeIncomeToCsv(java.io.PrintWriter writer, List<IncomeRecord> records) {
        // 写入表头
        writer.println("收入ID,用户ID,账户ID,金额,货币,收入日期,来源类型,来源详情,工作时长,描述,备注,结算状态,创建时间");

        // 写入数据
        for (IncomeRecord record : records) {
            writer.println(String.format("%d,%d,%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    record.getIncomeId(),
                    record.getUserId(),
                    record.getAccountId(),
                    record.getAmount(),
                    record.getCurrency(),
                    record.getIncomeDate(),
                    record.getSourceTypeCode(),
                    escapeCsvField(record.getSourceDetail()),
                    record.getWorkHours(),
                    escapeCsvField(record.getDescription()),
                    escapeCsvField(record.getRemark()),
                    record.getIsSettled(),
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
}