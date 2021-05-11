package com.personal.kindergarten.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.personal.kindergarten.dao.KdWechat;
import com.personal.kindergarten.dao.KdWechatExample;
import com.personal.kindergarten.dao.KdWechatMapper;
import com.personal.kindergarten.service.CrawlingService;
import com.personal.kindergarten.service.PushWechatMessageService;
import com.personal.kindergarten.service.WechatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 爬取网站数据
 * @author lxw
 * @date 2021-01-17
 *
 */
@Slf4j
@Service
public class PushWechatMessageServiceImpl implements PushWechatMessageService {

//    @Value("${wechat.GetAllUser_URL}")
    @NacosValue(value = "${wechat.GetAllUser_URL}", autoRefreshed = true)
    private String GetAllUser_URL;//获取所有关注用户列表
    @NacosValue(value = "${wechat.testAppId}", autoRefreshed = true)
    private String testAppId;
    @NacosValue(value = "${wechat.testAppSecret}", autoRefreshed = true)
    private String testAppSecret;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WechatMessageService wechatMessageService;
    @Autowired
    private CrawlingService crawlingService;
    @Autowired
    private KdWechatMapper kdWechatMapper;

    @Override
    @Scheduled(cron="0 0 8,14 * * ?")
    public Map pushAllMessage() {
        //WechatMessageService.GetToken()
        KdWechatExample example=new KdWechatExample();
        example.createCriteria().andAppIdIsNotNull();
        List<KdWechat> kdWechatList=kdWechatMapper.selectByExample(example);
        String access_token=kdWechatList.get(0).getAccessToken();
        if(StringUtils.isEmpty(access_token)){
            log.warn("access_token is null "+kdWechatList);
            access_token=wechatMessageService.GetToken(testAppId,testAppSecret);
        }
        JSONObject responseJSON=restTemplate.getForObject(String.format(GetAllUser_URL,access_token,""), JSONObject.class);
        log.info("所有关注用户列表: "+responseJSON);


        return null;
    }

    @Override
    public boolean pushMessage(String openId) {
        return false;
    }
}
