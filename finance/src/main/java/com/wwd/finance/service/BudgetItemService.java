package com.wwd.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.finance.entity.BudgetItem;
import com.wwd.financeapi.dto.budget.BudgetItemDTO;
import com.wwd.financeapi.dto.budget.BudgetItemOperateDTO;
import com.wwd.financeapi.dto.budget.BudgetItemQueryDTO;

import java.util.List;

/**
 * 预算项目服务接口
 */
public interface BudgetItemService {

    /**
     * 分页查询预算项目
     */
    IPage<BudgetItemDTO> pageBudgetItems(BudgetItemQueryDTO budgetItemQueryDTO);

    /**
     * 分页查询预算项目
     */
    List<BudgetItemDTO> listBudgetItems(BudgetItemQueryDTO budgetItemQueryDTO);

    /**
     * 获取预算项目详情
     */
    BudgetItemDTO getBudgetItemById(Long budgetId);

    /**
     * 创建预算项目
     */
    Long create(BudgetItemOperateDTO dto);

    /**
     * 更新预算项目
     */
    Boolean update(BudgetItemOperateDTO dto);

    /**
     * 删除预算项目
     */
    Boolean delete(Long budgetId);

    /**
     * 批量删除预算项目
     */
    Boolean batchDelete(List<Long> ids);

    /**
     * 更新预算项目状态
     */
    Boolean updateStatus(Long budgetId, String status);

    /**
     * 批量更新预算项目状态
     */
    Boolean batchUpdateStatus(List<Long> ids, String status);

    /**
     * 导出预算数据
     */
    byte[] export(BudgetItemQueryDTO query);
}