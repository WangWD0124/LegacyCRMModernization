package com.wwd.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.common.dto.BusinessResult;
import com.wwd.common.enums.BusinessResultEnun;
import com.wwd.customer.entity.FundAccount;
import com.wwd.customer.mapper.FundAccountMapper;
import com.wwd.customer.service.FundAccountService;
import com.wwd.customerapi.dto.FundAccountQueryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.service.impl.FundAccountServiceImpl
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-04
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-04     wangwd7          v1.0.0               创建
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FundAccountServiceImpl extends ServiceImpl<FundAccountMapper, FundAccount> implements FundAccountService {


    @Override
    public FundAccount selectByAccountId(Long account_id) {
        return baseMapper.selectByFundAccountId(account_id);
    }

    @Override
    public FundAccount selectByAccountCode(String account_code) {
        return baseMapper.selectByAccountCode(account_code);
    }

    @Override
    public Long createFundAccount(FundAccount fundAccount) {

        Long accountCode = IdWorker.getId();
        fundAccount.setAccountCode(String.valueOf(accountCode));
        fundAccount.setIsActive("1");
        fundAccount.setCreatedAt(LocalDateTime.now());
        fundAccount.setUpdatedAt(LocalDateTime.now());
        baseMapper.insert(fundAccount);
        return fundAccount.getAccountId();
    }

    @Override
    public Integer updateFundAccount(FundAccount fundAccount) {
        fundAccount.setUpdatedAt(LocalDateTime.now());
        return baseMapper.updateById(fundAccount);
    }

    @Override
    public Integer deleteFundAccountByAccountId(Long accountId) {

        FundAccount fundAccount = selectByAccountId(accountId);
        fundAccount.setIsActive("0");
        fundAccount.setUpdatedAt(LocalDateTime.now());
        return baseMapper.updateById(fundAccount);
    }


    @Override
    public List<FundAccount> queryFundAccountListByCondition(FundAccountQueryDTO condition) {
        condition.setIs_active("1");
        return baseMapper.selectListByCondition(condition);
    }

    @Override
    public IPage<FundAccount> queryFundAccountPageByCondition(FundAccountQueryDTO condition) {
        Page<FundAccount> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        condition.setIs_active("1");
        return baseMapper.selectPageByCondition(page, condition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<String> addBalance(Long accountId, BigDecimal amount) {

        try {
            log.info("[账户服务][增加余额]开始，账户ID: {}, 增加金额: {}", accountId, amount);
            FundAccount fundAccount = selectByAccountId(accountId);
            if (fundAccount == null) {
                log.error("[账户服务][增加余额]账户不存在，账户ID: {}", accountId);
                return BusinessResult.error(BusinessResultEnun.FAILED_NULL_ACCOUNT);
            }
            log.info("[账户服务][增加余额]查询到账户，当前余额: {}", fundAccount.getBalance());

            // 将字符串余额转换为BigDecimal
            BigDecimal currentBalance = new BigDecimal(fundAccount.getBalance());
            // 加法运算
            BigDecimal newBalance = currentBalance.add(amount);
            // 保留2位小数
            String updatedBalance = newBalance.setScale(2, RoundingMode.HALF_UP).toString();
            log.info("[账户服务][增加余额]计算后新余额: {}", updatedBalance);

            fundAccount.setBalance(updatedBalance);
            log.info("[账户服务][增加余额]更新账户，账户ID: {}, 新余额: {}", accountId, updatedBalance);
            Integer updateRow = updateFundAccount(fundAccount);
            log.info("[账户服务][增加余额]更新影响行数: {}", updateRow);

            if (updateRow > 0) {
                // 再次查询，确认更新
                FundAccount updatedAccount = selectByAccountId(accountId);
                log.info("[账户服务][增加余额]更新后查询账户余额: {}", updatedAccount.getBalance());
                return BusinessResult.success(updatedBalance);
            } else {
                return BusinessResult.error();
            }
        } catch (Exception e) {
            log.error("添加余额异常", e);
            throw e;
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<String> deductBalance(Long accountId, BigDecimal amount) {

        FundAccount fundAccount = selectByAccountId(accountId);
        BigDecimal balance = BigDecimal.valueOf(Long.parseLong(fundAccount.getBalance()));
        if (balance.compareTo(amount) < 0) {
            return BusinessResult.error(BusinessResultEnun.FAILED_INSUFFICIENT);
        }
        String currentBalance = balance.add(amount).toString();
        fundAccount.setBalance(currentBalance);
        Integer updateRow = updateFundAccount(fundAccount);
        if (updateRow > 0) {
            return BusinessResult.success(currentBalance);
        } else {
            return BusinessResult.error();
        }
    }
}
