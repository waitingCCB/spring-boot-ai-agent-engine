package com.flow.agent.config;

import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AiConfig {
    @Value("${doubao.ark.api-key}")
    private String apiKey;

    @Value("${doubao.ark.base-url}")
    private String baseUrl;

    @Value("${doubao.pool.max-idle-connections:5}")
    private int maxIdleConnections;

    @Value("${doubao.pool.keep-alive-duration:1}")
    private long keepAliveDuration;

    // 定义连接池Bean
    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
    }

    // 定义分发器Bean
    @Bean
    public Dispatcher dispatcher() {
        return new Dispatcher();
    }

    // 定义核心的ArkService Bean，并注入上面定义的连接池和分发器
    @Bean
    public ArkService arkService(ConnectionPool connectionPool, Dispatcher dispatcher) {
        return ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }
}
