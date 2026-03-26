package com.app.mysecureapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 工具類別 (支援不透明令牌 Opaque Token 架構)
 * 負責 JWT 的生成、解析與驗證。
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 🚀 雙 ID 架構核心：
     * 從 Token 的 Subject 中取出對外識別碼 (External ID / UUID)。
     * 重要：雖然配合 Spring Security 介面名稱保留為 extractUsername，但實質內容已替換為 UUID。
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 產生 Token (發放護照)
     * @param userDetails 經過 CustomUserDetailsService 處理過的使用者資訊，此時的 Username 已被替換為 UUID
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 🚀 這裡的 userDetails.getUsername() 實際上會提取出 UUID 字串
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject) // 這裡放入的將是 UUID
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 驗證 Token 是否有效
     * 比對 Token 中的 UUID 與資料庫查詢到的 UUID 是否一致
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        // 從 token 取出 UUID 字串
        final String tokenUuid = extractUsername(token);
        // 比對兩者的 UUID 是否相同，並確認是否過期
        return (tokenUuid.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}