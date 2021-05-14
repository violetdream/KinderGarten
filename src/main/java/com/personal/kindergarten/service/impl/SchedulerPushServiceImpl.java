package com.personal.kindergarten.service.impl;

import com.personal.kindergarten.bean.Email;
import com.personal.kindergarten.config.StockIdTypeProperties;
import com.personal.kindergarten.service.CrawlingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.sql.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@EnableScheduling
public class SchedulerPushServiceImpl {
    @Autowired
    private StockIdTypeProperties stockIdTypeProperties;

    @Autowired
    private SendMailServiceImpl sendMailService;
    @Autowired
    private CrawlingService crawlingService;

    private String[] receviers=new String[]{"liuxianweimarx@163.com","1311855250@qq.com"};

    @Scheduled(cron="0 0 18 * * ?")
    public void pushMessage(){
        String content= stockIdTypeProperties.getStockIdTypeList().stream().map(stockIdType -> {
            Map map=crawlingService.getLiXingerInfo(stockIdType);
            Date date= (Date) map.get("date");
            String templature= (String) map.get("temperature");
            return stockIdType.getName()+"在时间为<font color='blue' size='5px'>"+date+"</font>的温度为<font color='red' size='6px'>"+templature+"</font>℃";
        }).collect(Collectors.joining("\n<br/>"));
        log.info("发送消息内容："+content);
        Email email =new Email();
        email.setReceivers(receviers);
        email.setSubject("每日温度定时推送");
        email.setContent(content);
        boolean success=sendMailService.sendHtmlMail(email);
        log.info("发送"+(success?"成功":"失败"));
    }
}
