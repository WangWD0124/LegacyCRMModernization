package com.wwd.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.common.entity.CommonType;
import com.wwd.common.mapper.CommonTypeMapper;
import com.wwd.common.service.CommonTypeService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.service.impl.CommonTypeServiceImpl
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
@Service
public class CommonTypeServiceImpl extends ServiceImpl<CommonTypeMapper, CommonType> implements CommonTypeService {


    @Autowired
    private CommonTypeMapper commonTypeMapper;

    /**
     * 根据分组获取类型列表（带缓存）
     * 缓存key: commonType::group::{typeGroup}
     */
    @Override
    @Cacheable(value = "commonType", key = "'group:' + #typeGroup")
    public List<CommonType> getTypesByGroup(String typeGroup) {
        return commonTypeMapper.findByTypeGroup(typeGroup);
    }

    /**
     * 根据分组和代码获取类型（带缓存）
     * 缓存key: commonType::detail::{typeGroup}::{typeCode}
     */
    @Override
    @Cacheable(value = "commonType", key = "'detail:' + #typeGroup + ':' + #typeCode")
    public CommonType getTypeByGroupAndCode(String typeGroup, String typeCode) {
        return commonTypeMapper.findByGroupAndCode(typeGroup, typeCode);
    }

    /**
     * 新增类型（清除相关缓存）
     */
    @Override
    @Transactional
    @CacheEvict(value = "commonType", key = "'group:' + #type.typeGroup")
    public boolean addType(CommonType type) {
        return commonTypeMapper.insert(type) > 0;
    }

    /**
     * 更新类型（清除相关缓存）
     */
    @Override
    @Transactional
    @CacheEvict(value = "commonType", allEntries = true)
    public boolean updateType(CommonType type) {
        return commonTypeMapper.updateById(type) > 0;
    }

}