package com.wwd.common.dto;

import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.common.dto.PageResult
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
public class PageResult<T> {
    private List<T> records;
    private Long total;
    private Long current;
    private Long size;
    private Long pages;

    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }
}
