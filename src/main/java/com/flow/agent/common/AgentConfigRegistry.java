package com.flow.agent.common;

import com.flow.agent.core.AgentMachineConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// spring容器注解
@Component
public class AgentConfigRegistry {

    // 账号 - > 配置文件，一个配置文件一个状态机
    private final Map<String, AgentMachineConfig> configMap = new HashMap<String, AgentMachineConfig>();

    // 保存配置
    public void saveConfig(String account, AgentMachineConfig config) {
        configMap.put(account, config);
    }

    // 获取配置
    public AgentMachineConfig getConfig(String account) {
        return configMap.get(account);
    }

    // 删除配置
    public void removeConfig(String account) {
        configMap.remove(account);
    }


}
