package com.wwd.finance.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.finance.context.UserContext;
import com.wwd.finance.entity.BudgetItem;
import com.wwd.finance.mapper.BudgetItemMapper;
import com.wwd.finance.service.BudgetItemService;
import com.wwd.financeapi.dto.budget.BudgetItemDTO;
import com.wwd.financeapi.dto.budget.BudgetItemOperateDTO;
import com.wwd.financeapi.dto.budget.BudgetItemQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预算项目服务实现
 */
@Slf4j
@Service
public class BudgetItemServiceImpl extends ServiceImpl<BudgetItemMapper, BudgetItem> implements BudgetItemService {

    @Resource
    private BudgetItemMapper budgetItemMapper;

    @Override
    public IPage<BudgetItemDTO> pageBudgetItems(BudgetItemQueryDTO budgetItemQueryDTO) {
        // 设置默认排序
        if (StringUtils.isBlank(budgetItemQueryDTO.getOrderBy())) {
            budgetItemQueryDTO.setOrderBy("createdAt");
            budgetItemQueryDTO.setAsc(false);
        }

        //当前用户
        budgetItemQueryDTO.setUserId(UserContext.getCurrentUserId());

        // 构建分页参数
        Page<BudgetItem> page = new Page<>(budgetItemQueryDTO.getPageNum(), budgetItemQueryDTO.getPageSize());

        // 执行分页查询
        IPage<BudgetItemDTO> budgetItemDTOIPage = budgetItemMapper.selectPageWithInfo(page, budgetItemQueryDTO);

        // 转换为DTO
        return budgetItemDTOIPage;
    }

