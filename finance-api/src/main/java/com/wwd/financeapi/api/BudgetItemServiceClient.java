package com.wwd.financeapi.api;

import com.wwd.common.constant.ServiceNamesConstant;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.financeapi.dto.budget.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预算管理API接口（Feign客户端）
 */
@FeignClient(
        name = ServiceNamesConstant.FINANCE_SERVICE,
        contextId = "BudgetItemServiceClient",
        path = "/api/finance/budget"
)
public interface BudgetItemServiceClient {

    /**
     * 分页查询预算项目
     */
    @GetMapping("/page")
    Result<PageResult<BudgetItemDTO>> pageBudgetItems(@RequestParam BudgetItemQueryDTO budgetItemQueryDTO);

    /**
     * 查询预算项目
     */
    @GetMapping("/list")
    Result<List<BudgetItemDTO>> listBudgetItems(@RequestParam BudgetItemQueryDTO budgetItemQueryDTO);

    /**
     * 获取预算项目详情
     */
    @GetMapping("/{id}")
    Result<BudgetItemDTO> getBudgetItemById(@PathVariable("id") Long id);

    /**
     * 创建或更新预算项目
     */
    @PostMapping("/operate")
    Result<Long> operateBudgetItem(@RequestBody BudgetItemOperateDTO budgetItemOperateDTO);

    /**
     * 删除预算项目
     */
    @DeleteMapping("/delete/{id}")
    Result<Boolean> deleteBudgetItem(@PathVariable("id") Long id);

    /**
     * 批量删除预算项目
     */
    @DeleteMapping("/batch")
    Result<Boolean> batchDelete(@RequestBody List<Long> ids);

    /**
     * 更新预算项目状态
     */
    @PutMapping("/status")
    Result<Boolean> updateStatus(@RequestParam Long id, @RequestParam String status);

    /**
     * 批量更新预算项目状态
     */
    @PutMapping("/batch/status")
    Result<Boolean> batchUpdateStatus(@RequestBody BatchUpdateStatusDTO dto);

    /**
     * 获取预算分类列表
     */
    @GetMapping("/category/list")
    Result<List<BudgetCategoryDTO>> getCategories();

    /**
     * 获取消费类型列表
     */
    @GetMapping("/consumption/type/list")
    Result<List<ConsumptionTypeDTO>> getConsumptionTypes();

}