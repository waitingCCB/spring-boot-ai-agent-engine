package com.flow.agent.service;

public interface IAITestService {

    /**
     * @return AI生成的背景大纲
     */
    String generateBackGround();


    /**
     * @param topic 话题
     * @param backGround  故事背景
     * @return  故事
     */
    String generateStory(String backGround, String topic);

    /**日志打印与保存
     * @param story :故事
     */
    void finish(String story);


}
