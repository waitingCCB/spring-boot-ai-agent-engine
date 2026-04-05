package com.flow.agent.service.impl;



import com.alibaba.fastjson2.JSON;

import com.flow.agent.scanner.AgentFunctionScanner;
import com.flow.agent.core.AgentMachineConfig;
import com.flow.agent.core.AgentStep;
import com.flow.agent.core.AiFlowResponse;
import com.flow.agent.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AgentUtils {

    @Autowired
    private AiService aiService; // 现有的AI调用服务

    /** 根据用户的问题自动生成配置文件
     * @param question 用户问题
     * @return AI自己生成的配置文件
     */
    public String getJsonConfig(String question) {
        // 1. 构建提示词
        String functionList = buildFunctionText(AgentFunctionScanner.FUNCTION_POOL);
        String prompt = buildPrompt(functionList, question);

        // 2. 调用AI
        String aiResponse = aiService.generateAnswerByPrompt(prompt, "只返回JSON");


        // 把 AI 返回的 {flowName, steps}
        // 转成为配置类格式 {agentStepListMap: {flowName: steps}}
        AiFlowResponse flow = JSON.parseObject(aiResponse, AiFlowResponse.class);

        AgentMachineConfig finalConfig = new AgentMachineConfig();
        finalConfig.setAgentId("auto_agent");
        finalConfig.setAgentName(flow.getFlowName());
        finalConfig.setAccount("123456");
        finalConfig.setQuestion(question);

        // 关键：构造 agentStepListMap
        Map<String, List<AgentStep>> stepMap = new HashMap<>();
        stepMap.put(flow.getFlowName(), flow.getSteps());
        finalConfig.setAgentStepListMap(stepMap);

        // 3. 返回最终可直接解析的配置JSON
        return JSON.toJSONString(finalConfig);
    }


    // 内部工具：把扫描到的方法 → 拼成给AI看的说明
    private String buildFunctionText(Map<String, List<AgentFunctionScanner.FunctionInfo>> functionPool) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, List<AgentFunctionScanner.FunctionInfo>> entry : functionPool.entrySet()) {
            String serviceName = entry.getKey();
            sb.append("\n【服务：").append(serviceName).append("】\n");

            for (AgentFunctionScanner.FunctionInfo func : entry.getValue()) {
                sb.append("- 方法：").append(func.getMethodName()).append("\n");
                sb.append("  描述：").append(func.getDesc()).append("\n");
                if (!func.getParams().isEmpty()) {
                    sb.append("  参数：");
                    func.getParams().forEach(p -> {
                        sb.append(p.getName()).append("(").append(p.getType()).append(")：")
                                .append(p.getDesc()).append("，");
                    });
                    sb.setLength(sb.length() - 1);
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }


    // 内部工具：提示词模板
    private String buildPrompt(String functionList, String question) {
        return """
            你是工作流编排引擎，根据用户需求，生成可执行的步骤配置。

            可用业务能力：
            %s

            用户需求：%s

            请严格按照以下JSON格式返回，不要输出任何多余内容：
            {
              "flowName": "流程名称",
              "steps": [
                {
                  "id": "步骤ID",
                  "needService": "服务名",
                  "needMethod": "方法名",
                  "useLastResult": true/false,
                  "params": {
                    "参数名": "参数值"
                  }
                }
              ]
            }

            规则：
            1. useLastResult=true 表示把上一步结果作为当前方法第一个参数
            2. params 只填用户提供的固定参数
            3. 步骤必须按逻辑顺序
            """.formatted(functionList, question);
    }
}