package com.wwd.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwd.common.enums.MessageStatus;
import com.wwd.customer.entity.MessageIdempotent;
import com.wwd.customer.mapper.MessageIdempotentMapper;
import com.wwd.customer.service.MessageIdempotentService;
import com.wwd.customerapi.dto.MessageIdempotentQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.service.impl.MessageIdempotentServiceImpl
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
@Slf4j
public class MessageIdempotentServiceImpl extends ServiceImpl<MessageIdempotentMapper, MessageIdempotent> implements MessageIdempotentService {

    private static final String LOG_HEAD = "MessageIdempotentServiceImpl>>>{}";

    @Override
    public MessageIdempotent getByMessageId(String messageId) {
        return baseMapper.selectByMessageId(messageId);
    }

    @Override
    public MessageIdempotent getByBusiness(String businessType, String businessId) {
        return null;
    }

    @Override
    public boolean isMessageProcessed(String messageId) {

        MessageIdempotent messageIdempotent = getByMessageId(messageId);
        if (messageIdempotent != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean markAsProcessing(String messageId, String businessId, String businessType, String serviceName) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsCompleted(String messageId, boolean success, String result) {

        try {
            int updateRow = baseMapper.updateStatusAndRetry(
                    messageId,
                    success ? MessageStatus.SUCCESS.getCode() : MessageStatus.FAILED.getCode(),
                    result,
                    0,
                    new Date()
            );
            return updateRow > 0;
        } catch (Exception e) {
            log.error(LOG_HEAD, "标记消息完成失败:"+messageId, e);
            return false;
        }
    }

    @Override
    public boolean incrementRetry(String messageId, String newStatus, String result) {
        return false;
    }

    @Override
    public IPage<MessageIdempotent> queryByPage(MessageIdempotentQueryDTO query) {
        return null;
    }

    @Override
    public List<MessageIdempotent> getMessagesForRetry(Integer maxRetryCount, Date beforeTime) {
        return null;
    }

    @Override
    public int cleanupExpiredData(Integer expireDays) {
        return 0;
    }

    @Override
    public Map<String, Object> getStatistics(String serviceName, String businessType, Date startTime, Date endTime) {
        return null;
    }
}
