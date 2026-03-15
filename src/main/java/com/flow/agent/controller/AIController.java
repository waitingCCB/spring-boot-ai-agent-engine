package com.flow.agent.controller;

import com.flow.agent.service.AiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/ai")
@Tag(name = "ai接口")
public class AIController {

    @Autowired
    private AiService aiService;


}
