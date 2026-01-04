package com.wwd.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.order.entity.OrderInfo;
import com.wwd.order.mapper.OrderInfoMapper;
import com.wwd.orderapi.dto.OrderQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.order.service.OrderServiceImpl
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
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;


    @Value("${app.default-order-status.ACTIVE}")
    private Integer defaultOrderStatus;

    //private final PasswordEncoder passwordEncoder;

    @Override
    public Long createOrder(OrderInfo orderInfo) {

        //order.setPassword(passwordEncoder.encode(order.getPassword()));
        orderInfo.setOrderStatus(defaultOrderStatus);
        orderInfo.setCreateTime(LocalDateTime.now());
        orderInfo.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(orderInfo);
        return orderInfo.getOrderId();
    }

    @Override
    public int updateOrder(OrderInfo orderInfo) {

        orderInfo.setUpdateTime(LocalDateTime.now());

        return baseMapper.updateById(orderInfo);
    }

    @Override
    public int deleteOrderByOrderId(Long orderId) {
        return baseMapper.deleteById(orderId);
    }

    @Override
    public OrderInfo queryOrderByOrderId(Long orderId) {
        return baseMapper.selectById(orderId);
    }

    @Override
    public List<OrderInfo> queryOrderListByCondition(OrderQueryDTO condition) {
        return baseMapper.selectListByCondition(condition);
    }

    @Override
    public IPage<OrderInfo> queryOrderPageByCondition(OrderQueryDTO condition) {

        Page<OrderInfo> page = new Page<>(condition.getPageNum(), condition.getPageSize());
        return baseMapper.selectPageByCondition(page, condition);
    }




}
