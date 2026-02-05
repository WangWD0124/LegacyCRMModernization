package com.wwd.customer.consumer;

import com.wwd.common.dto.BusinessResult;
import com.wwd.common.dto.IdempotentCheckResult;
import com.wwd.common.enums.BusinessResultEnun;
import com.wwd.customer.entity.MessageIdempotent;
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
import java.util.Map;
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

        // 关键点：获取分布式锁，防止并发处理
        if (!tryLock(messageId)) {
            log.warn("获取锁失败，消息可能正在被处理: {}", messageId);
            return;
        }

        // 关键点：消费幂等性检查（防止重复消费）
        IdempotentCheckResult idempotentCheckResult = checkMessage(messageId);


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

        String lockKey = buildRedisKey(messageId);

        /**
         * setIfAbsent 该方法如果键值对不存在则设置缓存，若存在则设置失败，返回设置结果
         *
         * 该操作属于原子性操作，利用键值对在缓存中的唯一性，实现分布式锁
         *
         * 设置过期时间，能避免死锁
         *
         * 但由于业务执行时长可鞥超过有效期，这里有待优化
         *
         * 该操作涉及外部服务，会产生IO操作，可能会因为网络问题等造成异常，故需要容错处理
         */
        try {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, Thread.currentThread().getName(), 10, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            log.error(LOG_HEAD, "获取分布式锁异常，messageId:" + messageId, e);
            return false;
        }

    }

    /**
     * 消费幂等性检查（防止重复消费）
     * @param messageId
     * @return
     */
    private IdempotentCheckResult checkMessage(String messageId) {

//        // 第1级：Redis布隆过滤器（防止缓存穿透）
//        if (bloomFilterCheck(messageId)) {
//            log.info("消息已处理，幂等跳过: {}", messageId);
//            return true;
//        }

        // 第2级：Redis精确匹配（快速返回）
        IdempotentCheckResult idempotentCheckResult = checkRedis(messageId);
        if (idempotentCheckResult != null) {
            return idempotentCheckResult;
        }

        // 第3级. 检查数据库唯一性幂等性
        idempotentCheckResult = checkDatabase(messageId);
        return idempotentCheckResult;
    }

    private IdempotentCheckResult checkDatabase(String messageId) {

        MessageIdempotent messageIdempotent = messageIdempotentService.getByMessageId(messageId);
        switch (messageIdempotent.getStatus()){
            case IdempotentCheckResult.CheckStatus.PROCESSED_SUCCESS:
                return IdempotentCheckResult.success(messageId, "数据库检查_已处理成功");
        }
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
     * @param messageId
     * @return
     */
    private IdempotentCheckResult checkRedis(String messageId) {

        log.info(LOG_HEAD, "开始Redis缓存精准检查");
        try {
            String redisKey = buildRedisKey(messageId);
            Map idempotentValue = redisTemplate.opsForHash().entries(redisKey);

            if (idempotentValue.isEmpty()) {
                return IdempotentCheckResult.notProcessed(messageId);
            }
            String status = (String) idempotentValue.get("status");
            IdempotentCheckResult.CheckStatus checkStatus = IdempotentCheckResult.CheckStatus.valueOf(status);

            switch (checkStatus) {
                case PROCESSING:
                    log.info("Redis检查_正在处理，{}", messageId);
                    return IdempotentCheckResult.success(messageId, "Redis检查_正在处理");
                case PROCESSED_SUCCESS:
                    log.info("Redis检查_已处理成功，{}", messageId);
                    return IdempotentCheckResult.success(messageId, "Redis检查_已处理成功");
                case PROCESSED_FAILED:
                    log.info("Redis检查_已处理失败，{}", messageId);
                    /**
                     * 重试机制
                     */
                    Integer retryCount = (Integer) idempotentValue.get("retry_count");
                    if (retryCount != null && retryCount < 3) {
                        return IdempotentCheckResult.failedRetry(messageId, "Redis检查_已处理失败_可重试");
                    }
                    return IdempotentCheckResult.failed(messageId, "Redis检查_已处理失败_不可重试");
                default:
                    return null;
            }

        } catch (Exception e) {
            log.error("Redis检查异常，降级到数据库检查");
            return null;
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
