package com.wwd.common.enums;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.enums.MessageStatus
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

import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
public enum MessageStatus {

    PROCESSING("PROCESSING", "处理中"),
    SUCCESS("SUCCESS", "处理成功"),
    FAILED("FAILED", "处理失败"),
    RETRYING("RETRYING", "重试中");

    private final String code;
    private final String desc;

    MessageStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MessageStatus getByCode(String code) {
        for (MessageStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public boolean isFinalStatus() {
        return this == SUCCESS || this == FAILED;
    }

    public boolean canRetry() {
        return this == FAILED || this == RETRYING;
    }
}

