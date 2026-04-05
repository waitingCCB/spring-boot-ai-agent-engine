package com.flow.agent.autoconfigure;


import com.flow.agent.scanner.AiConfig;
import com.flow.agent.service.impl.AgentUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.flow.agent")
public class AgentAutoConfiguration {



}
