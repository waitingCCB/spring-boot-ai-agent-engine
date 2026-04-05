package com.flow.agent.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

// 从json文件中加载配置，要求状态机只根据这个类就能完整运行
@Data
public class AgentMachineConfig {

    private String agentId;
    private String agentName;
    private String account;
    private String question;
    private Map<String, List<AgentStep>> agentStepListMap;


}
