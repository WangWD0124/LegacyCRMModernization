package com.wwd.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.customer.entity.UserInfo;
import com.wwd.customerapi.dto.UserLoginDTO;
import com.wwd.customerapi.dto.UserQueryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.service.UserService
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
public interface UserService {

    Long createUser(UserInfo userInfo);

    int updateUser(UserInfo userInfo);

    int deleteUserByUserId(Long userId);

    UserInfo queryUserByUserId(Long userId);

    List<UserInfo> queryUserListByCondition(UserQueryDTO userQueryDTO);

    IPage<UserInfo> queryUserPageByCondition(UserQueryDTO userQueryDTO);

    String login(UserLoginDTO req);
}
