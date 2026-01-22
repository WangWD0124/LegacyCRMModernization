package com.wwd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.finance.entity.BudgetItem;
import com.wwd.finance.entity.ExpenseRecord;
import com.wwd.finance.mapper.ExpenseRecordMapper;
import com.wwd.finance.service.ExpenseRecordService;
import com.wwd.financeapi.dto.budget.BudgetItemDTO;
import com.wwd.financeapi.dto.expense.ExpenseQueryDTO;
import com.wwd.financeapi.dto.expense.ExpenseRecordDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 支出流水服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseRecordServiceImpl extends ServiceImpl<ExpenseRecordMapper, ExpenseRecord> implements ExpenseRecordService {

    private final ExpenseRecordMapper expenseRecordMapper;

    @Override
    public IPage<ExpenseRecordDTO> pageExpense(ExpenseQueryDTO expenseQueryDTO) {
        // 设置默认排序
        if (StringUtils.isEmpty(expenseQueryDTO.getOrderBy())){
            expenseQueryDTO.setOrderBy("createdAt");
            expenseQueryDTO.setAsc(false);
        }
        // 设置用户ID
        Long userId = getCurrentUserId();
        expenseQueryDTO.setUserId(userId);

        // 处理分页参数
        Page<ExpenseRecord> page = new Page<>(expenseQueryDTO.getPageNum(), expenseQueryDTO.getPageSize());
        IPage<ExpenseRecord> expenseRecordPage = expenseRecordMapper.selectExpensePage(page, expenseQueryDTO);
        return expenseRecordPage.convert(this::convertToDTO);
    }

    @Override
    public ExpenseRecord getExpenseDetail(Long expenseId) {
        LambdaQueryWrapper<ExpenseRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExpenseRecord::getExpenseId, expenseId)
                .eq(ExpenseRecord::getDeleted, 0)
                .eq(ExpenseRecord::getUserId, getCurrentUserId());
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateExpense(ExpenseRecordDTO dto) {
        ExpenseRecord expenseRecord = new ExpenseRecord();
        BeanUtils.copyProperties(dto, expenseRecord, "expenseId");

        // 设置用户ID
        expenseRecord.setUserId(getCurrentUserId());

        // 计算差异信息
        calculateDifference(expenseRecord);

        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        if (expenseRecord.getExpenseId() == null) {
            // 新增
            expenseRecord.setCreatedAt(now);
            expenseRecord.setUpdatedAt(now);
            this.save(expenseRecord);
        } else {
            // 更新
            expenseRecord.setUpdatedAt(now);
            LambdaQueryWrapper<ExpenseRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ExpenseRecord::getExpenseId, dto.getExpenseId())
                    .eq(ExpenseRecord::getUserId, getCurrentUserId());
            this.update(expenseRecord, queryWrapper);
        }

        return expenseRecord.getExpenseId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteExpense(Long expenseId) {
        LambdaQueryWrapper<ExpenseRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExpenseRecord::getExpenseId, expenseId)
                .eq(ExpenseRecord::getUserId, getCurrentUserId())
                .eq(ExpenseRecord::getDeleted, 0);

        ExpenseRecord expenseRecord = new ExpenseRecord();
        expenseRecord.setDeleted(1);
        expenseRecord.setUpdatedAt(LocalDateTime.now());

        return this.update(expenseRecord, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteExpense(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 验证所有记录都属于当前用户
        LambdaQueryWrapper<ExpenseRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExpenseRecord::getExpenseId, ids)
                .eq(ExpenseRecord::getUserId, getCurrentUserId())
                .eq(ExpenseRecord::getDeleted, 0);

        List<ExpenseRecord> records = this.list(queryWrapper);
        if (records.size() != ids.size()) {
            throw new RuntimeException("部分记录不存在或无权操作");
        }

        // 批量删除
        List<ExpenseRecord> updateList = records.stream()
                .map(record -> {
                    ExpenseRecord updateRecord = new ExpenseRecord();
                    updateRecord.setExpenseId(record.getExpenseId());
                    updateRecord.setDeleted(1);
                    updateRecord.setUpdatedAt(LocalDateTime.now());
                    return updateRecord;
                })
                .collect(Collectors.toList());

        return this.updateBatchById(updateList);
    }

    @Override
    public Map<String, Object> getExpenseStatistics(Map<String, Object> params) {
        Long userId = getCurrentUserId();
        params.put("userId", userId);

        Map<String, Object> statistics = expenseRecordMapper.selectExpenseStatistics(params);

        // 计算本月支出
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        Map<String, Object> monthParams = new HashMap<>();
        monthParams.put("userId", userId);
        monthParams.put("expenseDateStart", firstDayOfMonth);
        monthParams.put("expenseDateEnd", now);

        Map<String, Object> monthStats = expenseRecordMapper.selectExpenseStatistics(monthParams);
        BigDecimal currentMonthExpense = (BigDecimal) monthStats.getOrDefault("totalExpense", BigDecimal.ZERO);

        // 计算关联率
        Long totalCount = (Long) statistics.getOrDefault("totalCount", 0L);
        Long linkedCount = (Long) statistics.getOrDefault("linkedCount", 0L);
        BigDecimal budgetLinkedRate = totalCount > 0
                ? BigDecimal.valueOf(linkedCount).divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        statistics.put("currentMonthExpense", currentMonthExpense);
        statistics.put("budgetLinkedRate", budgetLinkedRate);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getExpenseTrend(Map<String, Object> params) {
        Long userId = getCurrentUserId();
        params.put("userId", userId);

        // 根据period参数设置日期范围和格式化
        String period = (String) params.get("period");
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        String dateFormat;

        switch (period) {
            case "7d":
                startDate = endDate.minusDays(6);
                dateFormat = "%m-%d";
                break;
            case "30d":
                startDate = endDate.minusDays(29);
                dateFormat = "%m-%d";
                break;
            case "90d":
                startDate = endDate.minusDays(89);
                dateFormat = "%Y-%m";
                break;
            case "year":
                startDate = endDate.withDayOfYear(1);
                dateFormat = "%Y-%m";
                break;
            default:
                startDate = endDate.minusDays(29);
                dateFormat = "%m-%d";
        }

        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("dateFormat", dateFormat);

        return expenseRecordMapper.selectExpenseTrend(params);
    }

    @Override
    public Map<String, Object> getDifferenceAnalysis(Map<String, Object> params) {
        Long userId = getCurrentUserId();
        params.put("userId", userId);

        List<Map<String, Object>> analysisData = expenseRecordMapper.selectDifferenceAnalysis(params);

        Map<String, Object> result = new HashMap<>();

        // 计算汇总信息
        BigDecimal totalBudgetAmount = BigDecimal.ZERO;
        BigDecimal totalActualAmount = BigDecimal.ZERO;
        BigDecimal totalDifferenceAmount = BigDecimal.ZERO;
        int overBudgetCount = 0;
        int underBudgetCount = 0;
        BigDecimal overBudgetAmount = BigDecimal.ZERO;
        BigDecimal underBudgetAmount = BigDecimal.ZERO;

        for (Map<String, Object> item : analysisData) {
            BigDecimal budgetAmount = (BigDecimal) item.get("budgetAmount");
            BigDecimal actualAmount = (BigDecimal) item.get("actualAmount");
            BigDecimal differenceAmount = (BigDecimal) item.get("differenceAmount");

            totalBudgetAmount = totalBudgetAmount.add(budgetAmount);
            totalActualAmount = totalActualAmount.add(actualAmount);
            totalDifferenceAmount = totalDifferenceAmount.add(differenceAmount);

            if (differenceAmount.compareTo(BigDecimal.ZERO) < 0) {
                overBudgetCount++;
                overBudgetAmount = overBudgetAmount.add(differenceAmount.abs());
            } else if (differenceAmount.compareTo(BigDecimal.ZERO) > 0) {
                underBudgetCount++;
                underBudgetAmount = underBudgetAmount.add(differenceAmount);
            }
        }

        // 预算执行率
        BigDecimal budgetExecutionRate = totalBudgetAmount.compareTo(BigDecimal.ZERO) > 0
                ? totalActualAmount.divide(totalBudgetAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        // 差异分布（按百分比区间分组）
        int[] distribution = new int[6]; // 严重超支、中等超支、轻微超支、轻微结余、中等结余、严重结余
        for (Map<String, Object> item : analysisData) {
            BigDecimal differencePercent = (BigDecimal) item.get("differencePercent");
            double percent = differencePercent != null ? differencePercent.doubleValue() : 0;

            if (percent < -50) {
                distribution[0]++; // 严重超支
            } else if (percent < -20) {
                distribution[1]++; // 中等超支
            } else if (percent < 0) {
                distribution[2]++; // 轻微超支
            } else if (percent <= 20) {
                distribution[3]++; // 轻微结余
            } else if (percent <= 50) {
                distribution[4]++; // 中等结余
            } else {
                distribution[5]++; // 严重结余
            }
        }

        // 预算执行情况分布
        List<Map<String, Object>> executionSummary = Arrays.asList(
                createSummaryItem("超支项目", overBudgetCount, "#f56c6c"),
                createSummaryItem("结余项目", underBudgetCount, "#67c23a"),
                createSummaryItem("刚好预算", analysisData.size() - overBudgetCount - underBudgetCount, "#409eff")
        );

        result.put("details", analysisData);
        //result.put("summary", Map.of(
//                "totalBudgetAmount", totalBudgetAmount,
//                "totalActualAmount", totalActualAmount,
//                "totalDifferenceAmount", totalDifferenceAmount,
//                "overBudgetCount", overBudgetCount,
//                "underBudgetCount", underBudgetCount,
//                "overBudgetAmount", overBudgetAmount,
//                "underBudgetAmount", underBudgetAmount,
//                "budgetExecutionRate", budgetExecutionRate
//        ));
        result.put("differenceDistribution", distribution);
        result.put("executionSummary", executionSummary);

        return result;
    }

    @Override
    public List<ExpenseRecord> exportExpense(ExpenseQueryDTO expenseQueryDTO) {
        Long userId = getCurrentUserId();
        expenseQueryDTO.setUserId(userId);

        // 查询所有符合条件的记录（不分页）
        Page<ExpenseRecord> page = new Page<>(1, 10000); // 最大导出10000条
        IPage<ExpenseRecord> result = expenseRecordMapper.selectExpensePage(page, expenseQueryDTO);

        return result.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean linkBudgetItem(Long expenseId, Long budgetItemId, BigDecimal budgetAmount) {
        ExpenseRecord expenseRecord = this.getById(expenseId);
        if (expenseRecord == null || !expenseRecord.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("支出记录不存在或无权操作");
        }

        expenseRecord.setBudgetItemId(budgetItemId);
        expenseRecord.setBudgetAmount(budgetAmount);
        calculateDifference(expenseRecord);
        expenseRecord.setUpdatedAt(LocalDateTime.now());

        return this.updateById(expenseRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchLinkBudgetItem(List<Long> expenseIds, Long budgetItemId, BigDecimal budgetAmount) {
        if (expenseIds == null || expenseIds.isEmpty()) {
            return false;
        }

        // 验证所有记录都属于当前用户
        LambdaQueryWrapper<ExpenseRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExpenseRecord::getExpenseId, expenseIds)
                .eq(ExpenseRecord::getUserId, getCurrentUserId())
                .eq(ExpenseRecord::getDeleted, 0);

        List<ExpenseRecord> records = this.list(queryWrapper);
        if (records.size() != expenseIds.size()) {
            throw new RuntimeException("部分记录不存在或无权操作");
        }

        return expenseRecordMapper.batchUpdateBudgetLink(expenseIds, budgetItemId, budgetAmount) > 0;
    }

    @Override
    public Long getCurrentUserId() {
        // TODO: 从安全上下文中获取当前用户ID
        // 这里暂时返回一个模拟的ID
        return 1L;
    }

    /**
     * 计算差异信息
     */
    private void calculateDifference(ExpenseRecord expenseRecord) {
        if (expenseRecord.getBudgetAmount() != null && expenseRecord.getAmount() != null) {
            BigDecimal difference = expenseRecord.getBudgetAmount().subtract(expenseRecord.getAmount());
            expenseRecord.setDifferenceAmount(difference);

            if (expenseRecord.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percent = difference.divide(expenseRecord.getBudgetAmount(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                expenseRecord.setDifferencePercent(percent);
            } else {
                expenseRecord.setDifferencePercent(BigDecimal.ZERO);
            }
        } else {
            expenseRecord.setDifferenceAmount(null);
            expenseRecord.setDifferencePercent(null);
        }
    }

    /**
     * 将前端字段名转换为数据库列名
     */
    private String convertToColumnName(String fieldName) {
        switch (fieldName) {
            case "expenseId":
                return "er.expense_id";
            case "expenseDate":
                return "er.expense_date";
            case "createdAt":
                return "er.created_at";
            case "updatedAt":
                return "er.updated_at";
            case "paymentMethod":
                return "er.payment_method";
            case "budgetItemId":
                return "er.budget_item_id";
            case "accountId":
                return "er.account_id";
            case "differenceAmount":
                return "er.difference_amount";
            case "differencePercent":
                return "er.difference_percent";
            default:
                return fieldName;
        }
    }

    /**
     * 创建汇总项
     */
    private Map<String, Object> createSummaryItem(String name, int value, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        //item.put("itemStyle", Map.of("color", color));
        return item;
    }

    /**
     * 转换为DTO
     */
    private ExpenseRecordDTO convertToDTO(ExpenseRecord entity) {
        if (entity == null) {
            return null;
        }

        ExpenseRecordDTO dto = new ExpenseRecordDTO();
        BeanUtils.copyProperties(entity, dto);

        return dto;
    }
}