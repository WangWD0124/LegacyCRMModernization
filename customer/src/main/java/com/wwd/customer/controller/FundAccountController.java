package com.wwd.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.customer.context.UserContext;
import com.wwd.customer.entity.FundAccount;
import com.wwd.customer.entity.UserInfo;
import com.wwd.customer.service.FundAccountService;
import com.wwd.customerapi.api.FundAccountServiceClient;
import com.wwd.customerapi.dto.FundAccountDTO;
import com.wwd.customerapi.dto.FundAccountOperateDTO;
import com.wwd.customerapi.dto.FundAccountQueryDTO;
import com.wwd.customerapi.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
@RestController
@RequestMapping("/api/customer/fundAccount")
public class FundAccountController implements FundAccountServiceClient {

    @Autowired
    private FundAccountService fundAccountService;

    @Override
    public Result<Long> operateFundAccount(FundAccountOperateDTO fundAccountOperateDTO) {

        FundAccount fundAccount = new FundAccount();
        BeanUtils.copyProperties(fundAccountOperateDTO, fundAccount);

        if (fundAccount.getAccountId() != null){
            fundAccountService.updateFundAccount(fundAccount);
            return Result.success(fundAccount.getAccountId());
        } else {
            Long currentUserId = UserContext.getCurrentUserId();

            if (currentUserId == null){
                return Result.error("用户未登录");
            }
            fundAccount.setUserId(currentUserId);

            Long account_id = fundAccountService.createFundAccount(fundAccount);
            return Result.success(account_id);
        }
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
        IPage<FundAccount> page = fundAccountService.queryFundAccountPageByCondition(fundAccountQueryDTO);
        List<FundAccountDTO> fundAccountDTOS = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PageResult<FundAccountDTO> fundAccountPage = new PageResult<FundAccountDTO>(
                fundAccountDTOS,
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return Result.success(fundAccountPage);
    }

    @Override
    public Result<Long> update(FundAccountOperateDTO fundAccountOperateDTO) {
        return null;
    }

    /**
     * Entity 转 DTO
     */
    private FundAccountDTO convertToDTO(FundAccount fundAccount) {
        if (fundAccount == null) {
            return null;
        }
        FundAccountDTO dto = new FundAccountDTO();
        BeanUtils.copyProperties(fundAccount, dto);
        return dto;
    }
}
