// finance/src/main/java/com/wwd/finance/service/impl/IncomeRecordServiceImpl.java
package com.wwd.finance.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwd.customerapi.api.FundAccountServiceClient;
import com.wwd.customerapi.dto.FundAccountDTO;
import com.wwd.finance.context.UserContext;
import com.wwd.finance.entity.BudgetItem;
import com.wwd.finance.entity.IncomeRecord;
import com.wwd.finance.mapper.IncomeRecordMapper;
import com.wwd.finance.service.IncomeRecordService;
import com.wwd.financeapi.dto.IncomeQueryDTO;
import com.wwd.financeapi.dto.IncomeRecordDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收入流水服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeRecordServiceImpl extends ServiceImpl<IncomeRecordMapper, IncomeRecord> implements IncomeRecordService {

    private final IncomeRecordMapper incomeRecordMapper;
    private final ObjectMapper objectMapper;

    @Qualifier
    private FundAccountServiceClient fundAccountServiceClient;

    @Override
    public IPage<IncomeRecordDTO> pageIncome(IncomeQueryDTO incomeQueryDTO) {
        // 设置默认排序
        if (StringUtils.isBlank(incomeQueryDTO.getOrderBy())) {
            incomeQueryDTO.setOrderBy("created_at");
            incomeQueryDTO.setAsc(false);
        }

        //当前用户
        incomeQueryDTO.setUserId(UserContext.getCurrentUserId());
        // 处理分页参数
        Page<IncomeRecord> page = new Page<>(incomeQueryDTO.getPageNum(), incomeQueryDTO.getPageSize());
        IPage<IncomeRecord> incomeRecord = incomeRecordMapper.selectIncomePage(page, incomeQueryDTO);

        return incomeRecord.convert(this::convertToDTO);
    }

    /**
     * 转换为DTO
     */
    private IncomeRecordDTO convertToDTO(IncomeRecord entity) {
        if (entity == null) {
            return null;
        }

        IncomeRecordDTO dto = new IncomeRecordDTO();
        BeanUtils.copyProperties(entity, dto);

        return dto;
    }

    @Override
    public IncomeRecord getIncomeDetail(Long incomeId) {
        LambdaQueryWrapper<IncomeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IncomeRecord::getIncomeId, incomeId)
                .eq(IncomeRecord::getIsDeleted, "0")
                .eq(IncomeRecord::getUserId, getCurrentUserId());
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateIncome(IncomeRecordDTO dto) {
        IncomeRecord incomeRecord = new IncomeRecord();
        BeanUtils.copyProperties(dto, incomeRecord, "incomeId", "allocationRule");

        // 设置用户ID
        incomeRecord.setUserId(getCurrentUserId());

        // 处理分配规则（JSON字符串）
//        if (dto.getAllocationRule() != null && !dto.getAllocationRule().isEmpty()) {
//            try {
//                String allocationRuleJson = objectMapper.writeValueAsString(dto.getAllocationRule());
//                incomeRecord.setAllocationRule(allocationRuleJson);
//            } catch (JsonProcessingException e) {
//                log.error("分配规则JSON转换失败", e);
//                throw new RuntimeException("分配规则格式错误");
//            }
//        }

        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        if (incomeRecord.getIncomeId() == null) {
            // 新增
            incomeRecord.setCreatedAt(now);
            incomeRecord.setUpdatedAt(now);
            this.save(incomeRecord);

            FundAccountDTO fundAccountDTO = new FundAccountDTO();
            fundAccountDTO.setAccountId(incomeRecord.getAccountId());
        } else {
            // 更新
            incomeRecord.setUpdatedAt(now);
            LambdaQueryWrapper<IncomeRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(IncomeRecord::getIncomeId, dto.getIncomeId())
                    .eq(IncomeRecord::getUserId, getCurrentUserId());
            this.update(incomeRecord, queryWrapper);
        }

        //更新账户余额
        fundAccountServiceClient.

        return incomeRecord.getIncomeId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteIncome(Long incomeId) {
        IncomeRecord incomeRecord = baseMapper.selectById(incomeId);
        if (incomeRecord == null) {
            throw new RuntimeException("收入数据不存在");
        }

        // 逻辑删除
        incomeRecord.setIsDeleted("1");
        boolean result = this.updateById(incomeRecord);

        if (result) {
            log.info("删除收入成功: incomeId={}", incomeId);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteIncome(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 验证所有记录都属于当前用户
        LambdaQueryWrapper<IncomeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(IncomeRecord::getIncomeId, ids)
                .eq(IncomeRecord::getUserId, getCurrentUserId())
                .eq(IncomeRecord::getIsDeleted, "0");

        List<IncomeRecord> records = this.list(queryWrapper);
        if (records.size() != ids.size()) {
            throw new RuntimeException("部分记录不存在或无权操作");
        }

        // 批量删除
        List<IncomeRecord> updateList = records.stream()
                .map(record -> {
                    IncomeRecord updateRecord = new IncomeRecord();
                    updateRecord.setIncomeId(record.getIncomeId());
                    updateRecord.setIsDeleted("1");
                    updateRecord.setUpdatedAt(LocalDateTime.now());
                    return updateRecord;
                })
                .collect(Collectors.toList());

        return this.updateBatchById(updateList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSettleStatus(Long incomeId, String isSettled) {
        LambdaQueryWrapper<IncomeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IncomeRecord::getIncomeId, incomeId)
                .eq(IncomeRecord::getUserId, getCurrentUserId())
                .eq(IncomeRecord::getIsDeleted, "0");

        IncomeRecord incomeRecord = this.getOne(queryWrapper);
        if (incomeRecord == null) {
            throw new RuntimeException("收入记录不存在");
        }

        IncomeRecord updateRecord = new IncomeRecord();
        updateRecord.setIncomeId(incomeId);
        updateRecord.setIsSettled(isSettled);
        updateRecord.setUpdatedAt(LocalDateTime.now());

        return this.updateById(updateRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateSettleStatus(List<Long> ids, String isSettled) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 验证所有记录都属于当前用户
        LambdaQueryWrapper<IncomeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(IncomeRecord::getIncomeId, ids)
                .eq(IncomeRecord::getUserId, getCurrentUserId())
                .eq(IncomeRecord::getIsDeleted, "0");

        List<IncomeRecord> records = this.list(queryWrapper);
        if (records.size() != ids.size()) {
            throw new RuntimeException("部分记录不存在或无权操作");
        }

        // 批量更新
        List<IncomeRecord> updateList = records.stream()
                .map(record -> {
                    IncomeRecord updateRecord = new IncomeRecord();
                    updateRecord.setIncomeId(record.getIncomeId());
                    updateRecord.setIsSettled(isSettled);
                    updateRecord.setUpdatedAt(LocalDateTime.now());
                    return updateRecord;
                })
                .collect(Collectors.toList());

        return this.updateBatchById(updateList);
    }

    @Override
    public Map<String, Object> getIncomeStatistics(Map<String, Object> params) {
        Long userId = getCurrentUserId();
        params.put("userId", userId);

        Map<String, Object> statistics = incomeRecordMapper.selectIncomeStatistics(params);

        // 计算本月收入
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);

        Map<String, Object> monthParams = new HashMap<>();
        monthParams.put("userId", userId);
        monthParams.put("incomeDateStart", firstDayOfMonth);
        monthParams.put("incomeDateEnd", now);

        Map<String, Object> monthStats = incomeRecordMapper.selectIncomeStatistics(monthParams);
        BigDecimal currentMonthIncome = (BigDecimal) monthStats.getOrDefault("totalIncome", BigDecimal.ZERO);

        statistics.put("currentMonthIncome", currentMonthIncome);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> getIncomeTrend(Map<String, Object> params) {
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

        return incomeRecordMapper.selectIncomeTrend(params);
    }

    @Override
    public List<IncomeRecord> exportIncome(IncomeQueryDTO incomeQueryDTO) {
        Long userId = getCurrentUserId();
        incomeQueryDTO.setUserId(userId);

        // 查询所有符合条件的记录（不分页）
        Page<IncomeRecord> page = new Page<>(1, 10000); // 最大导出10000条
        IPage<IncomeRecord> result = incomeRecordMapper.selectIncomePage(page, incomeQueryDTO);

        return result.getRecords();
    }

    @Override
    public Long getCurrentUserId() {

        return UserContext.getCurrentUserId();
    }

    /**
     * 将前端字段名转换为数据库列名
     */
    private String convertToColumnName(String fieldName) {
        switch (fieldName) {
            case "incomeId":
                return "income_id";
            case "incomeDate":
                return "income_date";
            case "createdAt":
                return "created_at";
            case "updatedAt":
                return "updated_at";
            case "sourceTypeCode":
                return "source_type_code";
            case "sourceDetail":
                return "source_detail";
            case "workHours":
                return "work_hours";
            case "accountId":
                return "account_id";
            case "isSettled":
                return "is_settled";
            default:
                return fieldName;
        }
    }
}