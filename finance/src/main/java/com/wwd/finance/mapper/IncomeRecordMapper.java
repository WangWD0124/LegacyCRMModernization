// finance/src/main/java/com/wwd/finance/mapper/IncomeRecordMapper.java
package com.wwd.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wwd.finance.entity.IncomeRecord;
import com.wwd.financeapi.dto.IncomeQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 收入流水Mapper接口
 */
@Mapper
public interface IncomeRecordMapper extends BaseMapper<IncomeRecord> {

    /**
     * 分页查询收入流水
     */
    IPage<IncomeRecord> selectIncomePage(Page<IncomeRecord> page, IncomeQueryDTO params);

    /**
     * 统计收入数据
     */
    Map<String, Object> selectIncomeStatistics(@Param("params") Map<String, Object> params);

    /**
     * 获取收入趋势数据
     */
    List<Map<String, Object>> selectIncomeTrend(@Param("params") Map<String, Object> params);

    /**
     * 更新结算状态
     */
    int updateSettleStatus(@Param("incomeId") Long incomeId, @Param("isSettled") String isSettled);

    /**
     * 批量更新结算状态
     */
    int batchUpdateSettleStatus(@Param("ids") List<Long> ids, @Param("isSettled") String isSettled);

    /**
     * 根据时间段统计收入
     */
    List<Map<String, Object>> selectIncomeByDateRange(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);
}