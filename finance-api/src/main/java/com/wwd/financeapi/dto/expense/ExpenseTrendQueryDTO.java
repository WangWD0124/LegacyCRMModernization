package com.wwd.financeapi.dto.expense;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.financeapi.dto.expense.ExpenseTrendQueryDTO
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-18
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-18     wangwd7          v1.0.0               创建
 */
import lombok.Data;

/**
 * 支出趋势查询DTO
 */
@Data
public class ExpenseTrendQueryDTO {

    private String period = "30d";

    private Long userId;

    private String startDate;

    private String endDate;
}
