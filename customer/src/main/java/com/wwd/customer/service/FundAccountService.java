package com.wwd.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.dto.BusinessResult;
import com.wwd.common.enums.MessageStatus;
import com.wwd.customer.entity.FundAccount;
import com.wwd.customerapi.dto.FundAccountQueryDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.service.FundAccountService
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
public interface FundAccountService {

    FundAccount selectByAccountId(Long account_id);

    FundAccount selectByAccountCode(String account_code);

    Long createFundAccount(FundAccount fundAccount);

    Integer updateFundAccount(FundAccount fundAccount);

    Integer deleteFundAccountByAccountId(Long accountId);

    List<FundAccount> queryFundAccountListByCondition(FundAccountQueryDTO fundAccountQueryDTO);

    IPage<FundAccount> queryFundAccountPageByCondition(FundAccountQueryDTO fundAccountQueryDTO);

    BusinessResult<String> addBalance(Long accountId, BigDecimal amount);
}
