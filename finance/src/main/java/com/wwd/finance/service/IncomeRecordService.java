// finance/src/main/java/com/wwd/finance/service/IncomeRecordService.java
package com.wwd.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wwd.finance.entity.IncomeRecord;
import com.wwd.financeapi.dto.IncomeQueryDTO;
import com.wwd.financeapi.dto.IncomeRecordDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 收入流水服务接口
 */
public interface IncomeRecordService extends IService<IncomeRecord> {

    /**
     * 分页查询收入流水
     */
    IPage<IncomeRecordDTO> pageIncome(IncomeQueryDTO incomeQueryDTO);

    /**
     * 获取收入详情
     */
    IncomeRecord getIncomeDetail(Long incomeId);

    /**
     * 创建或更新收入流水
     */
    Long saveOrUpdateIncome(IncomeRecordDTO dto);

    /**
     * 删除收入流水
     */
    boolean deleteIncome(Long incomeId);

    /**
     * 批量删除收入流水
     */
    boolean batchDeleteIncome(List<Long> ids);

    /**
     * 更新结算状态
     */
    boolean updateSettleStatus(Long incomeId, String isSettled);

    /**
     * 批量更新结算状态
     */
    boolean batchUpdateSettleStatus(List<Long> ids, String isSettled);

    /**
     * 获取收入统计信息
     */
    Map<String, Object> getIncomeStatistics(Map<String, Object> params);

    /**
     * 获取收入趋势数据
     */
    List<Map<String, Object>> getIncomeTrend(Map<String, Object> params);

    /**
     * 导出收入数据
     */
    List<IncomeRecord> exportIncome(IncomeQueryDTO incomeQueryDTO);

    /**
     * 获取当前用户ID
     */
    Long getCurrentUserId();
}