package com.flow.agent.enmu;

public enum AgentState {
    LOAD_CONFIG,  // 加载配置
    JUDGE_INTENT, // 判断意图
    DO_CHAIN,   //  执行工作链条
    FINISH,    // 完成工作
    ERROR
}
