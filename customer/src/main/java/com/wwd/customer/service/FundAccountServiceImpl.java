package com.wwd.customer.service;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.customer.context.UserContext;
import com.wwd.customer.entity.FundAccount;
import com.wwd.customer.entity.UserInfo;
import com.wwd.customer.mapper.FundAccountMapper;
import com.wwd.customerapi.dto.FundAccountQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.service.FundAccountServiceImpl
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
public class FundAccountServiceImpl extends ServiceImpl<FundAccountMapper, FundAccount> implements FundAccountService{


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
    public IPage<FundAccount> queryFundAccountPageByCondition(FundAccountQueryDTO condition) {
        Page<FundAccount> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        condition.setIs_active("1");
        return baseMapper.selectPageByCondition(page, condition);
    }
}
