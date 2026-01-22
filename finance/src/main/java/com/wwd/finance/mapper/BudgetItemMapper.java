package com.wwd.finance.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wwd.financeapi.dto.budget.BudgetItemQueryDTO;
import com.wwd.finance.entity.BudgetItem;
import com.wwd.financeapi.dto.budget.BudgetStatisticDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预算项目Mapper接口
 */
@Mapper
public interface BudgetItemMapper extends BaseMapper<BudgetItem> {

    /**
     * 分页查询预算项目（带关联查询）
     */
    IPage<BudgetItem> selectPageWithInfo(Page<BudgetItem> page, @Param("query") BudgetItemQueryDTO query);

    /**
     * 列表
     * @param budgetItemQueryDTO
     * @return
     */
    List<BudgetItem> selectListWithInfo(BudgetItemQueryDTO budgetItemQueryDTO);

    /**
     * 获取预算统计信息
     */
    BudgetStatisticDTO selectStatistics(@Param("userId") Long userId, @Param("query") BudgetItemQueryDTO query);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("ids") java.util.List<Long> ids, @Param("status") String status);

    /**
     * 批量删除
     */
    int batchDelete(@Param("ids") java.util.List<Long> ids);

    /**
     * 更新预算项目状态
     */
    int updateStatus(@Param("budgetId") Long budgetId, @Param("status") String status);
}