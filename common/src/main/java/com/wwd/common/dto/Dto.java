package com.wwd.common.dto;

import org.springframework.beans.BeanUtils;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.dto.Dto
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-21
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-21     wangwd7          v1.0.0               创建
 */
public class Dto<T> {


    /**
     * DTO 转 Entity
     */
    private Entity<T> convertToEntity(Dto<T> dto) {
        if (dto == null) {
            return null;
        }
        Entity<T> entity = new Entity<T>();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
