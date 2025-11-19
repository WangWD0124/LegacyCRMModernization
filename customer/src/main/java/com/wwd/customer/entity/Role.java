package com.wwd.customer.entity;

import lombok.Data;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.entity.Role
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-11-19
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-11-19     wangwd7          v1.0.0               创建
 */
@Data
public class Role {

    private Long roleId;
    private String roleName;
    private String description;

}
