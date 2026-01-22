// finance-api/src/main/java/com/wwd/financeapi/dto/IncomeQueryDTO.java
package com.wwd.financeapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 收入查询DTO
 */
@Data
@Schema(description = "收入查询参数")
public class IncomeQueryDTO {

    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer pageSize = 10;

    @Schema(description = "项目标题（模糊查询）")
    private String title;

    @Schema(description = "来源类型代码")
    private String sourceTypeCode;

    @Schema(description = "结算状态（Y/N）")
    private String isSettled;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "账户ID")
    private Long accountId;

    @Schema(description = "收入日期开始")
    private LocalDate incomeDateStart;

    @Schema(description = "收入日期结束")
    private LocalDate incomeDateEnd;

    @Schema(description = "创建时间开始")
    private String createTimeStart;

    @Schema(description = "创建时间结束")
    private String createTimeEnd;

    @Schema(description = "金额最小值")
    private BigDecimal amountMin;

    @Schema(description = "金额最大值")
    private BigDecimal amountMax;

    @Schema(description = "来源详情（模糊查询）")
    private String sourceDetail;

    @Schema(description = "是否有工作时长（true/false）")
    private String hasWorkHours;

    @Schema(description = "是否有分配规则（true/false）")
    private String hasAllocationRule;

    @Schema(description = "创建时间")
    private LocalDate createAt;

    @Schema(description = "更新时间")
    private LocalDate updateAt;

    @Schema(description = "是否已删除")
    private String isDeleted;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "是否升序")
    private Boolean asc;
}