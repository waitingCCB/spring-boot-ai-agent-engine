# spring-boot-ai-agent-engine 轻量级状态机工作流引擎 V 0.2
轻量级 · 可扩展 · AI 智能体核心框架

+ ✨ 一个轻量级的 AI Agent 实验引擎，基于状态机流程控制 + 大模型意图识别，实现自动化任务编排与执行。

+   ✨ 无侵入式架构，可与现有业务无缝集成，轻松将原有业务接入 AI 工作流。


---

# V0.2更新：

+ ✅ 重构为标准 Spring Boot Starter，支持自动装配，开箱即用 
+ ✨ 采用配置化驱动，支持外部配置，不再硬编码密钥，安全开源 

+ ✨ 移除启动类，纯依赖库结构，更适合作为二方包/开源组件使用  
+ ✨ 集成火山引擎/豆包大模型 API，支持 HTTP 连接池、超时、模型配置 
+ ✅ 优化 Bean 加载机制，支持 @EnableAiAgentEngine 一键启用 
+ ✅ 项目结构彻底清理，代码更简洁、更专业、更易于维护 
+ ✅ 支持本地 Maven 打包、install、多项目复用



---



## 项目介绍

这是一个轻量级 AI 智能体核心框架，用于实践当前热门的 AI Agent 架构思想。
项目通过状态机管理流程，通过大模型识别用户意图，并自动执行对应的任务链条。

已实现核心能力：

+  ✅ AI 意图识别（Intent Recognition） 
+ ✅ 状态机流程控制（State Machine） 
+ ✅ 可配置任务执行链（Task Chain） 
+ ✅ 上下文管理（Context Management） 
+ ✅ 模块化服务扩展 
+ ✅ Spring Boot Starter 自动化集成

---



# 如何使用

在配置文件添加如下配置，并填写豆包的key

```
doubao:
  ark:
    api-key: # 替换为真实API Key
    base-url: https://ark.cn-beijing.volces.com/api/v3 # 替换为示例代码里的服务地址
    model: doubao-seed-1-6-flash-250828 # 替换为模型名字
  pool:
    max-idle-connections: 5
    keep-alive-duration: 1

```



## 一、核心功能说明

本引擎支持两种使用方式：

1. **手动编写 JSON 流程配置 → 运行状态机**
2. **AI 自动生成流程配置 → 一键执行全自动工作流**

支持通过注解注册业务服务，AI 可自动识别并编排执行步骤。

------

## 二、快速使用（全自动模式）

AI 自动生成工作流 + 自动执行

```
@SpringBootTest(classes = AgentAutoConfiguration.class)
@EnableAiAgentEngine
public class TestDemo {

    @Autowired
    private AgentUtils agentUtils;

    @Autowired
    private IAgentMachine agentMachine;

    @Autowired
    private AiTestService aiTestService;

    @Autowired
    private AiService aiService;

    @Test
    public void testAutoRun() {
        // 用户输入一句话
        String userQuestion = "我想要听一个开心的故事";

        // 1. AI 自动生成流程配置文件
        String configJson = agentUtils.getJsonConfig(userQuestion);
        AgentMachineConfig config = JSON.parseObject(configJson, AgentMachineConfig.class);

        // 2. 注册业务服务，你想给AI提供哪些业务类调用
        Map<String, Object> serviceMap = new HashMap<>();
        serviceMap.put("aiTestService", aiTestService);
        serviceMap.put("aiService", aiService);

        // 3. 启动 AI 状态机
        AgentContext context = new AgentContext();
        context.setUserQuestion(userQuestion);
        agentMachine.start(config, context, serviceMap);

        // 输出最终结果
        System.out.println("执行结果：" + context);
    }
}
```

------

## 三、手动配置模式（自定义流程）

你可以手动编写 JSON 流程，自由编排步骤：

