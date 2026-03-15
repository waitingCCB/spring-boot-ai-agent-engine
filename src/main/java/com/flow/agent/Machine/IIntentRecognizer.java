package com.flow.agent.Machine;

import com.flow.agent.entity.AgentContext;

import java.util.List;

public interface IIntentRecognizer {
    String RecognizedIntent(AgentContext agentContext, List<String> intentList);
}
