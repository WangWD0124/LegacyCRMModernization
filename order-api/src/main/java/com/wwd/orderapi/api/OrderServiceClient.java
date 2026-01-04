package com.wwd.orderapi.api;

import com.wwd.common.constant.ServiceNamesConstant;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.orderapi.dto.OrderDTO;
import com.wwd.orderapi.dto.OrderOperateDTO;
import com.wwd.orderapi.dto.OrderQueryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customerapi.api.OrderServiceClient
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
        name = ServiceNamesConstant.ORDER_SERVICE,
        path = "/api/order"
)
public interface OrderServiceClient {

    // 创建用户
    @PostMapping
    Result<Long> createOrder(@RequestBody @Valid OrderOperateDTO req);

    // 更新用户
    @PutMapping
    Result<Integer> updateOrder(@RequestBody @Valid OrderOperateDTO req);

    // 删除用户
    @DeleteMapping("/{orderId}")
    Result<Integer> deleteOrderByOrderId(@PathVariable Long orderId);

    // 根据ID获取用户
    @GetMapping("/orderId/{orderId}")
    Result<OrderDTO> queryOrderByOrderId(@PathVariable Long orderId);

    // 获取用户列表
    @GetMapping
    Result<List<OrderDTO>> queryOrderListByCondition(@SpringQueryMap OrderQueryDTO req);

    // 分页查询用户
    @GetMapping("/page")
    Result<PageResult<OrderDTO>> queryOrderPageByCondition(@SpringQueryMap OrderQueryDTO req);
}
