package com.gushu.gushumingmu.User.Config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JwtConfig {
    @Value("${jwt.secret:default-secret-key}") // 默认值避免配置缺失报错
    private String secret;

    @Value("${jwt.expiration:86400000}") // 默认24小时
    private long expiration;
}