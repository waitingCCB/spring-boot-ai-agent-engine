package com.flow.agent.Machine;

import com.flow.agent.entity.AgentContext;

import java.util.List;

public interface IIntentRecognizer {
    /**
     * @param agentContext 状态机上下文
     * @param intentList    可选的意图列表
     * @return 应该执行哪个意图
     */
    String RecognizedIntent(AgentContext agentContext, List<String> intentList);
}
