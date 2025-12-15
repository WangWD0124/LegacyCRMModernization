package com.wwd.customerapi.api;

import com.wwd.common.constant.ServiceNamesConstant;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.customerapi.dto.UserDTO;
import com.wwd.customerapi.dto.UserQueryDTO;
import com.wwd.customerapi.dto.UserOperateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        path = "/api/users"
)
public interface UserServiceClient {

    // 创建用户
    @PostMapping
    Result<Long> createUser(@RequestBody @Valid UserOperateDTO req);

    // 更新用户
    @PutMapping
    Result<Integer> updateUser(@RequestBody @Valid UserOperateDTO req);

    // 删除用户
    @DeleteMapping("/{userId}")
    Result<Integer> deleteUserByUserId(@PathVariable Long userId);

    // 根据ID获取用户
    @GetMapping("/userId/{userId}")
    Result<UserDTO> queryUserByUserId(@PathVariable Long userId);

    // 获取用户列表
    @GetMapping
    Result<List<UserDTO>> queryUserListByCondition(@SpringQueryMap UserQueryDTO req);

    // 分页查询用户
    @GetMapping("/page")
    Result<PageResult<UserDTO>> queryUserPageByCondition(@SpringQueryMap UserQueryDTO req);
}
