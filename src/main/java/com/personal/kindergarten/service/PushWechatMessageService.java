package com.personal.kindergarten.service;

import java.util.Map;

public interface PushWechatMessageService {

    /**
     * 推送公众号所有人微信消息
     * @return 返回对应推送用户以及成功状态
     */
    public Map pushAllMessage();

    /**
     * 推送公众号指定人微信消息
     * @return 返回对应推送成功状态
     */
    public boolean pushMessage(String openId);
}
