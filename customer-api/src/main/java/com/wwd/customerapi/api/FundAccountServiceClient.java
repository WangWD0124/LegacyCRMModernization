package com.wwd.customerapi.api;

import com.wwd.common.constant.ServiceNamesConstant;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.customerapi.dto.FundAccountDTO;
import com.wwd.customerapi.dto.FundAccountOperateDTO;
import com.wwd.customerapi.dto.FundAccountQueryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customerapi.api.FundAccountServiceClient
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
@FeignClient(
        name = ServiceNamesConstant.CUSTOMER_SERVICE,
        path = "/api/customer/fundAccount"
)
@Component("FundAccountServiceClient")
public interface FundAccountServiceClient {

    @PostMapping("/operate")
    Result<Long> operateFundAccount(@RequestBody @Valid FundAccountOperateDTO fundAccountOperateDTO);

    @DeleteMapping("/delete/{account_id}")
    Result<Integer> deleteFundAccountByAccountId(@PathVariable Long account_id);

    @GetMapping("/{account_id}")
    Result<FundAccountDTO> queryFundAccountByAccountId(@PathVariable Long account_id);

    @GetMapping("/list")
    Result<List<FundAccountDTO>> queryFundAccountListByCondition(FundAccountQueryDTO fundAccountQueryDTO);

    @GetMapping("/page")
    Result<PageResult<FundAccountDTO>> queryFundAccountPageByCondition(FundAccountQueryDTO fundAccountQueryDTO);

    @PostMapping("/operate")
    Result<Long> update(@RequestBody @Valid FundAccountOperateDTO fundAccountOperateDTO);
}
