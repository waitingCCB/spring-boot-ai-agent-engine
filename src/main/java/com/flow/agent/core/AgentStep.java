package com.flow.agent.core;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


// 工作链条步骤类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentStep {
    private String id;
    private String intent;
    private String needService;
    private Map<String, Object> params;
    private String needMethod;
    private boolean useLastResult;
}
