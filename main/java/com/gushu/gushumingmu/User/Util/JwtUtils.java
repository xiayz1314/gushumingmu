package com.gushu.gushumingmu.User.Util;

import com.gushu.gushumingmu.User.Config.JwtConfig;
import com.gushu.gushumingmu.User.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.*;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtConfig jwtConfig;

    // 生成JWT令牌（核心方法）
    // JwtUtils.java
    public String generateToken(String username) {

        Key signingKey =
                Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + jwtConfig.getExpiration())
                )
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }



    // 从令牌中获取用户名
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // 从令牌中获取角色集合
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        //先获取为List,在转为set
        List<String> roleList = (List<String>) parseClaims(token).get("roles");
        return roleList != null ? new HashSet<>(roleList) : new HashSet<>();
    }

    // 从令牌中获取权限集合
    @SuppressWarnings("unchecked")
    public Set<String> getPermissionsFromToken(String token) {
        // 先获取为List，再转为Set
        List<String> permissionList = (List<String>) parseClaims(token).get("permissions");
        return permissionList != null ? new HashSet<>(permissionList) : new HashSet<>();
    }

    // 验证令牌是否有效（包含签名、过期时间等校验）
    public boolean validateToken(String token) {
        try {
            // 解析令牌（内部已包含签名和过期时间校验）
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌格式: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT令牌格式错误（可能被篡改）: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT签名验证失败（密钥不匹配或令牌被篡改）: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT令牌为空或参数错误: {}", e.getMessage());
        }
        return false;
    }

    // 解析令牌获取claims（私有工具方法）
    private Claims parseClaims(String token) {
        // 生成验证密钥（与签名密钥一致）
        Key signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());

        // 解析并验证令牌
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey) // 验证签名（新版本API）
                .build()
                .parseSignedClaims(token) // 解析签名后的令牌
                .getPayload(); // 获取claims（载荷）
    }
}