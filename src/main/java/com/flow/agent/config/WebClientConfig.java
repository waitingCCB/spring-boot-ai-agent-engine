package com.flow.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient 配置类：用于连接 localhost:8848（如 Nacos、自定义服务等）
 * 解决 Spring Boot 3.x 下的 HttpClient 依赖问题，并做基础优化
 */
@Configuration
public class WebClientConfig {

    private static final String BASE_URL = "http://localhost:8848";

    /**
     * 配置 WebClient 使用 Netty 底层（无 Apache HttpClient 依赖）
     */
    @Bean
    public WebClient webClient() {
        // 可选：自定义 Netty 的 HttpClient（比如设置超时）

        return WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}