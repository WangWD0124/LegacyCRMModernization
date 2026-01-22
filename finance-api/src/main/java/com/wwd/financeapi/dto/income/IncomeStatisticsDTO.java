// finance-api/src/main/java/com/wwd/financeapi/dto/IncomeStatisticsDTO.java
package com.wwd.financeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收入统计结果DTO
 */
@Data
@Schema(description = "收入统计结果")
public class IncomeStatisticsDTO {

    @Schema(description = "总收入")
    private BigDecimal totalIncome;

    @Schema(description = "已结算收入")
    private BigDecimal settledIncome;

    @Schema(description = "未结算收入")
    private BigDecimal unsettledIncome;

    @Schema(description = "当前月收入")
    private BigDecimal currentMonthIncome;

    @Schema(description = "总记录数")
    private Long totalCount;

    @Schema(description = "已结算记录数")
    private Long settledCount;

    @Schema(description = "未结算记录数")
    private Long unsettledCount;

    @Schema(description = "平均时薪")
    private BigDecimal avgHourlyRate;

    @Schema(description = "平均收入")
    private BigDecimal avgIncome;
}