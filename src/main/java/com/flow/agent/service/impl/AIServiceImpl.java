package com.flow.agent.service.impl;

import com.flow.agent.service.AiService;
import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AIServiceImpl implements AiService {

    @Autowired
    private ArkService arkService; // 直接注入使用



    @Value("${doubao.ark.model}")
    private String model;


    /**
     * 极简意图识别：直接返回匹配的意图
     * @param question 用户问题
     * @param intents 意图列表
     * @return 匹配的意图 / 不符合
     */
    @Override
    public String gotIntent(String question, List<String> intents) {
        try {
            // 官方格式提示词
            String prompt = """
            你是意图识别器。
            可选意图：%s
            用户问题：%s
            规则：
            1. 只返回【匹配到的意图文本】
            2. 不要加任何符号：不要加[]、不要加引号、不要加空格
            3. 无匹配返回：不符合
            """.formatted(intents, question);

            // 把消息放进列表！！！
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.builder()
                    .role(ChatMessageRole.SYSTEM)
                    .content(prompt)
                    .build());

            // 官方标准非流式请求
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)  // 现在消息列表有效
                    .stream(false)
                    .temperature(0.0)   // 结果最稳定
                    .maxTokens(64)      // 意图只需要很短
                    .build();

            // 官方标准调用 + 安全取值
            var response = arkService.createChatCompletion(request);
            String result = response.getChoices().get(0).getMessage().getContent().toString().trim();

            log.info("意图识别结果：{}", result);
            return result;

        } catch (Exception e) {
            log.error("意图识别失败", e);
            return "不符合";
        }
    }



    public String test(){
        return "测试成功！！！状态机正常运行";
    }


    public String generateData(){
        return "我是数据";
    }

    public String dealData(String data){
        data = "数据已经处理";
        return data;
    }

    public String finish(String data, String param2){
        log.info("第二个参数为" + param2);
        return "全部数据处理完成，最终数据为" + data;
    }
}
