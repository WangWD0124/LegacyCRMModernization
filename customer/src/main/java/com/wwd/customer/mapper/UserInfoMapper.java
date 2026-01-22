package com.wwd.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wwd.customer.entity.UserInfo;
import com.wwd.customerapi.dto.UserQueryDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.customer.mapper.UserMapper
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
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    // ========== 继承 BaseMapper 已有方法 ==========
    // selectById, selectBatchIds, selectByMap, selectOne, selectList, selectPage
    // insert, updateById, deleteById, deleteByMap, deleteBatchIds

    UserInfo selectByUserId(@Param("userId") Long userId);

    UserInfo selectByEmail(@Param("email") String email);

    /**
     * 复杂查询：使用XML配置
     * 参数使用 @Param 注解，XML中通过 condition.xxx 访问
     */
    List<UserInfo> selectListByCondition(@Param("condition") UserQueryDTO condition);

    /**
     * 分页查询：使用XML配置
     */
    IPage<UserInfo> selectPageByCondition(Page<UserInfo> page, @Param("condition") UserQueryDTO condition);

}