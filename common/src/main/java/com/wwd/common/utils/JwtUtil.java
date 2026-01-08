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
@Component
public class JwtUtil {

    @Value("${jwt.secret:your-256-bit-secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration; // 默认24小时

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDTO user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("roles", String.join(",", user.getRoles()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    // 刷新Token（返回新Token，旧Token加入黑名单）
    public String refreshToken(String oldToken) {
        Claims claims = validateToken(oldToken);

        // 将旧Token加入黑名单（剩余有效期）
        blacklistToken(oldToken, claims.getExpiration());

        // 生成新Token
        UserDTO user = new UserDTO();
        user.setId(Long.parseLong(claims.getSubject()));
        user.setUsername(claims.get("username", String.class));

        return generateToken(user);
    }
}
