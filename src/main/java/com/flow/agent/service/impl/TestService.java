package com.flow.agent.service.impl;

import com.flow.agent.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService implements ITestService {


    @Override
    public void sayHello() {
        log.info("Hello World!!! I am TestService");
    }
}
