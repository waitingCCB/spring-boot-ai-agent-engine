package com.flow.agent.core;

import java.util.Map;

public interface IAgentMachine {


    /**
     * 初始化状态表
     */
    void initTable();


    /**
     * 判断用户的意图
     * @return 意图
     */
    String judgeIntent();


    /** 加载智能体工作流配置文件
     *
     * @return 是否成功
     */
    boolean loadConfig();


    /**
     * 状态机主循环
     */
    void update();


    /**初始化状态机并加载
     * @param config 主配置文件
     * @param context   上下文配置
     * @param serviceMap 状态机提供的可调用业务类
     */
    void start(AgentMachineConfig config, AgentContext context, Map<String, Object> serviceMap);
}
