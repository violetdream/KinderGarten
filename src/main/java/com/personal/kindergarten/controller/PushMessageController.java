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
        log.info("???????????????????????????????????????POST???"+xmlString);
        Element root;
        try {
            root=XMLParse.extractTOXMLElement(xmlString);
        } catch (AesException e) {
            log.error("??????XML????????????",e);
            return xmlString;
        }
        String MsgType=root.getElementsByTagName("MsgType").item(0).getTextContent();
        String xmlContent;
        switch (MsgType){
            case "text":
                //??????????????????
                xmlContent=processTextMessage(root);
                break;
            case "event":
                //??????/??????????????????
                xmlContent=processEventMessage(root);
                sendEmail("??????/????????????",xmlString);
                break;
            default:
                xmlContent=processDefaultMessage(root);
                sendEmail("??????????????????",xmlString);
                break;
        }
        log.info("?????????XML??????????????????"+xmlContent);
        return xmlContent;
    }

    @GetMapping(value="/api")
    public Object apiGetMessage(String signature,String timestamp,String nonce,String echostr){
        log.error("???????????????????????????????????????GET???signature={},timestamp={},nonce={},echostr={}",signature,timestamp,nonce,echostr);
        String localSignature= "";
        try {
            localSignature = SHA1.getSHA1(token,timestamp,nonce,"");
        } catch (AesException e) {
            log.error("SHA1????????????",e);
        }
        if(signature.equals(localSignature)){
            log.info("???????????????????????????????????????");
            return echostr;
        }else{
            log.error("??????????????????????????????");
            throw new RuntimeException("??????????????????????????????");
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
            return stockIdType.getName()+"????????????"+date+"????????????"+templature+"???";
        }).collect(Collectors.joining("\n"));
        */
        String content= stockIdTypeProperties.getStockIdTypeList().stream().map(stockIdType -> {
            Map map=crawlingService.getLiXingerInfo(stockIdType);
            Date date= (Date) map.get("date");
            String templature= (String) map.get("temperature");
            return stockIdType.getName()+"????????????"+date+"????????????"+templature+"???";
        }).collect(Collectors.joining("\n"));
        log.info("?????????????????????"+content);
        sendEmail("?????????????????????",content);
        return XMLParse.generateTextMessage(FromUserName,ToUserName,"text",content,MsgId);
    }

    private String processEventMessage(Element root) {
        String ToUserName = root.getElementsByTagName("ToUserName").item(0).getTextContent();
        String FromUserName = root.getElementsByTagName("FromUserName").item(0).getTextContent();
        String CreateTime = root.getElementsByTagName("CreateTime").item(0).getTextContent();
        String Event = root.getElementsByTagName("Event").item(0).getTextContent();
        String content = "";
        if ("subscribe".equals(Event)) {
            content = "Hello,??????????????????????????????????????????????????????????????????????????????<a href=\"https://mail.qq.com/\">854406842@qq.com</a>??????????????????";
        } else {
            content = "?????????????????????????????????????????????????????????<a href=\"https://mail.qq.com/\">854406842@qq.com</a>?????????????????????????????????";
        }
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
        String content ="Hello,??????????????????????????????????????????????????????????????????????????????<a href=\"https://mail.qq.com/\">854406842@qq.com</a>??????????????????";
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
                    log.info("????????????"+(success?"??????":"??????"));
                }catch (Exception e){
                    log.error("??????????????????",e);
                }
            }
        });
    }
}
