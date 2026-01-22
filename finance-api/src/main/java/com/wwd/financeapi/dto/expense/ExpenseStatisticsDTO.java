package com.wwd.financeapi.dto.expense;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.financeapi.dto.expense.ExpenseStatisticsDTO
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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支出统计结果DTO
 */
@Data
@Schema(description = "支出统计结果")
public class ExpenseStatisticsDTO {

    @Schema(description = "总支出")
    private BigDecimal totalExpense;

    @Schema(description = "本月支出")
    private BigDecimal currentMonthExpense;

    @Schema(description = "平均支出")
    private BigDecimal avgExpense;

    @Schema(description = "预算关联率")
    private BigDecimal budgetLinkedRate;

    @Schema(description = "总记录数")
    private Long totalCount;

    @Schema(description = "关联预算记录数")
    private Long linkedCount;
}
