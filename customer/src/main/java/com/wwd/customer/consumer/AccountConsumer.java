package com.wwd.customer.consumer;

import com.wwd.common.dto.BusinessResult;
import com.wwd.common.enums.BusinessResultEnun;
import com.wwd.customer.message.AccountAddMessage;
import com.wwd.customer.message.AccountDeductMessage;
import com.wwd.customer.service.FundAccountService;
import com.wwd.customer.service.MessageIdempotentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.customer.consumer.FundAccountConsumer
 * @Description: 账户消息队列消费类
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-02-02
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-02-02     wangwd7          v1.0.0               创建
 */
@Component
@Slf4j
public class AccountConsumer {

    private static final String LOG_HEAD = "AccountConsumer>>>{}";
    private static final String LOCK_KEY_PREFIX = "idempotent:customer:";

    @Autowired
    private MessageIdempotentService messageIdempotentService;

    @Autowired
    private FundAccountService fundAccountService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 监听处理打款消息
     * @param message
     */
    @RabbitListener(queues = "account.add.queue")
    @Transactional(rollbackFor = Exception.class)
    public void processAddMessage(AccountAddMessage message) {

        // 关键点1：如何消费消息
        String messageId = message.getMessageId();

        log.info("收到打款消息，消息ID: {}，收入记录ID：{}", messageId, message.getIncomeId());

        // 获取分布式锁，防止并发处理
        if (tryLock(messageId))

        // 关键点2：消费幂等性检查（防止重复消费）
        if (checkMessage(message.getMessageId())) {
            log.warn("消息已处理过，跳过: {}", message.getMessageId());
            return;
        }

        try {
            // 关键点3：执行业务逻辑
            BusinessResult<String> businessResult = fundAccountService.addBalance(
                    message.getAccountId(),
                    message.getAmount()
            );

            boolean isSuccess = true;
            if (businessResult.getResult() != BusinessResultEnun.SUCCESS) {
                log.info(LOG_HEAD, "执行打款操作返回失败，原因：" + businessResult.getData());
                isSuccess = false;
            }

            //打款成功
            log.info(LOG_HEAD, "执行打款操作返回成功，账户余额（单位：元）：" + businessResult.getData());

            // 关键点4：发送结果消息
            //sendDeductResult(message, success);

            // 关键点5：记录消息已处理
            messageIdempotentService.markAsCompleted(
                    message.getMessageId(),
                    isSuccess,
                    businessResult.getResult().getDesc()
            );
        } catch (Exception e) {
            log.error("处理扣款消息失败: {}", message.getMessageId(), e);
            // 关键点6：异常处理 - 重试 or 进入死信队列
        }

    }

    /**
     * 获取分布式锁
     * @param messageId
     * @return
     */
    private boolean tryLock(String messageId) {

        String lockKey = LOCK_KEY_PREFIX + messageId;

        /**
         *
         */
        try {

        }
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, Thread.currentThread().getName(), 10, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);


    }

    /**
     * 消费幂等性检查（防止重复消费）
     * @param messageId
     * @return
     */
    private boolean checkMessage(String messageId) {

        // 第1级：Redis布隆过滤器（防止缓存穿透）
        if (bloomFilterCheck(messageId)) {
            log.info("消息已处理，幂等跳过: {}", messageId);
            return true;
        }

        // 第2级：Redis精确匹配（快速返回）
        String redisKey = buildRedisKey(messageId);
        if (checkRedis(redisKey)) {
            log.info("消息已处理，幂等跳过: {}", messageId);
            return true;
        }

        // 第3级. 检查数据库唯一性幂等性
        if (messageIdempotentService.isMessageProcessed(messageId)) {
            log.info("消息已处理，幂等跳过: {}", messageId);
            return true;
        }

        // 消息不存在，未被处理
        return false;
    }

    /**
     * 第1级：布隆过滤器（防穿透，判断"一定不存在"）
     */
    private boolean bloomFilterCheck(String messageId) {

        log.info(LOG_HEAD, "开始布隆过滤器检查");
        try {
            // 企业常用方案：Redisson布隆过滤器
            // 这里简化，实际使用RBloomFilter

            // 布隆过滤器判断可能存在
        } catch (Exception e) {
            log.error("布隆过滤器检查异常，降级到Redis精准检查");
            return false;
        } finally {
            log.info(LOG_HEAD, "结束布隆过滤器检查");
        }
        return true;
    }

    /**
     * 第2级：Redis精确匹配（快速返回）
     * @param redisKey
     * @return
     */
    private boolean checkRedis(String redisKey) {

        log.info(LOG_HEAD, "开始Redis缓存精准检查");
        try {
            String status = (String) redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.hasText(status)) {
                return true;
            }
            // 消息不存在，未被处理
            return false;
        } catch (Exception e) {
            log.error("Redis检查异常，降级到数据库检查");
            return false;
        } finally {
            log.info(LOG_HEAD, "结束Redis缓存精准检查");
        }
    }

    /**
     * 构建缓存键值对key
     * @param messageId
     * @return
     */
    private String buildRedisKey(String messageId) {

        return LOCK_KEY_PREFIX + messageId;
    }


    @RabbitListener(queues = "account.deduct.queue")
    public void processDeductMessage(AccountDeductMessage message) {


    }

}
