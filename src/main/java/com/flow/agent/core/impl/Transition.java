package com.flow.agent.core.impl;

import com.flow.agent.core.ITransition;
import com.flow.agent.enmu.AgentEvent;
import com.flow.agent.enmu.AgentState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 状态机转移规则
 * 对应：事件 → 目标状态 → 执行动作
 * 设计：轻量、事件驱动、状态驱动混合
 */
public class Transition implements ITransition {

    // 事件 -> 下一个状态
    private final Map<AgentEvent, AgentState> eventToStateMap = new HashMap<>();

    // 事件 -> 要执行的动作
    private final Map<AgentEvent, Consumer<String>> eventToActionMap = new HashMap<>();

    public Transition() {
    }


    @Override
    public void addBranch(AgentEvent event, AgentState nextState, Consumer<String> action) {
        eventToStateMap.put(event, nextState);
        if (action != null) {
            eventToActionMap.put(event, action);
        }
    }

    /**
     * 重载：不带动作
     */
    @Override
    public void addBranch(AgentEvent event, AgentState nextState) {
        addBranch(event, nextState, null);
    }

    /**
     * 获取下一个状态
     */
    @Override
    public AgentState getNextState(AgentEvent event) {
        return eventToStateMap.getOrDefault(event, AgentState.ERROR);
    }

    /**
     * 执行事件对应的动作
     */
    @Override
    public void executeAction(AgentEvent event, String data) {
        Consumer<String> action = eventToActionMap.get(event);
        if (action != null) {
            action.accept(data);
        }
    }

    /**
     * 是否存在该事件的转移规则
     */
    @Override
    public boolean hasTransition(AgentEvent event) {
        return eventToStateMap.containsKey(event);
    }
}