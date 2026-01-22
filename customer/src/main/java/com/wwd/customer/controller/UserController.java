package com.wwd.customer.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.customer.entity.UserInfo;
import com.wwd.customer.service.UserService;
import com.wwd.customerapi.api.UserServiceClient;
import com.wwd.customerapi.dto.UserDTO;
import com.wwd.customerapi.dto.UserLoginDTO;
import com.wwd.customerapi.dto.UserOperateDTO;
import com.wwd.customerapi.dto.UserQueryDTO;
import lombok.RequiredArgsConstructor;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
@RequestMapping("/api/customer/user")
@RequiredArgsConstructor
public class UserController implements UserServiceClient {

    private final UserService userService;


    @Override
    public Result<Long> operateUser(UserOperateDTO userOperateDTO) {

        // DTO转换
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userOperateDTO, userInfo);

        //调用业务层类方法
        if (userInfo.getUserId() != null){
            userService.updateUser(userInfo);
            return Result.success(userInfo.getUserId());
        } else {
            Long userId = userService.createUser(userInfo);
            return Result.success(userId);
        }
    }

    @Override
    public Result<Integer> deleteUserByUserId(Long userId) {

        Integer rows = userService.deleteUserByUserId(userId);
        return Result.success(rows);
    }

    @Override
    public Result<UserDTO> queryUserByUserId(Long userId) {

        UserDTO userDTO = new UserDTO();
        UserInfo userInfo = userService.queryUserByUserId(userId);
        BeanUtils.copyProperties(userInfo, userDTO);
        return Result.success(userDTO);
    }

    @Override
    public Result<List<UserDTO>> queryUserListByCondition(UserQueryDTO userQueryDTO) {

        List<UserDTO> userDTOs = new ArrayList<>();
        List<UserInfo> userInfos = userService.queryUserListByCondition(userQueryDTO);
        BeanUtils.copyProperties(userInfos, userDTOs);
        return Result.success(userDTOs);
    }

    @Override
    public Result<PageResult<UserDTO>> queryUserPageByCondition(UserQueryDTO userQueryDTO) {

        IPage<UserInfo> page = userService.queryUserPageByCondition(userQueryDTO);
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

    @Override
    public Result<String> login(UserLoginDTO req) {

        String token = userService.login(req);
        return Result.success(token);
    }

    /**
     * Entity 转 DTO
     */
    private UserDTO convertToDTO(UserInfo userInfo) {
        if (userInfo == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(userInfo, dto);
        return dto;
    }
}
