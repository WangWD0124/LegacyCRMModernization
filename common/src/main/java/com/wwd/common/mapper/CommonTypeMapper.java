package com.wwd.common.mapper;

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
import com.wwd.common.entity.CommonType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommonTypeMapper extends BaseMapper<CommonType> {

    @Select("SELECT * FROM common_type WHERE type_group = #{typeGroup} AND status = 1 ORDER BY sort_order")
    List<CommonType> findByTypeGroup(@Param("typeGroup") String typeGroup);

    @Select("SELECT * FROM common_type WHERE type_group = #{typeGroup} AND type_code = #{typeCode} AND status = 1")
    CommonType findByGroupAndCode(@Param("typeGroup") String typeGroup,
                                  @Param("typeCode") String typeCode);
}
