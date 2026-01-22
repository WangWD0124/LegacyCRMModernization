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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 支出趋势查询DTO
 */
@Data
@Schema(description = "支出趋势查询参数")
public class ExpenseTrendQueryDTO {

    @Schema(description = "统计周期", defaultValue = "30d", allowableValues = {"7d", "30d", "90d", "year"})
    private String period = "30d";

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "开始日期（period不为空时忽略）")
    private String startDate;

    @Schema(description = "结束日期（period不为空时忽略）")
    private String endDate;
}
