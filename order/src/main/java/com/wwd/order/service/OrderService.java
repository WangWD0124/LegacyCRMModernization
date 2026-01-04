package com.wwd.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.order.entity.OrderInfo;
import com.wwd.orderapi.dto.OrderQueryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.order.service.OrderService
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
public interface OrderService {

    Long createOrder(OrderInfo userInfo);

    int updateOrder(OrderInfo userInfo);

    int deleteOrderByOrderId(Long userId);

    OrderInfo queryOrderByOrderId(Long userId);

    List<OrderInfo> queryOrderListByCondition(OrderQueryDTO userQueryDTO);

    IPage<OrderInfo> queryOrderPageByCondition(OrderQueryDTO userQueryDTO);

}
