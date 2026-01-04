package com.wwd.orderapi.dto;

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
public class OrderOperateDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度2-20位")
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String phone;

    private String status;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;

    private String remark;
    private String order_source;
    private String order_status;
    private Long payment_id;
    private Long consignee_id;
    private Long shipping_id;
    private Long userId;
}
