package com.flow.agent.annotation;

import java.lang.annotation.*;

/**
 * 标记方法是 AI 可调用的业务功能
 * 对应你的配置：needMethod
 */
@Target(ElementType.METHOD) // 只能放在方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgentMethod {

    /**
     * 方法名（给AI看，对应 needMethod）
     * 例如：test、generateData、dealData
     */
    String methodName();

    /**
     * 方法功能描述（非常重要！AI靠这个编排流程）
     */
    String desc();

}
