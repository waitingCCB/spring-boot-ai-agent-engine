# spring-boot-ai-agent-engine 轻量级状态机工作流引擎 V 0.1
轻量级 · 可扩展 · AI 智能体核心框架

+ ✨ 一个轻量级的 AI 智能体（AI Agent）实验引擎，基于状态机流程控制 + 大模型意图识别，实现自动化任务编排与执行。

+ ✨ 目的是尽量实现与原有业务类的无缝结合，由此，可以方便的把原有的业务添加到工作流当中

---

## 项目介绍
这是一个轻量级 AI 智能体核心框架，用于学习与实践当前热门的 AI Agent 架构思想。
项目通过状态机管理流程，通过大模型识别用户意图，并自动执行对应的任务链条。

已实现核心能力：
✅ AI 意图识别（Intent Recognition）
✅ 状态机流程控制（State Machine）
✅ 可配置任务执行链（Task Chain）
✅ 上下文管理（Context Management）
✅ 模块化服务扩展



# 效果

## 测试代码

具体内容在 \src\test\java\com\flow\agent\machine\AiMachineTest.java 下

```
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


        // 正式启动由用户决定
        agentMachine.update();

        log.info("最终上下文为{}", agentContext);
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

![image-20260315205800291](C:/Users/wjf/AppData/Roaming/Typora/typora-user-images/image-20260315205800291.png)

---



## 状态机

![image-20260315210100761](C:/Users/wjf/AppData/Roaming/Typora/typora-user-images/image-20260315210100761.png)

意图判断部分，可以自由实现 IIntentRecognizer 接口，实现关键词匹配，或调用不同的AI进行判断

核心是只要加载配置文件，就能自动运行状态机

---

# 项目价值



本项目非常适合用于学习：
✅ Spring Boot 项目结构
✅ AI Agent 基础设计思想
✅ 状态机设计模式实践
✅ 意图识别 + 流程执行
✅ 小型自动化引擎开发



项目实现了 AI Agent 的**最小可用内核**，包含：
- 流程状态管理
- 意图理解
- 任务自动执行
- 上下文传递

区别于单纯调用 AI 接口，具备**完整的自动化执行闭环**。



可作为基础框架进一步扩展为：
✅ 个人 AI 助手
✅ 简单自动化任务工具
✅ 智能对话流程引擎
✅ 轻量级业务自动化流程

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

# spring-boot-ai-agent-engine V 0.1

### Lightweight · Extensible · Core Framework for AI Agent

A lightweight experimental engine for AI Agent, based on **state machine flow control + LLM intent recognition**, to realize automated task orchestration and execution.

The core goal is to achieve seamless integration with existing business services, making it easy to add legacy business logic to the workflow.

### Core Implemented Capabilities

- ✅ AI Intent Recognition (extensible interface)
- ✅ State Machine Flow Control
- ✅ Configurable Task Execution Chain
- ✅ Global Context Management
- ✅ Modular Service Extension
- ✅ Seamless Call of Multi-Service
- ✅ Automatic Result Transfer Between Steps

### Project Value

#### For Learning

- Spring Boot project architecture practice
- Basic design ideas of AI Agent
- State Machine/Strategy design pattern application
- Integration of intent recognition and workflow execution
- Development of lightweight automation engine

#### For Extension

Can be extended as a basic framework to:

- Personal AI Assistant
- Simple automated task tool
- Intelligent dialogue flow engine
- Lightweight business automation process

### Tech Stack

- Spring Boot
- LLM (Volcano Ark / Doubao)
- State Machine Pattern
- Strategy Pattern
- Global Context Management
- Lightweight operation (no database dependency)