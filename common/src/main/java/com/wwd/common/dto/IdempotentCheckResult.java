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

    // 消息信息
    private String messageId;
    private String businessId;
    private String businessType;

    // 检查结果
    private CheckStatus status;
    private String statusDesc;

    // 处理信息（如果已处理）
    private String processResult;
    private Date processTime;
    private String serviceName;

    // 来源信息（用于调试和监控）
    private SourceType source;
    private Long responseTime; // 毫秒

    // 枚举定义
    public enum CheckStatus {
        NOT_PROCESSED,      // 未处理，可以继续处理
        PROCESSED_SUCCESS,  // 已处理且成功
        PROCESSED_FAILED,   // 已处理但失败
        PROCESSING,         // 正在处理中（防止并发）
        EXPIRED            // 已过期（可重新处理）
    }

    public enum SourceType {
        REDIS,      // 来自Redis
        DATABASE,   // 来自数据库
        MEMORY,     // 来自内存
        UNKNOWN     // 未知来源
    }

    /**
     * 未处理，可以继续处理
     */
    public static IdempotentCheckResult notProcessed(String messageId) {
        return IdempotentCheckResult.builder()
                .messageId(messageId)
                .status(CheckStatus.NOT_PROCESSED)
                .statusDesc("消息未处理，可以继续处理")
                .source(SourceType.UNKNOWN)
                .responseTime(System.currentTimeMillis())
                .build();
    }

    /**
     * 已处理且成功
     */
    public static IdempotentCheckResult alreadyProcessed(
            String messageId, String businessId, String result) {
        return IdempotentCheckResult.builder()
                .messageId(messageId)
                .businessId(businessId)
                .status(CheckStatus.PROCESSED_SUCCESS)
                .statusDesc("消息已处理成功")
                .processResult(result)
                .processTime(new Date())
                .source(SourceType.DATABASE)
                .responseTime(System.currentTimeMillis())
                .build();
    }

    /**
     * 正在处理中（用于分布式锁场景）
     */
    public static IdempotentCheckResult processing(String messageId, String lockOwner) {
        return IdempotentCheckResult.builder()
                .messageId(messageId)
                .status(CheckStatus.PROCESSING)
                .statusDesc("消息正在被处理，处理者: " + lockOwner)
                .processTime(new Date())
                .source(SourceType.REDIS)
                .responseTime(System.currentTimeMillis())
                .build();
    }

    /**
     * 处理失败
     */
    public static IdempotentCheckResult failed(String messageId, String errorMsg) {
        return IdempotentCheckResult.builder()
                .messageId(messageId)
                .status(CheckStatus.PROCESSED_FAILED)
                .statusDesc("消息处理失败")
                .processResult(errorMsg)
                .processTime(new Date())
                .source(SourceType.DATABASE)
                .responseTime(System.currentTimeMillis())
                .build();
    }

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

    // 便捷判断方法
    public boolean shouldProcess() {
        return status == CheckStatus.NOT_PROCESSED || status == CheckStatus.EXPIRED;
    }

    public boolean isSuccessProcessed() {
        return status == CheckStatus.PROCESSED_SUCCESS;
    }

    public boolean isFailedProcessed() {
        return status == CheckStatus.PROCESSED_FAILED;
    }

    public boolean isProcessing() {
        return status == CheckStatus.PROCESSING;
    }
}
