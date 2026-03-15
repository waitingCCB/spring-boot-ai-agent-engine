package com.flow.agent.Machine;

import com.flow.agent.enmu.AgentEvent;
import com.flow.agent.enmu.AgentState;

import java.util.function.Consumer;

public interface ITransition {


    /**
     * 添加事件处理规则
     * @param event 事件
     * @param nextState 下一个事件
     * @param action    要执行的动作
     */
    void addBranch(AgentEvent event, AgentState nextState, Consumer<String> action);


    /**重载方法，不带动作
     * @param event 事件
     * @param nextState 下一状态
     */
    void addBranch(AgentEvent event, AgentState nextState);

    /** 根据事件获取下一个状态
     * @param event 事件
     * @return 下一个状态
     */
    AgentState getNextState(AgentEvent event);

    /**执行动作，因为是事件 + 状态混合驱动，基本只做调试信息打印
     * @param event 发生的事件
     * @param data  调试信息
     */
    void executeAction(AgentEvent event, String data);

    /**判断该事件是否有转换规则
     * @param event 事件
     * @return 是否有转换规则
     */
    boolean hasTransition(AgentEvent event);
}
