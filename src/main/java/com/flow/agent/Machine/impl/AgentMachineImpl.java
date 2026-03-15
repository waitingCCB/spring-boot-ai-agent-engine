package com.flow.agent.Machine.impl;

import com.flow.agent.Machine.IAgentChain;
import com.flow.agent.Machine.IAgentMachine;
import com.flow.agent.Machine.IIntentRecognizer;
import com.flow.agent.enmu.AgentEvent;
import com.flow.agent.enmu.AgentState;
import com.flow.agent.entity.AgentContext;
import com.flow.agent.entity.AgentMachineConfig;
import com.flow.agent.entity.AgentStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class AgentMachineImpl implements IAgentMachine {


    // 无缝结合到已经有的业务里，尽量不编写新业务，只组合
    Map<String, Object> serviceMap = new HashMap<>();


    private Map<String, List<AgentStep>> agentStepListMap;

    // 状态转换表：状态 → 事件分支
    private final Map<AgentState, Transition> stateMap = new HashMap<>();

    // ====================== 运行时状态 ======================

    private AgentState currentState;     // 当前状态

    private String intent;  // 识别出的意图（仅一次）

    private AgentContext agentContext;  // 本次请求上下文（用户数据、结果）


    private AgentMachineConfig agentConfig; // 用户配置（工作流定义）


    @Autowired(required = false)
    private IIntentRecognizer intentRecognizer; // 意图识别器


    /**
     * 初始化状态表
     */
    @Override
    public void initTable() {
        stateMap.clear();

        // 1. 加载配置 → 加载完成 → 意图判断
        Transition loadConfigTrans = new Transition();
        loadConfigTrans.addBranch(
                AgentEvent.EV_LOAD_CONFIG_SUCCESS,
                AgentState.JUDGE_INTENT,
                data -> log.info("✅ 配置加载完成，准备识别意图")
        );
        stateMap.put(AgentState.LOAD_CONFIG, loadConfigTrans);

        // 2. 意图判断 → 得出意图 → 执行工作链
        Transition judgeIntentTrans = new Transition();
        judgeIntentTrans.addBranch(
                AgentEvent.EV_GOT_INTENT,
                AgentState.DO_CHAIN,
                data -> log.info("✅ 意图识别完成：" + intent)
        );
        stateMap.put(AgentState.JUDGE_INTENT, judgeIntentTrans);

        // 3. 执行工作链 → 执行完成 → 输出结果
        Transition doChainTrans = new Transition();
        doChainTrans.addBranch(
                AgentEvent.EV_CHAIN_DONE,
                AgentState.FINISH,
                data -> log.info("✅ 工作流执行完成，准备输出结果")
        );
        stateMap.put(AgentState.DO_CHAIN, doChainTrans);

        // 初始状态：加载配置
        this.currentState = AgentState.LOAD_CONFIG;
        log.info("=== 状态机初始化完成 ===");
    }



    /**
     * 启动一次状态机运行
     * @param config 从全局仓库获取的用户配置（包含<意图, List<步骤>>）
     * @param context 本次请求上下文（用户问题、账号等）
     * @param serviceMap 状态机提供的业务类
     */
    public void start(AgentMachineConfig config, AgentContext context,   Map<String, Object> serviceMap) {
        System.out.println("\n=== 启动新一次状态机运行 ===");
        // 重置所有运行时状态
        this.agentConfig = config;
        this.agentContext = context;
        this.intent = null;
        this.currentState = AgentState.LOAD_CONFIG;
        this.serviceMap = serviceMap;



        this.agentStepListMap = config.getAgentStepListMap();


        // 初始化状态表
        this.initTable();

        // 正式启动
        this.update();
    }


    @Override
    public String judgeIntent() {
        if (intentRecognizer == null) {
            throw new RuntimeException("未注入意图识别器");
        }

        List<String> intentList = new ArrayList<>();
        for(String intent : agentStepListMap.keySet()) {
            intentList.add(intent);
        }


        // 从上下文获取用户问题，识别一次意图
        return intentRecognizer.RecognizedIntent(agentContext, intentList);
    }


    @Override
    public boolean loadConfig() {

        return agentContext != null && agentConfig != null;
    }


    @Override
    public void update() {
        log.info("\n当前状态：" + currentState);

        switch (currentState) {
            // 1. 加载用户自定义工作流
            case LOAD_CONFIG:
                if (loadConfig()) {
                    triggerEvent(AgentEvent.EV_LOAD_CONFIG_SUCCESS);
                }
                break;

            // 2. 判断意图（仅一次）
            case JUDGE_INTENT:
                this.intent = judgeIntent();
                triggerEvent(AgentEvent.EV_GOT_INTENT);
                break;

            // 3. 执行工作链条（完全交给用户配置的流程）
            case DO_CHAIN:
                try {
                    // 从配置中获取当前意图对应的步骤列表
                    List<AgentStep> stepList = agentStepListMap.get(intent);
                    if (stepList == null || stepList.isEmpty()) {
                        throw new RuntimeException(" 意图[" + intent + "]无对应工作流步骤");
                    }


                    // 创建工作链条类
                    IAgentChain agentChain = new AgentChainImpl();
                    agentChain.doChain(agentContext, stepList, serviceMap);

                    // 触发完成事件
                    triggerEvent(AgentEvent.EV_CHAIN_DONE);
                } catch (Exception e) {
                    log.info("❌ 工作流执行失败：" + e.getMessage());
                    return;
                }
                break;

            // 4. 输出结果
            case FINISH:
                log.info("🎉 状态机执行完成");

                return;


            // 错误状态
            case ERROR:
                log.info("❌ 状态机异常终止");

                return;
        }
    }


    /**触发事件
     * @param event 当前发生的事件
     */
    private void triggerEvent(AgentEvent event) {
        // 1. 获取当前状态的转换规则
        Transition transition = stateMap.get(currentState);
        if (transition == null) {
            log.info("❌ 当前状态无转换规则：" + currentState);
            currentState = AgentState.ERROR;
            return;
        }

        // 2. 检查事件是否被支持
        if (!transition.hasTransition(event)) {
            log.info("❌ 状态[" + currentState + "]不支持事件：" + event);
            currentState = AgentState.ERROR;
            return;
        }

        // 3. 执行事件动作
        transition.executeAction(event, "");

        // 4. 切换到下一个状态
        AgentState oldState = currentState;
        currentState = transition.getNextState(event);

        // 5. 打印状态切换日志
        log.info("🔄 状态切换：" + oldState + " → " + currentState + " (事件：" + event + ")");

        // 6. 自动执行下一个状态
        update();
    }


    public AgentState getCurrentState() {
        return currentState;
    }
}