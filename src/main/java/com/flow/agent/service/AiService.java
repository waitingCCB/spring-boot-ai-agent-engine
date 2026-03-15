package com.flow.agent.service;

import java.util.List;

public interface AiService {


    String gotIntent(String question, List<String> intents);
}
