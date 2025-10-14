package com.wwd.customer.mapper;

import com.wwd.customer.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.mapper.UserMapper
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-10-12
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-10-12     wangwd7          v1.0.0               创建
 */
@Mapper
public interface UserMapper {

    // 查询所有用户
    @Select("SELECT * FROM user")
    List<User> findAll();

    // 根据ID查询用户
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    // 根据用户名查询用户
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    // 插入用户
    @Insert("INSERT INTO user (username, password, email, phone, status) " +
            "VALUES (#{username}, #{password}, #{email}, #{phone}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    // 更新用户
    @Update("UPDATE user SET username=#{username}, password=#{password}, " +
            "email=#{email}, phone=#{phone}, status=#{status} WHERE id=#{id}")
    int update(User user);

    // 删除用户
    @Delete("DELETE FROM user WHERE id = #{id}")
    int delete(Long id);

    // 分页查询用户（使用XML配置）
    List<User> findByPage(@Param("username") String username,
                          @Param("status") Integer status);

    // 统计用户数量
    Long countByCondition(@Param("username") String username,
                          @Param("status") Integer status);
}
