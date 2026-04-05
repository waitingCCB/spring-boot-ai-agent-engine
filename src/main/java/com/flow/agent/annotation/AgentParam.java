package com.flow.agent.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) // 加在参数上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgentParam {
    String desc(); // 参数描述：例如 "用户ID" "订单编号" "处理后的数据"
    String type();  // 数据类型
    String name();
    boolean required() default true;
}
