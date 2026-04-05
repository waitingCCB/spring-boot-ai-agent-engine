package com.flow.agent.scanner;


import com.flow.agent.annotation.AgentMethod;
import com.flow.agent.annotation.AgentParam;
import com.flow.agent.annotation.AgentTool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AgentFunctionScanner implements ApplicationListener<ContextRefreshedEvent> {

    // 全局能力池：key=serviceName, value=方法列表
    public static final Map<String, List<FunctionInfo>> FUNCTION_POOL = new HashMap<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext ctx = event.getApplicationContext();
        // 找出所有带 @AgentTool 注解的 Bean
        Map<String, Object> beans = ctx.getBeansWithAnnotation(AgentTool.class);

        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();
            AgentTool tool = clazz.getAnnotation(AgentTool.class);
            String serviceName = tool.serviceName();

            List<FunctionInfo> functions = new ArrayList<>();

            for (Method method : clazz.getDeclaredMethods()) {
                AgentMethod agentMethod = method.getAnnotation(AgentMethod.class);
                if (agentMethod == null) continue;

                FunctionInfo info = new FunctionInfo();
                info.setServiceName(serviceName);
                info.setMethodName(agentMethod.methodName());
                info.setDesc(agentMethod.desc());

                // 解析参数
                List<ParamInfo> params = new ArrayList<>();
                for (Parameter p : method.getParameters()) {
                    AgentParam ap = p.getAnnotation(AgentParam.class);
                    if (ap == null) continue;
                    ParamInfo pi = new ParamInfo();
                    pi.setName(ap.name());
                    pi.setDesc(ap.desc());
                    pi.setType(ap.type());
                    params.add(pi);
                }
                info.setParams(params);
                functions.add(info);
            }
            FUNCTION_POOL.put(serviceName, functions);
        }
        System.out.println("✅ AI能力扫描完成：" + FUNCTION_POOL);
    }

    // 方法信息
    public static class FunctionInfo {
        public String serviceName;
        public String methodName;
        public String desc;
        public List<ParamInfo> params;
        // getter/setter

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public List<ParamInfo> getParams() {
            return params;
        }

        public void setParams(List<ParamInfo> params) {
            this.params = params;
        }
    }

    // 参数信息
    public static class ParamInfo {
        public String name;
        public String desc;
        public String type;
        // getter/setter

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
