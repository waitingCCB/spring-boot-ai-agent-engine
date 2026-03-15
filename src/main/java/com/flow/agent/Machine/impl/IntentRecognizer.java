package com.flow.agent.Machine.impl;

import com.flow.agent.Machine.IIntentRecognizer;
import com.flow.agent.entity.AgentContext;
import com.flow.agent.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class IntentRecognizer implements IIntentRecognizer {

    @Autowired
    AiService aiService;


    @Override
    public String RecognizedIntent(AgentContext agentContext, List<String> intentList) {

        log.info("接口识别用户意图");
        String question = agentContext.getUserQuestion();


        return aiService.gotIntent(question, intentList);
    }
}
