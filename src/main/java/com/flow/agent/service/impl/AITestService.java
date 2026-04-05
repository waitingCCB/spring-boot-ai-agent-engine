package com.flow.agent.service.impl;

import com.flow.agent.annotation.AgentMethod;
import com.flow.agent.annotation.AgentParam;
import com.flow.agent.annotation.AgentTool;
import com.flow.agent.service.AiService;
import com.flow.agent.service.IAITestService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AgentTool(serviceName = "aiTestService", desc = "AI测试用工具，可以用来生成随机文本")
@Slf4j
@Service
public class AITestService implements IAITestService {

    @Autowired
    AiService aiService;



    @AgentMethod(methodName = "generateBackGround", desc = "生成一个随机的故事背景")
    @Override
    public String generateBackGround() {

        return aiService.generateAnswerByPrompt("随机生成一个故事背景",
                " 你是一个故事背景生成器。请严格遵循以下要求：\n" +
                "            1. 字数严格控制在80字以内。\n" +
                "            请生成故事背景。");
    }

    @AgentMethod(methodName = "generateStory", desc = "根据故事背景和主题，生成一个随机的故事")
    @Override
    public String generateStory(
            @AgentParam(name = "backGround", desc = "故事背景", type = "字符串") String backGround,
            @AgentParam(name = "topic", desc = "故事主题", type = "字符串") String topic) {
        String problem = "我想要生成一个故事，故事的背景为{" + backGround + "}, 故事的主题为{"+ topic+ "}";
        return aiService.generateAnswerByPrompt(problem, "你是一个故事生成器。请严格遵循以下要求：1.150字以上，不要超过500字");
    }


    @AgentMethod(methodName = "finish", desc = "把数据保存到数据库并打印在控制台")
    @Override
    public void finish(@AgentParam(name = "story", desc = "数据", type = "字符串") String story) {
        log.info("生成的故事为：{}", story);
        log.info("可以在这里把故事保存到数据库");
    }
}
