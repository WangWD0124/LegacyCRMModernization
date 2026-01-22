package com.wwd.customerapi.api;

import com.wwd.common.constant.ServiceNamesConstant;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.customerapi.dto.UserDTO;
import com.wwd.customerapi.dto.UserLoginDTO;
import com.wwd.customerapi.dto.UserQueryDTO;
import com.wwd.customerapi.dto.UserOperateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customerapi.api.UserServiceClient
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-12-12
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-12-12     wangwd7          v1.0.0               创建
 */
@FeignClient(
        name = ServiceNamesConstant.CUSTOMER_SERVICE,
        path = "/api/customer/user"
)
public interface UserServiceClient {

    // 创建or更新用户
    @PostMapping("/operate")
    Result<Long> operateUser(@RequestBody @Valid UserOperateDTO req);

    // 删除用户
    @DeleteMapping("/delete/{userId}")
    Result<Integer> deleteUserByUserId(@PathVariable Long userId);

    // 根据ID获取用户
    @GetMapping("/userId/{userId}")
    Result<UserDTO> queryUserByUserId(@PathVariable Long userId);

    // 获取用户列表
    @GetMapping("/list")
    Result<List<UserDTO>> queryUserListByCondition(@SpringQueryMap UserQueryDTO req);

    // 分页查询用户
    @GetMapping("/page")
    Result<PageResult<UserDTO>> queryUserPageByCondition(UserQueryDTO req);

    @PostMapping("/login")
    Result<String> login(@RequestBody @Valid UserLoginDTO req);
}
