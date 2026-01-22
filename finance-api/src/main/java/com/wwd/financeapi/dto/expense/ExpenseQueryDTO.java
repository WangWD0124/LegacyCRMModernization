package com.wwd.financeapi.dto.expense;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.financeapi.dto.expense.ExpenseQueryDTO
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
import java.time.LocalDate;

/**
 * 支出查询DTO
 */
@Data
@Schema(description = "支出查询参数")
public class ExpenseQueryDTO {

    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer pageSize = 10;

    @Schema(description = "收款方（模糊查询）")
    private String payee;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "支出日期开始")
    private LocalDate expenseDateStart;

    @Schema(description = "支出日期结束")
    private LocalDate expenseDateEnd;

    @Schema(description = "创建时间开始")
    private String createTimeStart;

    @Schema(description = "创建时间结束")
    private String createTimeEnd;

    @Schema(description = "金额最小值")
    private BigDecimal amountMin;

    @Schema(description = "金额最大值")
    private BigDecimal amountMax;

    @Schema(description = "描述（模糊查询）")
    private String description;

    @Schema(description = "是否关联预算（true/false）")
    private String hasBudgetItem;

    @Schema(description = "差异最小值")
    private BigDecimal differenceMin;

    @Schema(description = "差异最大值")
    private BigDecimal differenceMax;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "是否升序")
    private Boolean asc;
}
