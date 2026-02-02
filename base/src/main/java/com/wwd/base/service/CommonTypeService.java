package com.wwd.base.service;

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

import com.baomidou.mybatisplus.extension.service.IService;
import com.wwd.base.entity.CommonType;
import com.wwd.baseapi.dto.CommonTypeQueryDTO;

import java.util.List;
import java.util.Map;

public interface CommonTypeService extends IService<CommonType> {

    List<CommonType> getTypesByGroup(String typeGroup);

    CommonType getByGroupAndCode(String typeGroup, String typeCode);

    List<CommonType> listTypes(CommonTypeQueryDTO query);

    List<Map<String, Object>> getTypeTree(String typeGroup);

    Map<String, String> getTypeMapping(String typeGroup);

    Map<String, String> batchGetNames(String typeGroup, List<String> codes);

    Long createType(CommonType type);

    boolean updateType(Long typeId, CommonType type);

    boolean deleteType(Long typeId);

    boolean updateTypeStatus(Long typeId, Integer status);

    List<CommonType> getChildren(Long parentId);
}