package com.wwd.customer.mapper;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.mapper.MessageIdempotentMapper
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-02-02
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-02-02     wangwd7          v1.0.0               创建
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wwd.customer.entity.MessageIdempotent;
import com.wwd.customerapi.dto.MessageIdempotentQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息幂等性记录表 Mapper 接口
 */
@Mapper
public interface MessageIdempotentMapper extends BaseMapper<MessageIdempotent> {

    /**
     * 根据消息ID查询
     */
    @Select("SELECT * FROM message_idempotent WHERE message_id = #{messageId} AND is_deleted = 0")
    MessageIdempotent selectByMessageId(@Param("messageId") String messageId);

    /**
     * 根据业务类型和业务ID查询
     */
    @Select("SELECT * FROM message_idempotent WHERE business_type = #{businessType} " +
            "AND business_id = #{businessId} AND is_deleted = 0 LIMIT 1")
    MessageIdempotent selectByBusiness(@Param("businessType") String businessType,
                                       @Param("businessId") String businessId);

    /**
     * 根据状态查询列表
     */
    @Select("SELECT * FROM message_idempotent WHERE status = #{status} AND is_deleted = 0")
    List<MessageIdempotent> selectByStatus(@Param("status") String status);

    /**
     * 更新状态和重试次数
     */
    int updateStatusAndRetry(@Param("messageId") String messageId,
                             @Param("newStatus") String newStatus,
                             @Param("processResult") String processResult,
                             @Param("incrementRetry") Integer incrementRetry,
                             @Param("lastTryTime") Date lastTryTime);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("messageIds") List<String> messageIds,
                          @Param("newStatus") String newStatus);

    /**
     * 查询需要重试的消息
     */
    List<MessageIdempotent> selectForRetry(@Param("maxRetryCount") Integer maxRetryCount,
                                           @Param("beforeTime") Date beforeTime,
                                           @Param("statusList") List<String> statusList);

    /**
     * 条件分页查询
     */
    IPage<MessageIdempotent> selectByPage(IPage<MessageIdempotent> page,
                                          @Param("query") MessageIdempotentQueryDTO query);

    /**
     * 统计各个状态的数量
     */
    List<Map<String, Object>> countByStatus(@Param("serviceName") String serviceName,
                                            @Param("businessType") String businessType,
                                            @Param("startTime") Date startTime,
                                            @Param("endTime") Date endTime);

    /**
     * 清理过期数据
     */
    int deleteExpired(@Param("expireDays") Integer expireDays,
                      @Param("statusList") List<String> statusList);

    /**
     * 根据消息ID和版本号更新（乐观锁）
     */
    int updateWithVersion(MessageIdempotent entity);
}
