package com.wwd.customer.message;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.message.AccountAddMessage
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-02-02
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-02-02     wangwd7          v1.0.0               创建
 */
@Data
public class AccountAddMessage {

    private String messageId;
    private Long incomeId;
    private Long accountId;
    private BigDecimal amount;
    private Long userId;
    private Date createTime;

}
