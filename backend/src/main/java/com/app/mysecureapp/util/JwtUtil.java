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

@Component
public class JwtUtil {

    // 從 application.properties 中讀取密鑰
    @Value("${jwt.secret}")
    private String secret;

    // 從 application.properties 中讀取過期時間
    @Value("${jwt.expiration}")
    private long expiration;

    // 產生 SecretKey 物件 (jjwt 0.12.x 需要)
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 從 Token 中取出使用者名稱
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 從 Token 中取出過期時間
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 泛型方法：用來取出 Token 中的任何資訊 (Claim)
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 解析 Token (jjwt 0.12.x 写法)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // 設定驗證簽名的 Key
                .build()
                .parseSignedClaims(token) // 解析 Token
                .getPayload(); // 取得 Payload (Claims)
    }

    // 檢查 Token 是否過期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 產生 Token (給 UserDetails 用)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // 產生 Token (jjwt 0.12.x 写法)
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims) // 設定自定義聲明
                .subject(subject) // 設定使用者名稱
                .issuedAt(new Date(System.currentTimeMillis())) // 發行時間
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 過期時間
                .signWith(getSigningKey()) // 簽名
                .compact();
    }

    // 驗證 Token 是否有效
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
