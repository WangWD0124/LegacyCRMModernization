package com.wwd.customer.consumer;

import com.wwd.common.dto.BusinessResult;
import com.wwd.common.dto.IdempotentCheckResult;
import com.wwd.common.enums.BusinessResultEnun;
import com.wwd.common.message.AccountIncomeExpenseMessage;
import com.wwd.customer.entity.MessageIdempotent;
import com.wwd.customer.service.FundAccountService;
import com.wwd.customer.service.MessageIdempotentService;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.SystemException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
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
    private static final String LOCK_KEY_PREFIX = "idempotent:customer:lock:";
    private static final String CACHE_KEY_PREFIX = "idempotent:customer:check:";
    private static final Integer RETRY_COUNT_LIMIT = 3;
    private static final String STATUS = "status";
    private static final String PROCESS_RESULT = "process_result";
    private static final String RETRY_COUNT = "retry_count";
    private enum BUSINESS_TYPE {
        ACCOUNT_AMOUNT_ADD,
        ACCOUNT_AMOUNT_DEDUCT;
    };

    private long lockExpireTime = 30;

    @Resource
    private MessageIdempotentService messageIdempotentService;

    @Resource
    private FundAccountService fundAccountService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 监听处理打款消息
     * @param message
     */
    @RabbitListener(queues = "account.add.queue")
    public void processAddMessage(AccountIncomeExpenseMessage message) {

        // 关键点1：如何消费消息
        if (message == null) {
            throw new RuntimeException("消息为空");
        }
        String messageId = message.getMessageId();

        log.info("[账户消费者][打款处理]监听到打款消息，消息ID: {}，收入记录ID：{}", messageId, message.getBusinessId());

        try {

            // 关键点：获取分布式锁，防止并发处理（合理设置过期时间）
            if (!tryLock(messageId)) {
                log.warn("[账户消费者][打款处理]获取锁失败，消息可能正在被处理: 消息ID：{}", messageId);
                return;
            }
            log.info("[账户消费者][打款处理]获取锁成功: 消息ID：{}，线程：{}", messageId, Thread.currentThread().getName());


            // 关键点：消费幂等性检查，防止重复消费
            IdempotentCheckResult idempotentCheckResult = checkMessage(messageId);

            log.info("[账户消费者][打款处理]消息幂等性检查结果: 消息ID：{}，状态：{}", messageId, idempotentCheckResult.getStatus());

            if (!idempotentCheckResult.canProcess()){
                log.info("[账户消费者][打款处理]无法消费消息，消息ID：{}，", messageId);
                return;
            }

            Integer retryCount = idempotentCheckResult.getRetryCount();
            if (idempotentCheckResult.isFailedCanRetry()){
                retryCount++;
            }

            // 关键点：标记状态
            log.info("[账户消费者][打款处理]标记正在处理，消息ID：{}，", messageId);
            IdempotentCheckResult processing = IdempotentCheckResult.builder()
                    .status(IdempotentCheckResult.CheckStatus.PROCESSING)
                    .processResult("正在处理")
                    .retryCount(retryCount)
                    .build();

            boolean isMarkSuccess = markMessageIdempotent(message, processing);
            if (!isMarkSuccess){
                log.warn("[账户消费者][打款处理]标记正在处理，标记失败，消息ID：{}，", messageId);
                throw new RuntimeException("[账户消费者][打款处理]标记正在处理，标记失败，消息ID：" + messageId);
            }

            // 关键点：执行业务逻辑
            log.info("[账户消费者][打款处理]执行业务逻辑，消息ID：{}，", messageId);
            BusinessResult<String> businessResult = fundAccountService.addBalance(
                    message.getAccountId(),
                    message.getAmount()
            );

            IdempotentCheckResult processed;
            if (businessResult.getResult().equals(BusinessResultEnun.SUCCESS)) {
                log.info("[账户消费者][打款处理]执行业务逻辑，执行打款操作成功，账户余额（单位：元）：" + businessResult.getData());
                processed = IdempotentCheckResult.builder().status(IdempotentCheckResult.CheckStatus.SUCCESS)
                        .processResult(businessResult.getResult().getDesc())
                        .retryCount(retryCount)
                        .build();
            } else {
                log.warn("[账户消费者][打款处理]执行业务逻辑，执行打款操作失败，消息ID：{}，原因：{}", messageId, businessResult.getResult().getDesc());
                processed = IdempotentCheckResult.builder().status(retryCount >= RETRY_COUNT_LIMIT ? IdempotentCheckResult.CheckStatus.FAILED : IdempotentCheckResult.CheckStatus.FAILED_CAN_RETRY)
                        .processResult(businessResult.getResult().getDesc())
                        .retryCount(retryCount)
                        .build();
            }
            markMessageIdempotent(message, processed);

        } catch (Exception e) {
            log.error("[账户消费者][打款处理]处理扣款消息失败: {}", message.getMessageId(), e);
            // 关键点：异常处理 - 重试 or 进入死信队列
            handleProcessException(message, e);
        } finally {
            // 关键点：释放锁
            unlock(messageId);
        }

    }

    /**
     * 监听处理扣款消息
     * @param message
     */
    @RabbitListener(queues = "account.deduct.queue")
    public void processDeductMessage(AccountIncomeExpenseMessage message) {

        // 关键点1：如何消费消息
        if (message == null) {
            throw new RuntimeException("消息为空");
        }
        String messageId = message.getMessageId();

        log.info("[账户消费者][扣款处理]监听到扣款消息，消息ID: {}，收入记录ID：{}", messageId, message.getBusinessId());

        try {

            // 关键点：获取分布式锁，防止并发处理（合理设置过期时间）
            if (!tryLock(messageId)) {
                log.warn("[账户消费者][扣款处理]获取锁失败，消息可能正在被处理: 消息ID：{}", messageId);
                return;
            }
            log.info("[账户消费者][扣款处理]获取锁成功: 消息ID：{}，线程：{}", messageId, Thread.currentThread().getName());


            // 关键点：消费幂等性检查，防止重复消费
            IdempotentCheckResult idempotentCheckResult = checkMessage(messageId);

            log.info("[账户消费者][扣款处理]消息幂等性检查结果: 消息ID：{}，状态：{}", messageId, idempotentCheckResult.getStatus());

            if (!idempotentCheckResult.canProcess()){
                log.info("[账户消费者][扣款处理]无法消费消息，消息ID：{}，", messageId);
                return;
            }

            Integer retryCount = idempotentCheckResult.getRetryCount();
            if (idempotentCheckResult.isFailedCanRetry()){
                retryCount++;
            }

            // 关键点：标记状态
            log.info("[账户消费者][扣款处理]标记正在处理，消息ID：{}，", messageId);
            IdempotentCheckResult processing = IdempotentCheckResult.builder()
                    .status(IdempotentCheckResult.CheckStatus.PROCESSING)
                    .processResult("正在处理")
                    .retryCount(retryCount)
                    .build();

            boolean isMarkSuccess = markMessageIdempotent(message, processing);
            if (!isMarkSuccess){
                log.warn("[账户消费者][扣款处理]标记正在处理，标记失败，消息ID：{}，", messageId);
                throw new RuntimeException("[账户消费者][扣款处理]标记正在处理，标记失败，消息ID：" + messageId);
            }

            // 关键点：执行业务逻辑
            log.info("[账户消费者][扣款处理]执行业务逻辑，消息ID：{}，", messageId);
            BusinessResult<String> businessResult = fundAccountService.deductBalance(
                    message.getAccountId(),
                    message.getAmount()
            );

            IdempotentCheckResult processed;
            if (businessResult.getResult().equals(BusinessResultEnun.SUCCESS)) {
                log.info("[账户消费者][扣款处理]执行业务逻辑，执行扣款操作成功，账户余额（单位：元）：" + businessResult.getData());
                processed = IdempotentCheckResult.builder().status(IdempotentCheckResult.CheckStatus.SUCCESS)
                        .processResult(businessResult.getResult().getDesc())
                        .retryCount(retryCount)
                        .build();
            } else {
                log.warn("[账户消费者][扣款处理]执行业务逻辑，执行扣款操作失败，消息ID：{}，原因：{}", messageId, businessResult.getResult().getDesc());
                processed = IdempotentCheckResult.builder().status(retryCount >= RETRY_COUNT_LIMIT ? IdempotentCheckResult.CheckStatus.FAILED : IdempotentCheckResult.CheckStatus.FAILED_CAN_RETRY)
                        .processResult(businessResult.getResult().getDesc())
                        .retryCount(retryCount)
                        .build();
            }
            markMessageIdempotent(message, processed);

        } catch (Exception e) {
            log.error("[账户消费者][扣款处理]处理扣款消息失败: {}", message.getMessageId(), e);
            // 关键点：异常处理 - 重试 or 进入死信队列
            handleProcessException(message, e);
        } finally {
            // 关键点：释放锁
            unlock(messageId);
        }

    }
    /**
     * 释放锁
     * @param messageId
     */
    private void unlock(String messageId) {

        String redisKey = buildRedisLockKey(messageId);
        try {
            redisTemplate.delete(redisKey);
        } catch (Exception e) {
            log.error("释放锁异常");
            throw e;
        }
    }

    /**
     * 异常处理策略
     */
    private void handleProcessException(AccountIncomeExpenseMessage message, Exception e) {

        // 判断异常类型，决定是否记录重试次数
        try {
            IdempotentCheckResult idempotentCheckResult = checkMessage(message.getMessageId());
            Integer retryCount = idempotentCheckResult.getRetryCount();

            // 根据异常类型决定是否可以重试
            boolean canRetry = e instanceof SystemException ||
                    (e instanceof RuntimeException && !(e instanceof IllegalArgumentException));

            IdempotentCheckResult processed;
            if (canRetry) {
                // 可重试异常，增加重试次数
                retryCount++;
                processed = IdempotentCheckResult.builder()
                        .status(retryCount >= RETRY_COUNT_LIMIT ?
                                IdempotentCheckResult.CheckStatus.FAILED :
                                IdempotentCheckResult.CheckStatus.FAILED_CAN_RETRY)
                        .processResult("处理异常: " + e.getMessage())
                        .retryCount(retryCount)
                        .build();
            } else {
                // 不可重试异常，直接标记失败
                processed = IdempotentCheckResult.builder()
                        .status(IdempotentCheckResult.CheckStatus.FAILED)
                        .processResult("处理异常: " + e.getMessage())
                        .retryCount(retryCount)
                        .build();
            }
            markMessageIdempotent(message, processed);

        } catch (Exception ex) {
            log.error("处理异常时发生错误", ex);
        }
    }

    private boolean markMessageIdempotent(AccountIncomeExpenseMessage message, IdempotentCheckResult idempotent) {


        log.info("[账户消费者][扣款处理][消息幂等性标记]开始，消息ID：: {}", message.getMessageId());

        try {
            boolean isMarkSuccess = markDatabase(message, idempotent);

            if (!isMarkSuccess) {
                return false;
            }

            return markRedis(message, idempotent);

        } catch (Exception e) {
            log.info("[账户消费者][消息幂等性标记]异常，消息ID：: {}", message.getMessageId());
            return false;
        } finally {
            log.info("[账户消费者][消息幂等性标记]结束，消息ID：: {}", message.getMessageId());
        }
    }

    /**
     * 标记Redis
     * @param message
     * @param idempotent
     * @return
     */
    private boolean markRedis(AccountIncomeExpenseMessage message, IdempotentCheckResult idempotent) {

        log.info("[账户消费者][消息幂等性标记]标记Redis开始，消息ID：: {}", message.getMessageId());

        try {
            String redisKey = buildRedisCheckKey(message.getMessageId());
            redisTemplate.opsForHash().put(redisKey, STATUS, idempotent.getStatus().toString());
            redisTemplate.opsForHash().put(redisKey, PROCESS_RESULT, idempotent.getProcessResult());
            redisTemplate.opsForHash().put(redisKey, RETRY_COUNT, idempotent.getRetryCount());

            // 设置过期时间：成功状态保留24小时，失败状态保留7天，处理中状态保留30分钟
            long expireTime;
            switch (idempotent.getStatus()) {
                case SUCCESS:
                    expireTime = 24 * 60 * 60; // 24小时
                    break;
                case FAILED:
                    expireTime = 7 * 24 * 60 * 60; // 7天
                    break;
                case PROCESSING:
                case FAILED_CAN_RETRY:
                    expireTime = 30 * 60; // 30分钟
                    break;
                default:
                    expireTime = 24 * 60 * 60; // 默认24小时
            }
            redisTemplate.expire(redisKey, expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("[账户消费者][消息幂等性标记]标记Redis异常，消息ID：: {}", message.getMessageId());
            return false;
        } finally {
            log.info("[账户消费者][消息幂等性标记]标记Redis结束，消息ID：: {}", message.getMessageId());
        }


    }

    /**
     * 标记数据库
     * @param message
     * @param idempotent
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    boolean markDatabase(AccountIncomeExpenseMessage message, IdempotentCheckResult idempotent) {

        log.info("[账户消费者][消息幂等性标记]标记数据库开始，消息ID：: {}", message.getMessageId());

        try {
            MessageIdempotent messageIdempotent = messageIdempotentService.getByMessageId(message.getMessageId());

            if (messageIdempotent == null) {
                messageIdempotent = new MessageIdempotent();
                messageIdempotent.setMessageId(message.getMessageId());
                messageIdempotent.setBusinessId(message.getBusinessId());
                messageIdempotent.setBusinessType(BUSINESS_TYPE.ACCOUNT_AMOUNT_ADD.toString());
                messageIdempotent.setStatus(idempotent.getStatus().toString());
                messageIdempotent.setProcessResult(idempotent.getProcessResult());
                messageIdempotent.setCreatedTime(new Date());
                messageIdempotent.setUpdatedTime(new Date());
                return messageIdempotentService.save(messageIdempotent);
            }

            messageIdempotent.setStatus(idempotent.getStatus().toString());
            messageIdempotent.setProcessResult(idempotent.getProcessResult());
            messageIdempotent.setRetryCount(idempotent.getRetryCount());
            messageIdempotent.setUpdatedTime(new Date());
            return messageIdempotentService.updateByMessageId(messageIdempotent);
        } catch (Exception e) {
            log.error("[账户消费者][消息幂等性标记]标记数据库异常，消息ID：: {}", message.getMessageId(), e);
            throw e;
        } finally {
            log.info("[账户消费者][消息幂等性标记]标记数据库结束，消息ID：: {}", message.getMessageId());
        }


    }



    /**
     * 获取分布式锁
     * @param messageId
     * @return
     */
    private boolean tryLock(String messageId) {

        String lockKey = buildRedisLockKey(messageId);

        /**
         * setIfAbsent 该方法如果键值对不存在则设置缓存，若存在则设置失败，返回设置结果
         *
         * 该操作属于原子性操作，利用键值对在缓存中的唯一性，实现分布式锁
         *
         * 设置过期时间，能避免死锁
         *
         * 但也要合理设置过期时间，由于业务执行时长可鞥超过有效期
         *
         * 该操作涉及外部服务，会产生IO操作，可能会因为网络问题等造成异常，故需要容错处理
         */
        try {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, Thread.currentThread().getName(), lockExpireTime, TimeUnit.SECONDS);
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

        log.info("[账户消费者][消息幂等性检查]开始，消息ID：: {}", messageId);

        // 第1级：Redis精确匹配（快速返回）
        IdempotentCheckResult idempotentCheckResult = checkRedis(messageId);
        if (idempotentCheckResult != null) {
            return idempotentCheckResult;
        }

        log.info("[账户消费者]Redis消息幂等性检查结果为空，降级到数据库检查: {}", messageId);

        // 第3级. 检查数据库唯一性幂等性
        idempotentCheckResult = checkDatabase(messageId);

        log.info("[账户消费者][消息幂等性检查]结束，消息ID：{}", messageId);
        return idempotentCheckResult;
    }

    /**
     * 第1级：Redis精确匹配（快速返回）
     * @param messageId
     * @return
     */
    private IdempotentCheckResult checkRedis(String messageId) {

        log.info("[账户消费者][消息幂等性检查]Redis检查开始，消息ID：{}，检查来源：{}", messageId, IdempotentCheckResult.SourceType.REDIS);
        try {
            String redisKey = buildRedisCheckKey(messageId);
            log.info("[账户消费者][消息幂等性检查]Redis检查缓存消息KEY：{}", redisKey);

            Map idempotentValue = redisTemplate.opsForHash().entries(redisKey);

            if (idempotentValue.isEmpty()) {
                return null;
            }

            //状态
            String status = (String) idempotentValue.get(STATUS);
            //状态描述
            String processResult = (String) idempotentValue.get(PROCESS_RESULT);
            //重试次数
            Integer retryCount = (Integer) idempotentValue.get(RETRY_COUNT);


            log.info("[账户消费者][消息幂等性检查]Redis检查消息ID：{}，检查来源：{}，状态：{}，状态描述：{}，重试次数：{}", messageId, IdempotentCheckResult.SourceType.REDIS, status, processResult, retryCount);

            IdempotentCheckResult.CheckStatus checkStatus = IdempotentCheckResult.CheckStatus.valueOf(status);

            return IdempotentCheckResult.builder().messageId(messageId)
                    .status(checkStatus)
                    .processResult(processResult)
                    .retryCount(retryCount)
                    .source(IdempotentCheckResult.SourceType.REDIS)
                    .build();

        } catch (Exception e) {
            log.error("[账户消费者][消息幂等性检查]Redis检查异常，消息ID：{}，检查来源：{}", messageId, IdempotentCheckResult.SourceType.REDIS);
            return null;
        } finally {
            log.info("[账户消费者][消息幂等性检查]Redis检查结束，消息ID：{}，检查来源：{}", messageId, IdempotentCheckResult.SourceType.REDIS);
        }
    }

    /**
     * 第2级：数据库检查
     * @param messageId
     * @return
     */
    private IdempotentCheckResult checkDatabase(String messageId) {

        log.info("[账户消费者][消息幂等性检查]数据库检查开始，消息ID：{}，检查来源：{}", messageId, IdempotentCheckResult.SourceType.DATABASE);

        try {
            MessageIdempotent messageIdempotent = messageIdempotentService.getByMessageId(messageId);

            if (messageIdempotent == null) {
                return IdempotentCheckResult.builder()
                        .messageId(messageId)
                        .status(IdempotentCheckResult.CheckStatus.NOT_PROCESSED)
                        .processResult("消息未处理")
                        .retryCount(0)
                        .source(IdempotentCheckResult.SourceType.DATABASE)
                        .build();
            }

            //状态
            String status = messageIdempotent.getStatus();
            //状态描述
            String processResult = messageIdempotent.getProcessResult();
            //重试次数
            Integer retryCount = messageIdempotent.getRetryCount();

            log.info("[账户消费者][消息幂等性检查]数据库检查，消息ID：{}，检查来源：{}，状态：{}，状态描述：{}，重试次数：{}", messageId, IdempotentCheckResult.SourceType.DATABASE, status, processResult, retryCount);

            IdempotentCheckResult.CheckStatus checkStatus = IdempotentCheckResult.CheckStatus.valueOf(messageIdempotent.getStatus());

            return IdempotentCheckResult.builder()
                    .messageId(messageId)
                    .status(checkStatus)
                    .processResult(processResult)
                    .retryCount(retryCount)
                    .source(IdempotentCheckResult.SourceType.DATABASE)
                    .build();

        } catch (Exception e) {
            log.error("[账户消费者][消息幂等性检查]数据库检查异常，消息ID：{}，检查来源：{}", messageId, IdempotentCheckResult.SourceType.DATABASE);
            throw e;
        } finally {
            log.info("[账户消费者][消息幂等性检查]数据库检查结束，消息ID：{}，检查来源：{}", messageId, IdempotentCheckResult.SourceType.DATABASE);
        }

    }



    /**
     * 构建缓存键值对key
     * @param messageId
     * @return
     */
    private String buildRedisLockKey(String messageId) {

        return LOCK_KEY_PREFIX + messageId;
    }

    private String buildRedisCheckKey(String messageId) {

        return CACHE_KEY_PREFIX + messageId;
    }

}
