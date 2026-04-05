package com.flow.agent.annotation;

import com.flow.agent.autoconfigure.AgentAutoConfiguration;
import org.springframework.context.annotation.Import;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AgentAutoConfiguration.class)
public @interface EnableAiAgentEngine {

}
