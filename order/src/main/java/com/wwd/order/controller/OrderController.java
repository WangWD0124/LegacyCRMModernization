package com.wwd.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.common.dto.PageResult;
import com.wwd.common.dto.Result;
import com.wwd.order.entity.OrderInfo;
import com.wwd.order.service.OrderService;
import com.wwd.orderapi.api.OrderServiceClient;
import com.wwd.orderapi.dto.OrderDTO;
import com.wwd.orderapi.dto.OrderOperateDTO;
import com.wwd.orderapi.dto.OrderQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.controller.OrderController
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
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController implements OrderServiceClient {

    private final OrderService orderService;


    @Override
    public Result<Long> createOrder(OrderOperateDTO orderOperateDTO) {

        // DTO转换
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderOperateDTO, orderInfo);

        //调用业务层类方法
        Long orderId = orderService.createOrder(orderInfo);
        return Result.success(orderId);
    }

    @Override
    public Result<Integer> updateOrder(OrderOperateDTO orderOperateDTO) {

        // DTO转换
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderOperateDTO, orderInfo);

        //调用业务层类方法
        Integer rows = orderService.updateOrder(orderInfo);
        return Result.success(rows);
    }

    @Override
    public Result<Integer> deleteOrderByOrderId(Long orderId) {

        Integer rows = orderService.deleteOrderByOrderId(orderId);
        return Result.success(rows);
    }

    @Override
    public Result<OrderDTO> queryOrderByOrderId(Long orderId) {

        OrderDTO orderDTO = new OrderDTO();
        OrderInfo orderInfo = orderService.queryOrderByOrderId(orderId);
        BeanUtils.copyProperties(orderInfo, orderDTO);
        return Result.success(orderDTO);
    }

    @Override
    public Result<List<OrderDTO>> queryOrderListByCondition(OrderQueryDTO orderQueryDTO) {

        List<OrderDTO> orderDTOs = new ArrayList<>();
        List<OrderInfo> orderInfos = orderService.queryOrderListByCondition(orderQueryDTO);
        BeanUtils.copyProperties(orderInfos, orderDTOs);
        return Result.success(orderDTOs);
    }

    @Override
    public Result<PageResult<OrderDTO>> queryOrderPageByCondition(OrderQueryDTO orderQueryDTO) {

        IPage<OrderInfo> page = orderService.queryOrderPageByCondition(orderQueryDTO);
        List<OrderDTO> orderDTOs = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PageResult<OrderDTO> orderPage = new PageResult<OrderDTO>(
                orderDTOs,
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
        return Result.success(orderPage);
    }

    /**
     * Entity 转 DTO
     */
    private OrderDTO convertToDTO(OrderInfo orderInfo) {
        if (orderInfo == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(orderInfo, dto);
        return dto;
    }
}
