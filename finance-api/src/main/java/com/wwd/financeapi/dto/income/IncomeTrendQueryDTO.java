// finance-api/src/main/java/com/wwd/financeapi/dto/IncomeTrendQueryDTO.java
package com.wwd.financeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 收入趋势查询DTO
 */
@Data
@Schema(description = "收入趋势查询参数")
public class IncomeTrendQueryDTO {

    @Schema(description = "统计周期", defaultValue = "30d", allowableValues = {"7d", "30d", "90d", "year"})
    private String period = "30d";

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "开始日期（period不为空时忽略）")
    private String startDate;

    @Schema(description = "结束日期（period不为空时忽略）")
    private String endDate;
}