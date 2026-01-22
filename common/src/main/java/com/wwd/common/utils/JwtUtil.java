package com.wwd.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

/**
 * Copyright: Copyright (c) 2026 Asiainfo
 *
 * @ClassName: com.wwd.common.utils.JwtUtil
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2026-01-10
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2026-01-10     wangwd7          v1.0.0               创建
*/
@Component
@Slf4j
public class JwtUtil {


    // 从配置文件中读取密钥（推荐）
    @Value("${jwt.secret:your-256-bit-secret-change-this-in-production}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private Key key;

    @PostConstruct
    public void init() {
        try {
            // 确保密钥长度至少32字节（256位）
            if (secret == null || secret.length() < 32) {
                log.warn("JWT secret is too short, using default");
                secret = "your-256-bit-secret-change-this-in-production-please";
            }

            // 使用 Keys.hmacShaKeyFor 需要至少256位（32字节）的密钥
            byte[] keyBytes = secret.getBytes();
            if (keyBytes.length < 32) {
                // 如果不够长，进行填充
                byte[] padded = new byte[32];
                System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
                key = Keys.hmacShaKeyFor(padded);
            } else {
                key = Keys.hmacShaKeyFor(keyBytes);
            }

            log.info("JWT key initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize JWT key", e);
            throw new RuntimeException("JWT initialization failed", e);
        }
    }

    /**
     * 生成Token（只放用户ID）
     */
    public String generateToken(Long userId) {
        if (key == null) {
            throw new IllegalStateException("JWT key not initialized");
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Token中解析用户ID
     */
    public Long getUserIdFromToken(String token) {
        if (key == null) {
            throw new IllegalStateException("JWT key not initialized");
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        if (key == null) {
            throw new IllegalStateException("JWT key not initialized");
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
