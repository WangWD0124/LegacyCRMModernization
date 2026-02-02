package com.wwd.baseapi.dto;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.entity.CommonType
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-22
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-22     wangwd7          v1.0.0               创建
 */

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonTypeQueryDTO {
    private String typeGroup;
    private String typeCode;
    private String typeName;
    private Long parentId;
    private Integer status;
    private Integer page;
    private Integer size;

    public CommonTypeQueryDTO(String typeGroup) {
        this.typeGroup = typeGroup;
        this.status = 1;
        this.page = 1;
        this.size = 100;
    }
}
