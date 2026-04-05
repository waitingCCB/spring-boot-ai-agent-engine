package com.flow.agent.machine;


import com.alibaba.fastjson2.JSON;
import com.flow.agent.annotation.EnableAiAgentEngine;
import com.flow.agent.autoconfigure.AgentAutoConfiguration;
import com.flow.agent.common.AgentConfigRegistry;
import com.flow.agent.core.AgentContext;
import com.flow.agent.core.AgentMachineConfig;
import com.flow.agent.core.IAgentMachine;
import com.flow.agent.service.AiService;
import com.flow.agent.service.impl.AITestService;
import com.flow.agent.service.impl.AgentUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest   // 想测试去掉注释就行
@EnableAiAgentEngine
@Slf4j
public class AiMachineTest {

    @Autowired
    AgentConfigRegistry agentConfigRegistry;

    @Autowired
    IAgentMachine agentMachine;


    @Autowired
    AiService aiService;

    @Autowired
    AITestService aiTestService;

    @Autowired
    AgentUtils agentUtils;

    /**
     * 根據配置文件运行工作流
     */
    @Test
    public void testMachineRun() {

        String jsonConfig = """
            {
                "agentId": "agent_001",
                "agentName": "智能问答助手",
                "account": "123456",
                "question": "我想要进行测试",
                "agentStepListMap": {
                    "进行测试": [
                        {
                            "id": "step1",
                            "needService": "aiService",
                            "needMethod": "test"
                        }
                    ],
                    "进行多工作步骤测试": [
                        {
                            "id": "step1",
                            "needService": "aiService",
                            "needMethod": "generateData"
                        },
                        {
                            "id": "step2",
                            "needService": "aiService",
                            "needMethod": "dealData",
                            "useLastResult": true,
                            "params": {
                            }
                        },
                        {
                            "id": "step3",
                            "needService": "aiService",
                            "needMethod": "finish",
                            "useLastResult": true,
                            "params": {
                                "param2": "Hello"
                            }
                        },
                        {
                            "id": "step4",
                            "needService": "testService",
                            "needMethod": "sayHello",
                        }
                    ]
                }
            }
            """;
        // JSON字符串 → 配置类
        AgentMachineConfig config = JSON.parseObject(jsonConfig, AgentMachineConfig.class);

        config.setQuestion("我想要进行多工作步骤的测试");

        agentConfigRegistry.saveConfig(config.getAccount(), config);

        // 状态机初始运行就是纯净的
        AgentContext agentContext = new AgentContext();
        agentContext.setUserQuestion(config.getQuestion());

        // 给状态机提供业务类
        Map<String, Object> serviceMap = new HashMap<String, Object>();

        serviceMap.put("aiService", aiService);



        // 启动状态机
        agentMachine.start(config, agentContext, serviceMap);




        log.info("最终上下文为{}", agentContext);
    }


    /**
     * AI自动生成配置文件
     */
    @Test
    public void testAutoMachineRun() {
        String userQuestion = "我想要听一个开心的故事";

        // 1. AI 自动生成配置JSON
        String configJson = agentUtils.getJsonConfig(userQuestion);

        // 2. 直接解析成配置类
        AgentMachineConfig config = JSON.parseObject(configJson, AgentMachineConfig.class);

        // 3. 提供业务服务
        Map<String, Object> serviceMap = new HashMap<>();
        serviceMap.put("aiTestService", aiTestService);
        serviceMap.put("aiService", aiService);


        // 4. 运行状态机
        AgentContext context = new AgentContext();
        context.setUserQuestion(userQuestion);

        agentMachine.start(config, context, serviceMap);

        log.info("最终的上下文为{}", context);
    }
}
