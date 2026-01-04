package com.wwd.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.common.utils.JwtUtil;
import com.wwd.customer.entity.UserInfo;
import com.wwd.customer.mapper.UserInfoMapper;
import com.wwd.customerapi.dto.UserLoginDTO;
import com.wwd.customerapi.dto.UserQueryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;


    @Value("${app.default-user-status.ACTIVE}")
    private Integer defaultUserStatus;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Long createUser(UserInfo userInfo) {

        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        userInfo.setStatus(defaultUserStatus);
        userInfo.setCreateTime(LocalDateTime.now());
        userInfo.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(userInfo);
        return userInfo.getUserId();
    }

    @Override
    public int updateUser(UserInfo userInfo) {

        userInfo.setUpdateTime(LocalDateTime.now());

        return baseMapper.updateById(userInfo);
    }

    @Override
    public int deleteUserByUserId(Long userId) {
        return baseMapper.deleteById(userId);
    }

    @Override
    public UserInfo queryUserByUserId(Long userId) {
        return baseMapper.selectById(userId);
    }

    @Override
    public List<UserInfo> queryUserListByCondition(UserQueryDTO condition) {
        return baseMapper.selectListByCondition(condition);
    }

    @Override
    public IPage<UserInfo> queryUserPageByCondition(UserQueryDTO condition) {

        Page<UserInfo> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        return baseMapper.selectPageByCondition(page, condition);
    }

    @Override
    public String login(UserLoginDTO req) {

        // 根据邮箱查询用户
        UserInfo userInfo = userInfoMapper.selectByEmail(req.getEmail());

        if (userInfo == null) {
            throw new RuntimeException("用户不存在");
        }

        //验证密码
//        if (!passwordEncoder.matches(req.getPassword(), userInfo.getPassword())) {
//            throw new RuntimeException("密码错误");
//        }
        if (!req.getPassword().equals(userInfo.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        //检查用户状态
        if (userInfo.getStatus() == 0) {
            throw new RuntimeException("用户状态异常，已禁用");
        }

        //生成JWT Token
        String token = JwtUtil.generateToken(userInfo.getUserId(), userInfo.getUsername());

        log.info("用户登录成功：{}", userInfo.getUserId());

        return token;
    }


}