```
@Test
public void testManualRun() {
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
                    "useLastResult": true
                },
                {
                    "id": "step3",
                    "needService": "aiService",
                    "needMethod": "finish",
                    "useLastResult": true
                }
            ]
        }
    }
    """;

    AgentMachineConfig config = JSON.parseObject(jsonConfig, AgentMachineConfig.class);
    agentConfigRegistry.saveConfig(config.getAccount(), config);

    // 启动状态机
    AgentContext agentContext = new AgentContext();
    agentContext.setUserQuestion(config.getQuestion());

    Map<String, Object> serviceMap = new HashMap<>();
    serviceMap.put("aiService", aiService);

    agentMachine.start(config, agentContext, serviceMap);
}
```

------

## 四、业务类编写规范（@AgentTool + @AgentMethod）

只需要给业务类加上注解，AI 就能自动识别、编排、调用

```
@AgentTool(
    serviceName = "aiTestService",    // 服务名称
    desc = "AI测试用工具，可以生成故事"  // 服务描述
)
@Service
public class AITestService {

    @AgentMethod(methodName = "generateStory",desc = "根据背景和主题生成故事")
    public String generateStory(
    		@AgentParam(name = "backGround", desc = "故事背景") String backGround,
            @AgentParam(name = "topic", desc = "故事主题") String topic
            ) {
        // 你的业务逻辑
        return "生成的故事内容...";
    }
}
```

------

## 五、核心注解说明

|          注解          |                作用                |
| :--------------------: | :--------------------------------: |
| `@EnableAiAgentEngine` |         启用 AI 智能体引擎         |
|      `@AgentTool`      |   标记一个业务服务，让 AI 可识别   |
|     `@AgentMethod`     | 标记一个方法，作为 AI 可执行的步骤 |
|     `@AgentParam`      |    声明方法参数，AI 可自动填充     |

------

## 六、执行流程

1. 用户输入一句话
2. AI 自动识别意图
3. AI 自动生成执行流程
4. 状态机自动执行步骤
5. 自动调用你的业务方法
6. 返回最终结果

------





# 效果

## 完整测试代码

具体内容在 \src\test\java\com\flow\agent\machine\AiMachineTest.java 下

```

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


```

输出结果

```
=== 启动新一次状态机运行 ===
2026-03-15T20:48:40.889+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : === 状态机初始化完成 ===
2026-03-15T20:48:40.889+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : 
当前状态：LOAD_CONFIG
2026-03-15T20:48:40.890+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : ✅ 配置加载完成，准备识别意图
2026-03-15T20:48:40.890+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : 🔄 状态切换：LOAD_CONFIG → JUDGE_INTENT (事件：EV_LOAD_CONFIG_SUCCESS)
2026-03-15T20:48:40.890+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : 
当前状态：JUDGE_INTENT
2026-03-15T20:48:40.890+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.IntentRecognizer  : 接口识别用户意图
2026-03-15T20:48:58.131+08:00  INFO 22808 --- [           main] c.flow.agent.service.impl.AIServiceImpl  : 意图识别结果：进行多工作步骤测试
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : ✅ 意图识别完成：进行多工作步骤测试
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : 🔄 状态切换：JUDGE_INTENT → DO_CHAIN (事件：EV_GOT_INTENT)
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : 
当前状态：DO_CHAIN
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 执行步骤：step1
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 调用服务：aiService
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 执行结果：我是数据
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 执行步骤：step2
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 调用服务：aiService
2026-03-15T20:48:58.133+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : 第一个参数使用上一步的结果，赋值为我是数据
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 执行结果：数据已经处理
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 执行步骤：step3
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 调用服务：aiService
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : 第一个参数使用上一步的结果，赋值为数据已经处理
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : 第1个参数赋值为Hello
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.flow.agent.service.impl.AIServiceImpl  : 第二个参数为Hello
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 执行结果：全部数据处理完成，最终数据为数据已经处理
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 执行步骤：step4
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 调用服务：testService
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.flow.agent.service.impl.TestService    : Hello World!!! I am TestService
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentChainImpl    : → 执行结果：执行成功无返回值
2026-03-15T20:48:58.134+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : ✅ 工作流执行完成，准备输出结果
2026-03-15T20:48:58.135+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : 🔄 状态切换：DO_CHAIN → FINISH (事件：EV_CHAIN_DONE)
2026-03-15T20:48:58.135+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : 
当前状态：FINISH
2026-03-15T20:48:58.135+08:00  INFO 22808 --- [           main] c.f.agent.Machine.impl.AgentMachineImpl  : 🎉 状态机执行完成
2026-03-15T20:48:58.135+08:00  INFO 22808 --- [           main] com.flow.agent.machine.AiMachineTest     : 最终上下文为AgentContext(account=null, agentId=null, aiFinalAnswer=null, intent=null, userQuestion=我想要进行多工作步骤的测试, nowServiceResult=执行成功无返回值, agentState=null)

```



