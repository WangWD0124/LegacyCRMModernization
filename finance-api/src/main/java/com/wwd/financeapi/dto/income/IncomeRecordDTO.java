package com.wwd.financeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 收入流水DTO
 */
@Data
@Schema(description = "收入流水数据传输对象")
public class IncomeRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "收入ID（更新时必填）")
    private Long incomeId;

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "账户ID", required = true)
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    @Schema(description = "收入金额", required = true)
    @NotNull(message = "收入金额不能为空")
    @DecimalMin(value = "0.01", message = "收入金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "货币类型", defaultValue = "CNY")
    @NotBlank(message = "货币类型不能为空")
    private String currency = "CNY";

    @Schema(description = "收入日期", required = true)
    @NotNull(message = "收入日期不能为空")
    private LocalDate incomeDate;

    @Schema(description = "收入来源类型代码")
    private String sourceTypeCode;

    @Schema(description = "收入来源详情")
    @Size(max = 200, message = "来源详情不能超过200个字符")
    private String sourceDetail;

    @Schema(description = "工作时长")
    @DecimalMin(value = "0", message = "工作时长不能为负数")
    private BigDecimal workHours;

    @Schema(description = "描述")
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    @Schema(description = "备注")
    @Size(max = 1000, message = "备注不能超过1000个字符")
    private String remark;

    @Schema(description = "分配规则列表")
    private List<AllocationRuleDTO> allocationRule;

    @Schema(description = "结算状态", defaultValue = "N")
    @Pattern(regexp = "^(Y|N)$", message = "结算状态只能是Y或N")
    private String isSettled = "N";

    @Schema(description = "创建时间", required = true)
    @NotNull(message = "创建时间不能为空")
    private LocalDate createAt;

    @Schema(description = "更新时间", required = true)
    @NotNull(message = "更新时间不能为空")
    private LocalDate updateAt;
}

@Data
@Schema(description = "分配规则DTO")
class AllocationRuleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "基金ID", required = true)
    @NotNull(message = "基金ID不能为空")
    private Long fundId;

    @Schema(description = "分配百分比", required = true)
    @NotNull(message = "分配百分比不能为空")
    @DecimalMin(value = "0", message = "分配百分比不能小于0")
    @DecimalMax(value = "100", message = "分配百分比不能大于100")
    private BigDecimal percentage;

    @Schema(description = "基金名称（可选）")
    private String fundName;
}