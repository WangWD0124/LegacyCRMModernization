package com.wwd.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.finance.entity.BudgetItem;
import com.wwd.finance.service.BudgetItemService;
import com.wwd.finance.service.BudgetStatisticService;
import com.wwd.financeapi.api.BudgetItemServiceClient;
import com.wwd.financeapi.dto.budget.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * 预算项目管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/finance/budget")
@Api(tags = "预算管理")
public class BudgetItemController implements BudgetItemServiceClient {

    @Autowired
    private BudgetItemService budgetItemService;

    @Autowired
    private BudgetStatisticService budgetStatisticService;

    @Override
    @ApiOperation("分页查询预算项目")
    @GetMapping("/page")
    public Result<PageResult<BudgetItemDTO>> pageBudgetItems(@Valid BudgetItemQueryDTO budgetItemQueryDTO) {
        try {
            IPage<BudgetItemDTO> page = budgetItemService.pageBudgetItems(budgetItemQueryDTO);
            PageResult pageResult = new PageResult(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询预算项目失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result<List<BudgetItemDTO>> listBudgetItems(BudgetItemQueryDTO budgetItemQueryDTO) {

        List<BudgetItemDTO> list = budgetItemService.listBudgetItems(budgetItemQueryDTO);
        return Result.success(list);
    }

    @Override
    @ApiOperation("获取预算项目详情")
    @GetMapping("/{id}")
    public Result<BudgetItemDTO> getBudgetItemById(@PathVariable Long id) {
        try {
            BudgetItemDTO detail = budgetItemService.getBudgetItemById(id);
            if (detail == null) {
                return Result.error("预算项目不存在");
            }
            return Result.success(detail);
        } catch (Exception e) {
            log.error("获取预算项目详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @ApiOperation("创建或更新预算项目")
    @PostMapping("/operate")
    public Result<Long> operateBudgetItem(@RequestBody @Valid BudgetItemOperateDTO budgetItemOperateDTO) {
        try {
            Long budgetId;
            if (budgetItemOperateDTO.getBudgetId() != null) {
                budgetItemService.update(budgetItemOperateDTO);
                budgetId = budgetItemOperateDTO.getBudgetId();
            } else {
                budgetId = budgetItemService.create(budgetItemOperateDTO);
            }
            return Result.success(budgetId);
        } catch (Exception e) {
            log.error("操作预算项目失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @ApiOperation("删除预算项目")
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> deleteBudgetItem(@PathVariable Long id) {
        try {
            boolean result = budgetItemService.delete(id);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除预算项目失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @ApiOperation("批量删除预算项目")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        try {
            boolean result = budgetItemService.batchDelete(ids);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量删除预算项目失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @ApiOperation("更新预算项目状态")
    @PutMapping("/status")
    public Result<Boolean> updateStatus(@RequestParam Long id,@RequestParam String status) {
        try {
            boolean result = budgetItemService.updateStatus(id, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新预算项目状态失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @ApiOperation("批量更新预算项目状态")
    @PutMapping("/batch/status")
    public Result<Boolean> batchUpdateStatus(@RequestBody BatchUpdateStatusDTO dto) {
        try {
            boolean result = budgetItemService.batchUpdateStatus(dto.getIds(), dto.getStatus());
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量更新预算项目状态失败", e);
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("导出预算数据")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData(BudgetItemQueryDTO query,
                                             HttpServletResponse response) {
        try {
            byte[] data = budgetItemService.export(query);

            String fileName = "budgets_" + System.currentTimeMillis() + ".xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("导出预算数据失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation("获取预算统计信息")
    @GetMapping("/statistics")
    public ResponseEntity<Result<BudgetStatisticDTO>> getStatistics(BudgetItemQueryDTO query) {
        try {
            BudgetStatisticDTO statistics = budgetStatisticService.getStatistics(query);
            return ResponseEntity.ok(Result.success(statistics));
        } catch (Exception e) {
            log.error("获取预算统计信息失败", e);
            return ResponseEntity.ok(Result.error(e.getMessage()));
        }
    }

    @Override
    @ApiOperation("获取预算分类列表")
    @GetMapping("/category/list")
    public Result<List<BudgetCategoryDTO>> getCategories() {
        try {
            // TODO: 实现获取预算分类列表
            return Result.success(Collections.emptyList());
        } catch (Exception e) {
            log.error("获取预算分类列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @ApiOperation("获取消费类型列表")
    @GetMapping("/consumption/type/list")
    public Result<List<ConsumptionTypeDTO>> getConsumptionTypes() {
        try {
            // TODO: 实现获取消费类型列表
            return Result.success(Collections.emptyList());
        } catch (Exception e) {
            log.error("获取消费类型列表失败", e);
            return Result.error(e.getMessage());
        }
    }



    /**
     * Entity 转 DTO
     */
    private BudgetItemDTO convertToDTO(BudgetItem budgetItem) {
        if (budgetItem == null) {
            return null;
        }
        BudgetItemDTO dto = new BudgetItemDTO();
        BeanUtils.copyProperties(budgetItem, dto);
        return dto;
    }
}