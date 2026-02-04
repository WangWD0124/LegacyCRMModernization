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
public enum BusinessResultEnun {

    SUCCESS(1, "处理成功"),
    FAILED(-1, "处理失败");

    private final Integer code;
    private final String desc;

    BusinessResultEnun(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BusinessResultEnun getByCode(Integer code) {
        for (BusinessResultEnun status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}

