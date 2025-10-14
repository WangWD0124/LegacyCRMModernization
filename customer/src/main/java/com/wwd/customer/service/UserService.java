package com.wwd.customer.service;

import com.wwd.customer.entity.User;

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
public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User findByUsername(String username);

    boolean save(User user);

    boolean update(User user);

    boolean delete(Long id);

    Map<String, Object> findByPage(String username, Integer status, Integer page, Integer size);
}
