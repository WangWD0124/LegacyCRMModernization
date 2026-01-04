package com.wwd.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wwd.order.entity.OrderInfo;
import com.wwd.orderapi.dto.OrderQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.mapper.OrderMapper
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
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    // ========== 继承 BaseMapper 已有方法 ==========
    // selectById, selectBatchIds, selectByMap, selectOne, selectList, selectPage
    // insert, updateById, deleteById, deleteByMap, deleteBatchIds

    /**
     * 复杂查询：使用XML配置
     * 参数使用 @Param 注解，XML中通过 condition.xxx 访问
     */
    List<OrderInfo> selectListByCondition(@Param("condition") OrderQueryDTO condition);

    /**
     * 分页查询：使用XML配置
     */
    IPage<OrderInfo> selectPageByCondition(Page<OrderInfo> page, @Param("condition") OrderQueryDTO condition);

}