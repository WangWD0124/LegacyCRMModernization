package com.wwd.base.mapper;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.mapper.CommonTypeMapper
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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wwd.base.entity.CommonType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonTypeMapper extends BaseMapper<CommonType> {

    @Select("SELECT type_id, type_code, type_name, type_group, parent_id, " +
            "sort_order, status, description, created_at " +
            "FROM common_type " +
            "WHERE type_group = #{typeGroup} AND status = #{status} " +
            "ORDER BY sort_order, type_id")
    List<CommonType> selectByGroupAndStatus(
            @Param("typeGroup") String typeGroup,
            @Param("status") Integer status);

    @Select("SELECT DISTINCT type_group FROM common_type WHERE status = 1 ORDER BY type_group")
    List<String> selectDistinctGroups();

    @Select("SELECT type_code, type_name FROM common_type " +
            "WHERE type_group = #{typeGroup} AND status = 1 " +
            "ORDER BY sort_order")
    List<Map<String, Object>> selectMappingByGroup(@Param("typeGroup") String typeGroup);

    @Select("SELECT * FROM common_type " +
            "WHERE parent_id = #{parentId} AND status = 1 " +
            "ORDER BY sort_order")
    List<CommonType> selectChildren(@Param("parentId") Long parentId);
}

