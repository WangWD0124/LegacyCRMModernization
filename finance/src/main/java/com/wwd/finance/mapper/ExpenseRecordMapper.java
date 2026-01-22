// finance/src/main/java/com/wwd/finance/mapper/ExpenseRecordMapper.java
package com.wwd.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wwd.finance.entity.ExpenseRecord;
import com.wwd.financeapi.dto.expense.ExpenseQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 支出流水Mapper接口
 */
@Mapper
public interface ExpenseRecordMapper extends BaseMapper<ExpenseRecord> {

    /**
     * 分页查询支出流水
     */
    IPage<ExpenseRecord> selectExpensePage(Page<ExpenseRecord> page, ExpenseQueryDTO expenseQueryDTO);

    /**
     * 统计支出数据
     */
    Map<String, Object> selectExpenseStatistics(@Param("params") Map<String, Object> params);

    /**
     * 获取支出趋势数据
     */
    List<Map<String, Object>> selectExpenseTrend(@Param("params") Map<String, Object> params);

    /**
     * 获取差异分析数据
     */
    List<Map<String, Object>> selectDifferenceAnalysis(@Param("params") Map<String, Object> params);

    /**
     * 根据时间段统计支出
     */
    List<Map<String, Object>> selectExpenseByDateRange(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    /**
     * 批量更新预算关联信息
     */
    int batchUpdateBudgetLink(@Param("expenseIds") List<Long> expenseIds,
                              @Param("budgetItemId") Long budgetItemId,
                              @Param("budgetAmount") BigDecimal budgetAmount);
}