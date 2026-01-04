package com.wwd.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Copyright: Copyright (c) 2025 Asiainfo
 *
 * @ClassName: com.wwd.common.utils.JwtUtil
 * @Description:
 * @version: v1.0.0
 * @author: wangwd7
 * @date: 2025-12-29
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2025-12-29     wangwd7          v1.0.0               创建
 */
@Slf4j
public class JwtUtil {

    private static final String SECRET_KEY = "finance-system-secret-key-2024-spring-boot-vue-project";
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7天

    // 生成密钥
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public static String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
}
