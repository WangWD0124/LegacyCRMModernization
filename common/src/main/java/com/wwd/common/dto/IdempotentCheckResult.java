package com.wwd.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.dto.IdempotentCheckResult
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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotentCheckResult {

    /**
     * 消息ID
      */
    private String messageId;

    /**
     * 检查结果
      */
    private CheckStatus status;

    private String processResult;

    private Integer retryCount;

    private SourceType source;

    /**
     * 枚举定义
      */
    public enum CheckStatus {
        // 未处理，可以继续处理
        NOT_PROCESSED,
        // 已处理且成功
        SUCCESS,
        // 已处理但失败可重试，可以继续处理
        FAILED_CAN_RETRY,
        // 最终失败
        FAILED,
        // 正在处理中（防止并发）
        PROCESSING,
    }

    public enum SourceType {
        // 来自Redis
        REDIS,
        // 来自数据库
        DATABASE,
        // 来自内存
        MEMORY,
        // 未知来源
        UNKNOWN
    }

    /**
     * 判断是否可以消费消息的方法
     */
    public boolean canProcess() {
        return status == CheckStatus.NOT_PROCESSED || status == CheckStatus.FAILED_CAN_RETRY;
    }

    public boolean isNotProcessed() {
        return status == CheckStatus.NOT_PROCESSED;
    }

    public boolean isFailedCanRetry() {
        return status == CheckStatus.FAILED_CAN_RETRY;
    }

//    /**
//     * 未处理，可以继续处理
//     */
//    public static IdempotentCheckResult notProcessed(String messageId) {
//        return IdempotentCheckResult.builder()
//                .messageId(messageId)
//                .status(CheckStatus.NOT_PROCESSED)
//                .statusDesc("消息未处理，可以继续处理")
//                .source(SourceType.UNKNOWN)
//                .build();
//    }
//
//    /**
//     * 已处理且成功
//     */
//    public static IdempotentCheckResult success(
//            String messageId, String result) {
//        return IdempotentCheckResult.builder()
//                .messageId(messageId)
//                .status(CheckStatus.PROCESSED_SUCCESS)
//                .statusDesc("消息已处理成功")
//                .processResult(result)
//                .processTime(new Date())
//                .source(SourceType.DATABASE)
//                .responseTime(System.currentTimeMillis())
//                .build();
//    }
//
//    /**
//     * 正在处理中（用于分布式锁场景）
//     */
//    public static IdempotentCheckResult processing(String messageId, String lockOwner) {
//        return IdempotentCheckResult.builder()
//                .messageId(messageId)
//                .status(CheckStatus.PROCESSING)
//                .statusDesc("消息正在被处理，处理者: " + lockOwner)
//                .processTime(new Date())
//                .source(SourceType.REDIS)
//                .responseTime(System.currentTimeMillis())
//                .build();
//    }
//
//    /**
//     * 处理失败
//     */
//    public static IdempotentCheckResult processedFailed(String messageId, String errorMsg) {
//        return IdempotentCheckResult.builder()
//                .messageId(messageId)
//                .status(CheckStatus.PROCESSED_FAILED)
//                .statusDesc("消息处理失败")
//                .processResult(errorMsg)
//                .processTime(new Date())
//                .source(SourceType.DATABASE)
//                .responseTime(System.currentTimeMillis())
//                .build();
//    }
//
//    /**
//     * 处理失败_可重试
//     */
//    public static IdempotentCheckResult processedFailed(String messageId, String errorMsg, SourceType sourceType) {
//        return IdempotentCheckResult.builder()
//                .messageId(messageId)
//                .status(CheckStatus.PROCESSED_FAILED)
//                .statusDesc("消息处理失败可重试")
//                .processResult(errorMsg)
//                .processTime(new Date())
//                .source(sourceType)
//                .responseTime(System.currentTimeMillis())
//                .build();
//    }

//    /**
//     * 基于Redis记录构建
//     */
//    public static IdempotentCheckResult fromString(String messageId, String status) {
//
//        CheckStatus checkStatus;
//        switch (status) {
//            case "SUCCESS": checkStatus = CheckStatus.PROCESSED_SUCCESS; break;
//            case "FAILED": checkStatus = CheckStatus.PROCESSED_FAILED; break;
//            case "PROCESSING": checkStatus = CheckStatus.PROCESSING; break;
//            default: checkStatus = CheckStatus.NOT_PROCESSED;
//        }
//
//        return IdempotentCheckResult.builder()
//                .messageId(messageId)
//                .businessType(entity.getBusinessType())
//                .status(status)
//                .statusDesc("来自数据库记录")
//                .processResult(entity.getProcessResult())
//                .processTime(entity.getProcessTime())
//                .serviceName(entity.getServiceName())
//                .source(SourceType.DATABASE)
//                .responseTime(System.currentTimeMillis())
//                .build();
//    }
//
//
//    /**
//     * 基于数据库记录构建
//     */
//    public static IdempotentCheckResult fromEntity(MessageIdempotentEntity entity) {
//        CheckStatus status;
//        switch (entity.getStatus()) {
//            case "SUCCESS": status = CheckStatus.PROCESSED_SUCCESS; break;
//            case "FAILED": status = CheckStatus.PROCESSED_FAILED; break;
//            case "PROCESSING": status = CheckStatus.PROCESSING; break;
//            default: status = CheckStatus.NOT_PROCESSED;
//        }
//
//        return IdempotentCheckResult.builder()
//                .messageId(entity.getMessageId())
//                .businessId(entity.getBusinessId())
//                .businessType(entity.getBusinessType())
//                .status(status)
//                .statusDesc("来自数据库记录")
//                .processResult(entity.getProcessResult())
//                .processTime(entity.getProcessTime())
//                .serviceName(entity.getServiceName())
//                .source(SourceType.DATABASE)
//                .responseTime(System.currentTimeMillis())
//                .build();
//    }



//    public boolean isProcessed() {
//        return status == CheckStatus.PROCESSED_SUCCESS ||
//                status == CheckStatus.PROCESSED_FAILED_EXCEED_RETRY;
//    }
//
//    public boolean isProcessing() {
//        return status == CheckStatus.PROCESSING;
//    }

}
