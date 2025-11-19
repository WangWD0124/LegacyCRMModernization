package com.wwd.customer.service;

import com.wwd.customer.entity.User;
import com.wwd.customer.mapper.RoleMapper;
import com.wwd.customer.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Value("${app.default-user-status:ACTIVE}")
    private String defaultUserStatus;

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findByUserId(Long userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public User findByUsername(String userName) {
        return userMapper.findByUserName(userName);
    }

    @Override
    @Transactional
    public boolean saveUser(User user) {
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUserName(user.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        // 检查用户是否存在
        User existingUser = userMapper.findByUserId(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        return userMapper.update(user) > 0;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        // 检查用户是否存在
        User existingUser = userMapper.findByUserId(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        return userMapper.delete(userId) > 0;
    }

    @Override
    public Map<String, Object> findByPage(String username, Integer status, Integer page, Integer size) {
        // 计算分页参数
        int pageNum = page == null ? 1 : page;
        int pageSize = size == null ? 10 : size;
        int offset = (pageNum - 1) * pageSize;

        // 查询数据
        List<User> users = userMapper.findByPage(username, status);
        Long total = userMapper.countByCondition(username, status);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("total", total);
        result.put("page", pageNum);
        result.put("size", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize);

        return result;
    }
}
