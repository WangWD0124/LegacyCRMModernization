package com.wwd.customer.mapper;

import com.wwd.customer.entity.Role;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.mapper.RoleMapper
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-11-19
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-11-19     wangwd7          v1.0.0               创建
 */
@Repository
@Mapper
public interface RoleMapper {

    @Insert("INSERT INTO ROLE(ROLE_NAME, DESCRIPTION) VALUES(#{roleName}, #{description})")
    @Options(keyProperty = "ROLE_ID", useGeneratedKeys = true)
    int insertRole(Role role);

    @Select("SELECT * FROM ROLE WHERE ROLE_ID = #{roleId}")
    Role selectRoleByRoleId(Long roleId);
}
