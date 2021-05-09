package com.personal.kindergarten.service.impl;

import com.personal.kindergarten.bean.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 发送邮件
 * @author lxw
 * @date 2021-05-09
 */
@Slf4j
@Service
public class SendMailServiceImpl {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    public boolean sendHtmlMail(Email email){
        boolean success=true;
        try {
            //创建一个MIME消息
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, false);

            //发送人
            mimeMessageHelper.setFrom(mailProperties.getUsername());

            //收邮件人
            mimeMessageHelper.setTo(email.getReceivers());

            //邮件主题
            mimeMessageHelper.setSubject(email.getSubject());

            //邮件内容   true表示带有附件或html
            mimeMessageHelper.setText(email.getContent(), true);

            mailSender.send(message);
        }catch (MessagingException e){
            success=false;
        }
        return  success;
    }
}
