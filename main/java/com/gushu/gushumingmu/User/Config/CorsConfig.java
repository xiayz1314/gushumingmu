package com.gushu.gushumingmu.User.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1. 配置跨域信息
        CorsConfiguration config = new CorsConfiguration();
        // 允许前端域名（替换为你的前端实际地址，如localhost:5173，不要用*，否则无法携带Authorization）
        config.addAllowedOrigin("http://localhost:3000");
        // 允许的请求方法（包括预检请求OPTIONS）
        config.addAllowedMethod("*");
        // 允许的请求头（包括Authorization、Content-Type等）
        config.addAllowedHeader("*");
        // 允许携带Cookie/Token（前后端分离场景通常需要开启）
        config.setAllowCredentials(true);
        // 预检请求有效期（单位：秒，避免频繁发送预检请求）
        config.setMaxAge(3600L);

        // 2. 配置跨域路径（所有路径都生效）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // 3. 返回跨域过滤器
        return new CorsFilter(source);
    }
}