    @Override
    public List<BudgetItemDTO> listBudgetItems(BudgetItemQueryDTO budgetItemQueryDTO) {
        // 设置默认排序
        if (StringUtils.isBlank(budgetItemQueryDTO.getOrderBy())) {
            budgetItemQueryDTO.setOrderBy("createdAt");
            budgetItemQueryDTO.setAsc(false);
        }

        //当前用户
        budgetItemQueryDTO.setUserId(UserContext.getCurrentUserId());

        // 执行分页查询
        List<BudgetItem> list = budgetItemMapper.selectListWithInfo(budgetItemQueryDTO);

        // 转换为DTO
        return list.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public BudgetItemDTO getBudgetItemById(Long budgetId) {
        BudgetItem entity = budgetItemMapper.selectById(budgetId);
        if (entity == null || entity.getIsDeleted() == 1) {
            return null;
        }
        return convertToDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(BudgetItemOperateDTO dto) {
        // 验证数据
        validateBudgetItem(dto);

        // 转换为实体
        BudgetItem entity = new BudgetItem();
        BeanUtils.copyProperties(dto, entity);

        // 设置默认值
        entity.setUserId(getCurrentUserId()); // 获取当前用户ID
        entity.setIsDeleted(0);
        entity.setVersion(0);

        // 计算实际决策时间
        if (entity.getDecisionStartTime() != null && entity.getDecisionEndTime() != null) {
            long minutes = java.time.Duration.between(
                    entity.getDecisionStartTime(),
                    entity.getDecisionEndTime()
            ).toMinutes();
            entity.setActualDecisionMinutes((int) minutes);
        }

        // 保存到数据库
        this.save(entity);

        log.info("创建预算项目成功: budgetId={}, title={}", entity.getBudgetId(), entity.getTitle());
        return entity.getBudgetId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(BudgetItemOperateDTO dto) {
        // 验证数据
        if (dto.getBudgetId() == null) {
            throw new IllegalArgumentException("预算ID不能为空");
        }

        // 查询现有记录
        BudgetItem entity = baseMapper.selectById(dto.getBudgetId());
        if (entity == null) {
            throw new RuntimeException("预算项目不存在");
        }

        // 更新字段
        if (StringUtils.isNotBlank(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getFundAccountId() != null) {
            entity.setFundAccountId(dto.getFundAccountId());
        }
        if (StringUtils.isNotBlank(dto.getCategoryCode())) {
            entity.setCategoryCode(dto.getCategoryCode());
        }
        if (StringUtils.isNotBlank(dto.getConsumptionType())) {
            entity.setConsumptionType(dto.getConsumptionType());
        }
        if (dto.getBudgetAmount() != null) {
            entity.setBudgetAmount(dto.getBudgetAmount());
        }
        if (dto.getActualAmount() != null) {
            entity.setActualAmount(dto.getActualAmount());
        }
        if (StringUtils.isNotBlank(dto.getCurrency())) {
            entity.setCurrency(dto.getCurrency());
        }
        if (StringUtils.isNotBlank(dto.getStatus())) {
            entity.setStatus(dto.getStatus());
        }

        // 更新其他字段...

        // 计算实际决策时间
        if (entity.getDecisionStartTime() != null && entity.getDecisionEndTime() != null) {
            long minutes = java.time.Duration.between(
                    entity.getDecisionStartTime(),
                    entity.getDecisionEndTime()
            ).toMinutes();
            entity.setActualDecisionMinutes((int) minutes);
        }

        // 更新数据库
        boolean result = this.updateById(entity);

        if (result) {
            log.info("更新预算项目成功: budgetId={}", dto.getBudgetId());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long budgetId) {
        BudgetItem entity = baseMapper.selectById(budgetId);
        if (entity == null) {
            throw new RuntimeException("预算项目不存在");
        }

        // 逻辑删除
        entity.setIsDeleted(1);
        boolean result = this.updateById(entity);

        if (result) {
            log.info("删除预算项目成功: budgetId={}", budgetId);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDelete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("ID列表不能为空");
        }

        int affectedRows = budgetItemMapper.batchDelete(ids);
        log.info("批量删除预算项目成功: ids={}, affectedRows={}", ids, affectedRows);

        return affectedRows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(Long budgetId, String status) {
        validateStatus(status);

        int affectedRows = budgetItemMapper.updateStatus(budgetId, status);
        log.info("更新预算项目状态成功: budgetId={}, status={}", budgetId, status);

        return affectedRows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateStatus(List<Long> ids, String status) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new IllegalArgumentException("ID列表不能为空");
        }
        validateStatus(status);

        int affectedRows = budgetItemMapper.batchUpdateStatus(ids, status);
        log.info("批量更新预算项目状态成功: ids={}, status={}, affectedRows={}", ids, status, affectedRows);

        return affectedRows > 0;
    }

    @Override
    public byte[] export(BudgetItemQueryDTO query) {
        // TODO: 实现导出逻辑
        // 可以使用POI或者EasyExcel导出Excel
        log.info("导出预算数据: query={}", query);
        return new byte[0];
    }

    /**
     * 验证预算项目数据
     */
    private void validateBudgetItem(BudgetItemOperateDTO dto) {
        if (StringUtils.isBlank(dto.getTitle())) {
            throw new IllegalArgumentException("项目标题不能为空");
        }
        if (dto.getBudgetAmount() == null || dto.getBudgetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("预算金额必须大于0");
        }
        if (StringUtils.isBlank(dto.getCurrency())) {
            dto.setCurrency("CNY");
        }
    }

    /**
     * 验证状态值
     */
    private void validateStatus(String status) {
        if (!Arrays.asList(0, 1, 2, 3, 4).contains(status)) {
            throw new IllegalArgumentException("无效的状态值");
        }
    }

    /**
     * 转换为DTO
     */
    private BudgetItemDTO convertToDTO(BudgetItem entity) {
        if (entity == null) {
            return null;
        }

        BudgetItemDTO dto = new BudgetItemDTO();
        BeanUtils.copyProperties(entity, dto);

        // 设置状态名称
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("0", "待决策");
        statusMap.put("1", "决策中");
        statusMap.put("2", "已批准");
        statusMap.put("3", "已执行");
        statusMap.put("4", "已取消");
        dto.setStatusName(statusMap.getOrDefault(entity.getStatus(), entity.getStatus()));

        return dto;
    }

    /**
     * 获取当前用户ID（需要根据实际情况实现）
     */
    private Long getCurrentUserId() {
        return UserContext.getCurrentUserId();
    }
}