package com.wwd.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.customer.entity.User;
import com.wwd.customer.mapper.RoleMapper;
import com.wwd.customer.mapper.UserMapper;
import com.wwd.customerapi.dto.UserQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.service.UserServiceImpl
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
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Value("${app.default-user-status:ACTIVE}")
    private String defaultUserStatus;

    //private final PasswordEncoder passwordEncoder;

    @Override
    public Long createUser(User user) {

        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(defaultUserStatus);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(user);
        return user.getUserId();
    }

    @Override
    public int updateUser(User user) {

        user.setUpdateTime(LocalDateTime.now());

        return baseMapper.updateById(user);
    }

    @Override
    public int deleteUserByUserId(Long userId) {
        return baseMapper.deleteById(userId);
    }

    @Override
    public User queryUserByUserId(Long userId) {
        return baseMapper.selectById(userId);
    }

    @Override
    public List<User> queryUserListByCondition(UserQueryDTO condition) {
        return baseMapper.selectListByCondition(condition);
    }

    @Override
    public IPage<User> queryUserPageByCondition(UserQueryDTO condition) {

        Page<User> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        return baseMapper.selectPageByCondition(page, condition);
    }




}
