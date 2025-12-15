package com.wwd.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.customer.entity.User;
import com.wwd.customer.service.UserService;
import com.wwd.customerapi.api.UserServiceClient;
import com.wwd.customerapi.dto.UserDTO;
import com.wwd.customerapi.dto.UserOperateDTO;
import com.wwd.customerapi.dto.UserQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.controller.UserController
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
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserServiceClient {

    private final UserService userService;


    @Override
    public Result<Long> createUser(UserOperateDTO userOperateDTO) {

        // DTO转换
        User user = new User();
        BeanUtils.copyProperties(userOperateDTO, user);

        //调用业务层类方法
        Long userId = userService.createUser(user);
        return Result.success(userId);
    }

    @Override
    public Result<Integer> updateUser(UserOperateDTO userOperateDTO) {

        // DTO转换
        User user = new User();
        BeanUtils.copyProperties(userOperateDTO, user);

        //调用业务层类方法
        Integer rows = userService.updateUser(user);
        return Result.success(rows);
    }

    @Override
    public Result<Integer> deleteUserByUserId(Long userId) {

        Integer rows = userService.deleteUserByUserId(userId);
        return Result.success(rows);
    }

    @Override
    public Result<UserDTO> queryUserByUserId(Long userId) {

        UserDTO userDTO = new UserDTO();
        User user = userService.queryUserByUserId(userId);
        BeanUtils.copyProperties(user, userDTO);
        return Result.success(userDTO);
    }

    @Override
    public Result<List<UserDTO>> queryUserListByCondition(UserQueryDTO userQueryDTO) {

        List<UserDTO> userDTOs = new ArrayList<>();
        List<User> users = userService.queryUserListByCondition(userQueryDTO);
        BeanUtils.copyProperties(users, userDTOs);
        return Result.success(userDTOs);
    }

    @Override
    public Result<PageResult<UserDTO>> queryUserPageByCondition(UserQueryDTO userQueryDTO) {

        IPage<User> page = userService.queryUserPageByCondition(userQueryDTO);
        List<UserDTO> userDTOs = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PageResult<UserDTO> userPage = new PageResult<UserDTO>(
                userDTOs,
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return Result.success(userPage);
    }

    /**
     * Entity 转 DTO
     */
    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
