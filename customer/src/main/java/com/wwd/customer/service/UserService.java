package com.wwd.customer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.customer.entity.User;
import com.wwd.customerapi.dto.UserQueryDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    Long createUser(User user);

    int updateUser(User user);

    int deleteUserByUserId(Long userId);

    User queryUserByUserId(Long userId);

    List<User> queryUserListByCondition(UserQueryDTO userQueryDTO);

    IPage<User> queryUserPageByCondition(UserQueryDTO userQueryDTO);

}
