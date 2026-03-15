package com.flow.agent.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许携带认证信息（如cookies、Authorization头）
        config.setAllowCredentials(true);
        // 使用允许模式匹配，替代已过时的allowedOrigins("*")
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 允许的请求头
        config.addAllowedHeader("*");
        // 允许的请求方法
        config.addAllowedMethod("*");
        config.addAllowedMethod("OPTIONS");
        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        // 对所有接口路径应用此配置
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        // 设置过滤器优先级为最高
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}