package com.flow.agent.entity;

import com.flow.agent.enmu.AgentState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// doChain，执行工作链条时的上下文，状态机执行完后需要返回这个类的最终结果
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentContext {
    private String account;
    private String agentId;
    private String aiFinalAnswer;
    private String intent;
    private String userQuestion;

    private Object nowServiceResult;
    private AgentState agentState;
}
