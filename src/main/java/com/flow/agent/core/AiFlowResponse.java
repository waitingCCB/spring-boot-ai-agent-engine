package com.flow.agent.core;

import lombok.Data;

import java.util.List;

@Data
public class AiFlowResponse {
    private String flowName;
    private List<AgentStep> steps; // 直接用现有的 AgentStep
}
