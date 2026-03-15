package com.flow.agent.machine;

import com.alibaba.fastjson2.JSON;
import com.flow.agent.entity.AgentMachineConfig;
import com.flow.agent.service.AiService;
import com.flow.agent.Machine.IAgentMachine;
import com.flow.agent.common.AgentConfigRegistry;
import com.flow.agent.entity.AgentContext;
import com.flow.agent.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
public class AiMachineTest {


    @Autowired
    AgentConfigRegistry agentConfigRegistry;



    @Autowired
    IAgentMachine agentMachine;


    @Autowired
    ITestService testService;


    @Autowired
    AiService aiService;

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
        serviceMap.put("testService", testService);


        agentMachine.start(config, agentContext, serviceMap);




        log.info("最终上下文为{}", agentContext);
    }


}
