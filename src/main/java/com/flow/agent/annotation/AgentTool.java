package com.flow.agent.annotation;

import java.lang.annotation.*;

/**
 * 标记一个类是 AI 可调用的工具服务
 * 对应配置：needService
 */
@Target(ElementType.TYPE) // 只能放在类上
@Retention(RetentionPolicy.RUNTIME) // 运行时可读取
@Documented
public @interface AgentTool {

    /**
     * 服务名称（给AI看）
     * 对应配置里的 needService 值
     * 例如：aiService、testService
     */
    String serviceName();

    /**
     * 服务描述（AI理解用途）
     */
    String desc() default "";
}
