package com.personal.kindergarten.bean;

import lombok.Data;

@Data
public class Email {
    /**
     * 邮件接收方，可多人
     */
    private String[] receivers;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String content;
}
