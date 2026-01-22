package com.wwd.common.utils;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.utils.FlexibleLocalDateTimeDeserializer
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-09
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-09     wangwd7          v1.0.0               创建
 */



import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 灵活的LocalDateTime反序列化器
 * 支持多种日期格式：
 * 1. yyyy-MM-dd          -> 转为当天的 00:00:00
 * 2. yyyy-MM-dd HH:mm:ss -> 直接转换
 * 3. yyyy-MM-dd'T'HH:mm:ss -> ISO格式
 */
public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateStr = p.getText().trim();
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            // 尝试解析为日期时间（带有时分秒）
            if (dateStr.contains("T")) {
                return LocalDateTime.parse(dateStr);
            } else if (dateStr.contains(":")) {
                // 格式：yyyy-MM-dd HH:mm:ss
                return LocalDateTime.parse(dateStr, DATE_TIME_FORMATTER);
            } else {
                // 格式：yyyy-MM-dd，转换为当天的开始时间
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                return date.atStartOfDay();
            }
        } catch (Exception e) {
            throw new IOException("日期格式错误: " + dateStr + "，支持格式: yyyy-MM-dd, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss", e);
        }
    }
}
