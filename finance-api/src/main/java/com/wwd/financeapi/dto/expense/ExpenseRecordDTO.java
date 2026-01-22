package com.wwd.financeapi.dto.expense;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 支出流水DTO
 */
@Data
@Schema(description = "支出流水数据传输对象")
public class ExpenseRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "支出ID（更新时必填）")
    private Long expenseId;

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "账户ID", required = true)
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    @Schema(description = "预算项目ID")
    private Long budgetItemId;

    @Schema(description = "支出金额", required = true)
    @NotNull(message = "支出金额不能为空")
    @DecimalMin(value = "0.01", message = "支出金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "货币类型", defaultValue = "CNY")
    @NotBlank(message = "货币类型不能为空")
    private String currency = "CNY";

    @Schema(description = "支出日期", required = true)
    @NotNull(message = "支出日期不能为空")
    private LocalDate expenseDate;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "收款方")
    @Size(max = 200, message = "收款方不能超过200个字符")
    private String payee;

    @Schema(description = "描述")
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    @Schema(description = "备注")
    @Size(max = 1000, message = "备注不能超过1000个字符")
    private String remark;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "差异金额")
    private BigDecimal differenceAmount;

    @Schema(description = "差异百分比")
    private BigDecimal differencePercent;
}