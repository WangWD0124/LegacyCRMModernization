package com.wwd.financeapi.dto.budget;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消费类型DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ConsumptionTypeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    private Long id;

    /**
     * 类型代码
     */
    private String typeCode;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 图标
     */
    private String icon;

    /**
     * 颜色
     */
    private String color;

    /**
     * 父级类型ID
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否系统预置
     */
    private Boolean systemPredefined;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}