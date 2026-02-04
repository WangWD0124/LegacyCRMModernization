package com.wwd.common.dto;

import com.wwd.common.enums.BusinessResultEnun;
import lombok.Data;

import java.io.Serializable;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.common.dto.Result
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-12-13
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-12-13     wangwd7          v1.0.0               创建
 */

@Data
public class BusinessResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private BusinessResultEnun result;
    private T data;
    private Long timestamp;

    public BusinessResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> BusinessResult<T> success(BusinessResultEnun result, T data) {
        BusinessResult<T> businessResult = new BusinessResult<>();
        businessResult.setResult(result);
        businessResult.setData(data);
        return businessResult;
    }

    public static <T> BusinessResult<T> success(BusinessResultEnun result) {
        return error(result, null);
    }

    public static <T> BusinessResult<T> success() {
        return error(BusinessResultEnun.SUCCESS);
    }

    public static <T> BusinessResult<T> error(BusinessResultEnun result, T data) {
        BusinessResult<T> businessResult = new BusinessResult<>();
        businessResult.setResult(result);
        businessResult.setData(data);
        return businessResult;
    }

    public static <T> BusinessResult<T> error(BusinessResultEnun result) {
        return error(result, null);
    }

    public static <T> BusinessResult<T> error() {
        return error(BusinessResultEnun.FAILED);
    }
}
