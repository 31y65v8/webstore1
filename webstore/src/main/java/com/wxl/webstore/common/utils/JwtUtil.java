package com.wxl.webstore.common.utils;
/*
基于JJWT库生成、解析和获取用户角色信息，主要用于身份验证。
 */
import com.wxl.webstore.common.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * 生成 JWT 令牌
     * @param account 账号（手机号/邮箱）
     * @param role 用户角色
     * @return JWT Token
     */
    public String generateToken(String account, UserRole role) {
        return Jwts.builder()
                .subject(account)
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从token中获取Claims
     * @param token JWT Token
     * @return Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期", e);
        } catch (JwtException e) {
            throw new RuntimeException("Token无效", e);
        }
    }

    /**
     * 获取用户角色
     * @param token JWT Token
     * @return UserRole
     */
    public UserRole getUserRoleFromToken(String token) {
        return UserRole.valueOf(getClaimsFromToken(token).get("role", String.class));
    }

    /**
     * 获取账号（手机号/邮箱）
     * @param token JWT Token
     * @return String
     */
    public String getAccountFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 验证token是否有效
     * @param token JWT Token
     * @return boolean
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}