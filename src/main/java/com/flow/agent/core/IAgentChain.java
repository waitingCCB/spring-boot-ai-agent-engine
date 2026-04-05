package com.flow.agent.core;

import java.util.List;
import java.util.Map;


public interface IAgentChain {


    /**
     * @param agentContext 状态机上下文
     * @param stepList  工作步骤列表
     * @param serviceMap   这个工作链条需要调用的业务提供类（自定义，无缝适合所有业务类），map运行在单一工作流混合不同业务类
     */
    void doChain(AgentContext agentContext, List<AgentStep> stepList, Map<String, Object> serviceMap);

}
