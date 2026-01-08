package com.wwd.customer.controller;

import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.customer.entity.FundAccount;
import com.wwd.customer.service.FundAccountService;
import com.wwd.customerapi.api.FundAccountServiceClient;
import com.wwd.customerapi.dto.FundAccountDTO;
import com.wwd.customerapi.dto.FundAccountOperateDTO;
import com.wwd.customerapi.dto.FundAccountQueryDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.controller.FundAccountController
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-05
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-05     wangwd7          v1.0.0               创建
 */
@Controller
@RequestMapping("/api/fundAccount")
public class FundAccountController implements FundAccountServiceClient {

    @Autowired
    private FundAccountService fundAccountService;

    @Override
    public Result<Long> createFundAccount(FundAccountOperateDTO fundAccountOperateDTO) {

        FundAccount fundAccount = new FundAccount();
        BeanUtils.copyProperties(fundAccountOperateDTO, fundAccount);

        Long account_id = fundAccountService.createFundAccount(fundAccount);
        return Result.success(account_id);
    }

    @Override
    public Result<Integer> updateFundAccount(FundAccountOperateDTO fundAccountOperateDTO) {

        FundAccount fundAccount = new FundAccount();
        BeanUtils.copyProperties(fundAccountOperateDTO, fundAccount);
        return Result.success(fundAccountService.updateFundAccount(fundAccount));
    }

    @Override
    public Result<Integer> deleteFundAccountByAccountId(Long account_id) {
        return Result.success(fundAccountService.deleteFundAccountByAccountId(account_id));
    }

    @Override
    public Result<FundAccountDTO> queryFundAccountByAccountId(Long account_id) {
        FundAccountDTO fundAccountDTO = new FundAccountDTO();
        FundAccount fundAccount = fundAccountService.selectByAccountId(account_id);
        BeanUtils.copyProperties(fundAccount, fundAccountDTO);
        return Result.success(fundAccountDTO);
    }

    @Override
    public Result<List<FundAccountDTO>> queryFundAccountListByCondition(FundAccountQueryDTO fundAccountQueryDTO) {
        return null;
    }

    @Override
    public Result<PageResult<FundAccountDTO>> queryFundAccountPageByCondition(FundAccountQueryDTO fundAccountQueryDTO) {
        return null;
    }
}
