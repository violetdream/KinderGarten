package com.personal.kindergarten.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.personal.kindergarten.bean.Email;
import com.personal.kindergarten.config.StockIdTypeProperties;
import com.personal.kindergarten.module.StockIdType;
import com.personal.kindergarten.service.CrawlingService;
import com.personal.kindergarten.service.impl.SendMailServiceImpl;
import com.wechat.util.AesException;
import com.wechat.util.SHA1;
import com.wechat.util.XMLParse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Slf4j
@RestController
@RequestMapping("/wechat")
public class PushMessageController {
//    @Value("${wechat.AppSecret}")
    @NacosValue(value = "${wechat.AppSecret}", autoRefreshed = true)
    private String token;
    @Autowired
    private CrawlingService crawlingService;

    @NacosValue(value = "${spring.useLocalCache:0}", autoRefreshed = true)
    private int useLocalCache;

    @Autowired
    private StockIdTypeProperties stockIdTypeProperties;

    @Autowired
    private SendMailServiceImpl sendMailService;

    private Executor executor=new ThreadPoolExecutor(10,20,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(50), Executors.defaultThreadFactory());

    @RequestMapping(value = "/get", method = GET)
    @ResponseBody
    public int get() {
        return useLocalCache;
    }

    @RequestMapping(value = "/getStockIdType", method = GET)
    @ResponseBody
    public List<StockIdType> getStockIdType() {
        return stockIdTypeProperties.getStockIdTypeList();
    }

    @PostMapping(value = "/api")
    public String apiPostMessage(@RequestBody String xmlString){
        log.info("接收微信服务器的请求报文体POST："+xmlString);
        Element root;
        try {
            root=XMLParse.extractTOXMLElement(xmlString);
        } catch (AesException e) {
            log.error("请求XML报文有误",e);
            return xmlString;
        }
        String MsgType=root.getElementsByTagName("MsgType").item(0).getTextContent();
        String xmlContent;
        switch (MsgType){
            case "text":
                //文本消息处理
                xmlContent=processTextMessage(root);
                break;
            case "event":
                //关注/取消关注事件
                xmlContent=processEventMessage(root);
                break;
            default:
                xmlContent=processDefaultMessage(root);
                break;
        }
        log.info("返回的XML消息内容：："+xmlContent);
        return xmlContent;
    }

    @GetMapping(value="/api")
    public Object apiGetMessage(String signature,String timestamp,String nonce,String echostr){
        log.error("接收微信服务器的请求报文体GET：signature={},timestamp={},nonce={},echostr={}",signature,timestamp,nonce,echostr);
        String localSignature= "";
        try {
            localSignature = SHA1.getSHA1(token,timestamp,nonce,"");
        } catch (AesException e) {
            log.error("SHA1加密失败",e);
        }
        if(signature.equals(localSignature)){
            log.info("验证微信服务器发送请求成功");
            return echostr;
        }else{
            log.error("非微信服务器发送请求");
            throw new RuntimeException("非微信服务器发送语求");
        }
    }

    private String processTextMessage(Element root){
        String ToUserName=root.getElementsByTagName("ToUserName").item(0).getTextContent();
        String FromUserName=root.getElementsByTagName("FromUserName").item(0).getTextContent();
        String CreateTime=root.getElementsByTagName("CreateTime").item(0).getTextContent();
        String Content=root.getElementsByTagName("Content").item(0).getTextContent();
        String MsgId=root.getElementsByTagName("MsgId").item(0).getTextContent();

        List list =new ArrayList();
        /*
        String content= (String) Arrays.stream(StockIdEnumType.values()).map(stockIdEnumType -> {
            Map map=crawlingService.getLiXingerInfo(stockIdEnumType);
            Date date= (Date) map.get("date");
            String templature= (String) map.get("temperature");
            StockIdEnumType stockIdType= (StockIdEnumType) map.get("stockIdType");
            return stockIdType.getName()+"在时间为"+date+"的温度为"+templature+"℃";
        }).collect(Collectors.joining("\n"));
        */
        String content= stockIdTypeProperties.getStockIdTypeList().stream().map(stockIdType -> {
            Map map=crawlingService.getLiXingerInfo(stockIdType);
            Date date= (Date) map.get("date");
            String templature= (String) map.get("temperature");
            return stockIdType.getName()+"在时间为"+date+"的温度为"+templature+"℃";
        }).collect(Collectors.joining("\n"));
        log.info("发送消息内容："+content);
        sendEmail("公众号回复消息",content);
        return XMLParse.generateTextMessage(FromUserName,ToUserName,"text",content,MsgId);
    }

    private String processEventMessage(Element root) {
        String ToUserName = root.getElementsByTagName("ToUserName").item(0).getTextContent();
        String FromUserName = root.getElementsByTagName("FromUserName").item(0).getTextContent();
        String CreateTime = root.getElementsByTagName("CreateTime").item(0).getTextContent();
        String Event = root.getElementsByTagName("Event").item(0).getTextContent();
        String content = "";
        if ("subscribe".equals(Event)) {
            content = "Hello,欢迎你关注我，功能正在努力完善中，如果喜欢可发邮件至<a href=\"https://mail.qq.com/\">854406842@qq.com</a>给我留言哦！";
        } else {
            content = "很抱歉，功能正在努力完善中，可发邮件至<a href=\"https://mail.qq.com/\">854406842@qq.com</a>给我留言想要的功能哦！";
        }
        sendEmail("关注/取消事件",root.getTextContent());
        return XMLParse.generateTextMessage(FromUserName, ToUserName, "text", content, "");
    }

    private String processDefaultMessage(Element root) {
        String ToUserName = root.getElementsByTagName("ToUserName").item(0).getTextContent();
        String FromUserName = root.getElementsByTagName("FromUserName").item(0).getTextContent();
        String CreateTime = root.getElementsByTagName("CreateTime").item(0).getTextContent();
        String MsgId="";
        if(root.getElementsByTagName("MsgId")!=null){
            MsgId = root.getElementsByTagName("MsgId").item(0).getTextContent();
        }
        sendEmail("默认回复消息",root.getTextContent());
        String content ="Hello,欢迎你关注我，功能正在努力完善中，如果喜欢可发邮件至<a href=\"https://mail.qq.com/\">854406842@qq.com</a>给我留言哦！";
        return XMLParse.generateTextMessage(FromUserName, ToUserName, "text", content, MsgId);
    }

    private void sendEmail(String subject,String message){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    Email email =new Email();
                    String[] receviers=new String[]{"liuxianweimarx@163.com"};
                    email.setReceivers(receviers);
                    email.setSubject(subject);
                    email.setContent(message);
                    boolean success=sendMailService.sendHtmlMail(email);
                    log.info("发送邮件"+(success?"成功":"失败"));
                }catch (Exception e){
                    log.error("发送邮件失败",e);
                }
            }
        });
    }
}