---

# 基本架构

## 时序图

![image-20260315205800291](https://cdn.jsdelivr.net/gh/waitingCCB/image-bed@main/image/image-20260315205800291.png)

---



## 状态机

![image-20260315210100761](https://cdn.jsdelivr.net/gh/waitingCCB/image-bed@main/image/image-20260315210100761.png)

使用的是事件 + 状态混合驱动的状态机。状态表如下

![image-20260315220504180](C:/Users/wjf/AppData/Roaming/Typora/typora-user-images/image-20260315220504180.png)

意图判断部分，可以自由实现 IIntentRecognizer 接口，实现关键词匹配，或调用不同的AI进行判断

核心是只要加载配置文件，就能自动运行状态机



---



用于学习：

+ ✅ Spring Boot 项目结构
+ ✅ AI Agent 基础设计思想
+ ✅ 状态机设计模式实践
+ ✅ 意图识别 + 流程执行
+ ✅ 小型自动化引擎开发



项目实现了 AI Agent 的**最小可用内核**，包含：
- 流程状态管理
- 意图理解
- 任务自动执行
- 上下文传递

区别于单纯调用 AI 接口，具备**完整的自动化执行闭环**。



可作为基础框架进一步扩展为：

+ ✅ 个人 AI 助手
+ ✅ 简单自动化任务工具
+ ✅ 智能对话流程引擎
+ ✅ 轻量级业务自动化流程

项目无冗余依赖，结构清晰，易于理解和二次开发。

---

# 技术栈
- Spring Boot
- 火山方舟 · 豆包大模型
- 状态机设计模式
- 策略模式
- 上下文管理
- 无数据库轻量运行

---







---

# Spring Boot AI Agent Engine - Lightweight State Machine Workflow Engine V 0.2 
Lightweight · Scalable · Core Framework for AI Intelligent Entities 
+ ✨ A lightweight AI Agent experimental engine, based on state machine process control + Large model intent recognition, enabling automated task orchestration and execution. 
+   ✨  Non-intrusive architecture, capable of seamless integration with existing business systems, allowing easy integration of the original business processes into the AI workflow. 
---

# V0.2 Update: 
+ ✅ Reorganized into a standard Spring Boot Starter, supporting automatic configuration and ready to use out of the box
+ ✨ Utilizes configuration-driven approach, supporting external configuration, and no longer hard-codes keys. It is secure and open-source. 
+ ✨ Removed the startup class, with a pure dependency library structure, making it more suitable for use as a second-party package or open-source component
+ ✨ Integrated the API of Huoshan Engine/Doubao Large Model, supporting HTTP connection pool, timeouts, and model configuration
+ ✅ Optimized the Bean loading mechanism, supporting one-click enablement with @EnableAiAgentEngine
+ ✅ Thoroughly cleaned up the project structure, making the code more concise, professional, and easier to maintain
+ ✅ Supports local Maven packaging, installation, and reuse across multiple projects + 📝 Improved documentation, allowing for direct upload to GitHub for open-source use 


---



## Project Introduction 
This is a lightweight AI intelligent core framework, used to implement the current popular AI Agent architecture concept.
The project manages processes through a state machine, identifies user intentions through a large model, and automatically executes the corresponding task chains. 
Achieved core capabilities: 

+ ✅ AI Intent Recognition
+ ✅ State Machine Process Control
+ ✅ Configurable Task Execution Chain
+ ✅ Context Management
+ ✅ Modular Service Extension
+ ✅ Spring Boot Starter Automated Integration