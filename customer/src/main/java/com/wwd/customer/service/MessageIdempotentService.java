package com.wwd.customer.service;

import com.wwd.customer.entity.MessageIdempotent;
import com.wwd.customerapi.dto.MessageIdempotentQueryDTO;
import org.springframework.stereotype.Service;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.service.MessageIdempotentService
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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息幂等性记录表 Service 接口
 */
@Service
public interface MessageIdempotentService extends IService<MessageIdempotent> {

    /**
     * 根据消息ID查询
     */
    MessageIdempotent getByMessageId(String messageId);

    /**
     * 根据业务类型和业务ID查询
     */
    MessageIdempotent getByBusiness(String businessType, String businessId);

    /**
     * 检查消息是否已处理
     */
    boolean isMessageProcessed(String messageId);

    /**
     * 标记消息为处理中
     */
    boolean markAsProcessing(String messageId, String businessId, String businessType, String serviceName);

    /**
     * 标记消息处理完成
     */
    boolean markAsCompleted(String messageId, boolean success, String result);

    /**
     * 增加重试次数
     */
    boolean incrementRetry(String messageId, String newStatus, String result);

    /**
     * 条件分页查询
     */
    IPage<MessageIdempotent> queryByPage(MessageIdempotentQueryDTO query);

    /**
     * 查询需要重试的消息
     */
    List<MessageIdempotent> getMessagesForRetry(Integer maxRetryCount, Date beforeTime);

    /**
     * 清理过期数据
     */
    int cleanupExpiredData(Integer expireDays);

    /**
     * 获取统计信息
     */
    Map<String, Object> getStatistics(String serviceName, String businessType, Date startTime, Date endTime);
}
