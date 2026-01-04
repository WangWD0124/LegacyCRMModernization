package com.wwd.customerapi.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customerapi.dto.UserCreateReq
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-12-13
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-12-13     wangwd7          v1.0.0               创建
 */
@Data
public class UserLoginDTO {

    private Long userId;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;

}
