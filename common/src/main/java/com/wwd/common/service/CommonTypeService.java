package com.wwd.common.service;

import com.wwd.common.entity.CommonType;

import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.service.CommonTypeService
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
public interface CommonTypeService {

    /**
     * 根据分组获取类型列表（带缓存）
     * 缓存key: commonType::group::{typeGroup}
     */
    public List<CommonType> getTypesByGroup(String typeGroup);

    /**
     * 根据分组和代码获取类型（带缓存）
     * 缓存key: commonType::detail::{typeGroup}::{typeCode}
     */
    public CommonType getTypeByGroupAndCode(String typeGroup, String typeCode);

    /**
     * 新增类型（清除相关缓存）
     */
    public boolean addType(CommonType type);

    /**
     * 更新类型（清除相关缓存）
     */
    public boolean updateType(CommonType type);

}
