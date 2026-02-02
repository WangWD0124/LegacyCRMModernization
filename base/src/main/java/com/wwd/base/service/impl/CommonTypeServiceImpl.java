package com.wwd.base.service.impl;

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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.base.entity.CommonType;
import com.wwd.base.mapper.CommonTypeMapper;
import com.wwd.base.service.CommonTypeService;
import com.wwd.baseapi.dto.CommonTypeQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonTypeServiceImpl extends ServiceImpl<CommonTypeMapper, CommonType>
        implements CommonTypeService {

    private final CommonTypeMapper commonTypeMapper;

    @Override
    @Cacheable(value = "commonType", key = "'group:' + #typeGroup")
    public List<CommonType> getTypesByGroup(String typeGroup) {
        return commonTypeMapper.selectByGroupAndStatus(typeGroup, 1);
    }

    @Override
    public CommonType getByGroupAndCode(String typeGroup, String typeCode) {
        LambdaQueryWrapper<CommonType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommonType::getTypeGroup, typeGroup)
                .eq(CommonType::getTypeCode, typeCode)
                .eq(CommonType::getStatus, 1);
        return getOne(wrapper);
    }

    @Override
    public List<CommonType> listTypes(CommonTypeQueryDTO query) {
        LambdaQueryWrapper<CommonType> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getTypeGroup())) {
            wrapper.eq(CommonType::getTypeGroup, query.getTypeGroup());
        }
        if (StringUtils.hasText(query.getTypeCode())) {
            wrapper.like(CommonType::getTypeCode, query.getTypeCode());
        }
        if (StringUtils.hasText(query.getTypeName())) {
            wrapper.like(CommonType::getTypeName, query.getTypeName());
        }
        if (query.getParentId() != null) {
            wrapper.eq(CommonType::getParentId, query.getParentId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(CommonType::getStatus, query.getStatus());
        } else {
            wrapper.ne(CommonType::getStatus, 2); // 排除已删除的
        }

        wrapper.orderByAsc(CommonType::getSortOrder)
                .orderByAsc(CommonType::getTypeId);

        return list(wrapper);
    }

    @Override
    @Cacheable(value = "commonType", key = "'tree:' + #typeGroup")
    public List<Map<String, Object>> getTypeTree(String typeGroup) {
        List<CommonType> types = getTypesByGroup(typeGroup);

        // 构建ID到类型的映射
        Map<Long, Map<String, Object>> nodeMap = types.stream()
                .collect(Collectors.toMap(
                        CommonType::getTypeId,
                        type -> {
                            Map<String, Object> node = new HashMap<>();
                            node.put("typeId", type.getTypeId());
                            node.put("typeCode", type.getTypeCode());
                            node.put("typeName", type.getTypeName());
                            node.put("parentId", type.getParentId());
                            node.put("sortOrder", type.getSortOrder());
                            node.put("description", type.getDescription());
                            node.put("children", new ArrayList<Map<String, Object>>());
                            return node;
                        }
                ));

        // 构建树形结构
        List<Map<String, Object>> roots = new ArrayList<>();
        for (Map<String, Object> node : nodeMap.values()) {
            Long parentId = (Long) node.get("parentId");
            if (parentId == null) {
                roots.add(node);
            } else {
                Map<String, Object> parent = nodeMap.get(parentId);
                if (parent != null) {
                    List<Map<String, Object>> children =
                            (List<Map<String, Object>>) parent.get("children");
                    children.add(node);
                }
            }
        }

        // 排序
        roots.sort(Comparator.comparing(node -> (Integer) node.get("sortOrder")));
        sortTreeNodes(roots);

        return roots;
    }

    private void sortTreeNodes(List<Map<String, Object>> nodes) {
        nodes.sort(Comparator.comparing(node -> (Integer) node.get("sortOrder")));
        for (Map<String, Object> node : nodes) {
            List<Map<String, Object>> children =
                    (List<Map<String, Object>>) node.get("children");
            if (children != null && !children.isEmpty()) {
                sortTreeNodes(children);
            }
        }
    }

    @Override
    @Cacheable(value = "commonType", key = "'mapping:' + #typeGroup")
    public Map<String, String> getTypeMapping(String typeGroup) {
        List<Map<String, Object>> mappings =
                commonTypeMapper.selectMappingByGroup(typeGroup);

        return mappings.stream()
                .collect(Collectors.toMap(
                        map -> (String) map.get("type_code"),
                        map -> (String) map.get("type_name")
                ));
    }

    @Override
    public Map<String, String> batchGetNames(String typeGroup, List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> mapping = getTypeMapping(typeGroup);
        return codes.stream()
                .filter(mapping::containsKey)
                .collect(Collectors.toMap(
                        code -> code,
                        mapping::get
                ));
    }

    @Override
    @Transactional
    public Long createType(CommonType type) {
        // 检查唯一性
        LambdaQueryWrapper<CommonType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommonType::getTypeGroup, type.getTypeGroup())
                .eq(CommonType::getTypeCode, type.getTypeCode())
                .ne(CommonType::getStatus, 2);

        if (count(wrapper) > 0) {
            throw new RuntimeException("类型代码已存在");
        }

        type.setStatus(1); // 默认启用
        save(type);
        return type.getTypeId();
    }

    @Override
    @Transactional
    public boolean updateType(Long typeId, CommonType type) {
        CommonType existing = getById(typeId);
        if (existing == null) {
            throw new RuntimeException("类型不存在");
        }

        // 检查唯一性（排除自身）
        LambdaQueryWrapper<CommonType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommonType::getTypeGroup, type.getTypeGroup())
                .eq(CommonType::getTypeCode, type.getTypeCode())
                .ne(CommonType::getTypeId, typeId)
                .ne(CommonType::getStatus, 2);

        if (count(wrapper) > 0) {
            throw new RuntimeException("类型代码已存在");
        }

        type.setTypeId(typeId);
        return updateById(type);
    }

    @Override
    @Transactional
    public boolean deleteType(Long typeId) {
        // 逻辑删除
        CommonType type = new CommonType();
        type.setTypeId(typeId);
        type.setStatus(2); // 删除状态
        return updateById(type);
    }

    @Override
    @Transactional
    public boolean updateTypeStatus(Long typeId, Integer status) {
        CommonType type = new CommonType();
        type.setTypeId(typeId);
        type.setStatus(status);
        return updateById(type);
    }

    @Override
    public List<CommonType> getChildren(Long parentId) {
        return commonTypeMapper.selectChildren(parentId);
    }
}