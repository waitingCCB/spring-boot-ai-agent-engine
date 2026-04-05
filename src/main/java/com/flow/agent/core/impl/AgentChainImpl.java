package com.flow.agent.core.impl;

import com.flow.agent.core.IAgentChain;
import com.flow.agent.core.AgentContext;
import com.flow.agent.core.AgentStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AgentChainImpl implements IAgentChain {


    @Override
    public void doChain(AgentContext agentContext, List<AgentStep> stepList, Map<String, Object> serviceMap) {
        // 遍历步骤列表，逐个执行
        for (AgentStep step : stepList) {
            // 传入：上下文、步骤、serviceMap
            executeSingleStep(agentContext, step, serviceMap);
        }
    }

    /**
     * 自动根据步骤需要的服务名，反射调用任意业务类方法
     */
    private void executeSingleStep(AgentContext context, AgentStep step, Map<String, Object> serviceMap) {
        // 1. 获取步骤需要调用的服务名（如：intent_recognize）
        String serviceName = step.getNeedService();
        String needMethod = step.getNeedMethod();

        log.info("→ 执行步骤：" + step.getId());
        log.info("→ 调用服务：" + serviceName);

        try {
            // 2. 从 serviceMap 获取当前步骤要使用的服务实例
            Object serviceInstance = serviceMap.get(serviceName);
            if (serviceInstance == null) {
                throw new RuntimeException("未找到服务实例：" + serviceName);
            }

            // 3. 下划线转驼峰，获得真实方法名
            String methodName = camelCase(needMethod);

            // 4. 找到目标方法
            Method targetMethod = null;
            for (Method method : serviceInstance.getClass().getMethods()) {
                if (method.getName().equals(methodName)) {
                    targetMethod = method;
                    break;
                }
            }
            if (targetMethod == null) {
                throw new NoSuchMethodException("服务中未找到方法：" + methodName);
            }


            // 构建方法参数
            Parameter[] parameters = targetMethod.getParameters();
            Object[] args = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter param = parameters[i];
                // 规则：如果开启了 useLastResult，并且是第一个参数，传上一步结果
                if (i == 0 && step.isUseLastResult()) {
                    args[i] = context.getNowServiceResult(); // 直接传 Object，不做任何转换
                    log.info("第一个参数使用上一步的结果，赋值为{}", args[i]);
                } else {
                    // 其他参数正常读取
                    args[i] = getValueFromContextOrStep(context, param.getName(), param.getType(), step);
                    log.info("第{}个参数赋值为{}",i + 1, args[i]);
                }
            }

            // 6. 执行方法
            Object result = targetMethod.invoke(serviceInstance, args);

            // 7. 结果写入上下文
            context.setNowServiceResult(result == null ? "执行成功无返回值" : result);

        } catch (Exception e) {
            log.info("→ 服务调用失败：" + e.getMessage());
            context.setNowServiceResult("服务调用失败：" + e.getMessage());
        }

        log.info("→ 执行结果：" + context.getNowServiceResult());
    }


    /**
     * 从上下文或步骤参数自动取值
     */
    private Object getValueFromContextOrStep(AgentContext context, String paramName, Class<?> paramType, AgentStep step) {

        try {
            java.lang.reflect.Field field = AgentContext.class.getDeclaredField(paramName);
            field.setAccessible(true);
            return field.get(context);
        } catch (NoSuchFieldException ignored) {
            return step.getParams() != null ? step.getParams().get(paramName) : null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 下划线转驼峰
     */
    private String camelCase(String underscore) {
        if (underscore == null || !underscore.contains("_")) {
            return underscore;
        }

        String[] parts = underscore.split("_");
        StringBuilder sb = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            if (parts[i].isEmpty()) continue;
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
            sb.append(parts[i].substring(1));
        }
        return sb.toString();
    }
}
