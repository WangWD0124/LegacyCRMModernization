package com.wwd.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wwd.customer.entity.FundAccount;
import com.wwd.customerapi.dto.FundAccountQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.mapper.FundAccountMapper
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-04
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-04     wangwd7          v1.0.0               创建
 */
@Mapper
public interface FundAccountMapper extends BaseMapper<FundAccount> {

    // ========== 继承 BaseMapper 已有方法 ==========
    // selectById, selectBatchIds, selectByMap, selectOne, selectList, selectPage
    // insert, updateById, deleteById, deleteByMap, deleteBatchIds

    FundAccount selectByFundAccountId(@Param("account_id") Long account_id);

    FundAccount selectByAccountCode(@Param("account_code") String account_code);

    IPage<FundAccount> selectPageByCondition(Page<FundAccount> page, FundAccountQueryDTO condition);
}
