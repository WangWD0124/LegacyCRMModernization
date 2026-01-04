package com.wwd.customer.service;

import com.wwd.customer.entity.FundAccount;
import org.springframework.stereotype.Service;

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
}
