package com.personal.kindergarten.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.personal.kindergarten.bean.Article;
import com.personal.kindergarten.dao.KdWechat;
import com.personal.kindergarten.dao.KdWechatExample;
import com.personal.kindergarten.dao.KdWechatMapper;
import com.personal.kindergarten.service.WechatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@EnableScheduling
@EnableAsync
@Service
public class WechatMessageServiceImpl implements WechatMessageService, CommandLineRunner {

//    @Value("${wechat.GetToken_URL}")
    @NacosValue(value = "${wechat.GetToken_URL}", autoRefreshed = true)
    private String getTokenURL;
//    @Value("${wechat.AppId}")
    @NacosValue(value = "${wechat.AppId}", autoRefreshed = true)
    private String appId;
    @NacosValue(value = "${wechat.testAppId}", autoRefreshed = true)
    private String testAppId;
//    @Value("${wechat.AppSecret}")
    @NacosValue(value = "${wechat.AppSecret}", autoRefreshed = true)
    private String appSecret;
    @NacosValue(value = "${wechat.testAppSecret}", autoRefreshed = true)
    private String testAppSecret;
//    @Value("${wechat.UploadImg_URL}")
    @NacosValue(value = "${wechat.UploadImg_URL}", autoRefreshed = true)
    private String uploadImgURL;
//    @Value("${wechat.AddNews_URL}")
    @NacosValue(value = "${wechat.AddNews_URL}", autoRefreshed = true)
    private String addNesURL;
//    @Value("${wechat.AddMaterial_URL}")
    @NacosValue(value = "${wechat.AddMaterial_URL}", autoRefreshed = true)
    private String addMaterialURL;
//    @Value("${wechat.SendAll_URL}")
    @NacosValue(value = "${wechat.SendAll_URL}", autoRefreshed = true)
    private String sendAllURL;
//    @Value("${wechat.SendTextMessage_URL}")
    @NacosValue(value = "${wechat.SendTextMessage_URL}", autoRefreshed = true)
    private String sendTextMessageURL;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KdWechatMapper kdWechatMapper;


    /**
     * GetToken
     * ???6000ms????????????????????????????????????????????????????????????
     */
    @Override
//    @Scheduled(cron="0 0 0/1 * * ?")
//    @Async
    public String GetToken(String appId,String appSecret){
        long startTime=System.currentTimeMillis();
        log.info("start get_token schedulering... , start time is "+new Timestamp(startTime));
        String url=String.format(getTokenURL,appId,appSecret);
        String token="";
        int expires_in;
        try{
            Map returnObject=restTemplate.getForObject(url, Map.class);
            log.info("return Data : "+returnObject);
            if(StringUtils.isEmpty(returnObject.get("errcode"))){
                token=(String)returnObject.get("access_token");
                expires_in= (int) returnObject.get("expires_in");
                log.info("get Token Success ! ");
                KdWechatExample kdWechatExample=new KdWechatExample();
                kdWechatExample.createCriteria().andAppIdEqualTo(appId);
                List<KdWechat> kdWechatList=kdWechatMapper.selectByExample(kdWechatExample);
                if(kdWechatList.isEmpty()){
                    log.error("??????????????????????????????");
                    KdWechat kdWechat=new KdWechat();
                    kdWechat.setAppId(appId);
                    kdWechat.setAppSecret(appSecret);
                    kdWechat.setAccessToken(token);
                    kdWechat.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    kdWechatMapper.insertSelective(kdWechat);
                    return token;
                }
                KdWechat kdWechat= kdWechatMapper.selectByPrimaryKey(kdWechatList.get(0).getId());
                kdWechat.setAccessToken(token);
                kdWechat.setUpdateTime(new Timestamp(System.currentTimeMillis()));
               //??????access_token
                kdWechatMapper.updateByPrimaryKey(kdWechat);
            } else{
                log.error("get Token Fail ! "+returnObject.get("errmsg"));
            }
        }catch (Exception e){
            log.error("get Token Error ",e);
        }
        long endTime=System.currentTimeMillis();
        log.info("end get_token schedulering... , end time is "+new Timestamp(startTime)+" , total time is "+(endTime-startTime)+ " ms ");

        return token;
    }

    /**
     * ????????????????????????????????????
     * @return ??????????????????URL
     * ????????????????????????????????????URL
     * ???????????????????????????????????????????????????????????????????????????100000??????????????????????????????jpg/png????????????????????????1MB??????
     */
    @Override
    public String UploadImg(){
        String imageName="004.jpg";
        //??????token?????????????????????
        String token=GetToken(appId,appSecret);
        String url=String.format(uploadImgURL,token);


        File file = new File("E:\\?????????????????? ??????\\004.jpg");
        //??????header?????????????????????????????????
        HttpHeaders pictureHeader = new HttpHeaders();
        pictureHeader.setContentType(MediaType.IMAGE_JPEG);
        //????????????contentDisposition?????????file??????
        pictureHeader.setContentDispositionFormData("file",file.getName());
        byte[] fileByte = new byte[0];
        try {
            fileByte = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            log.error("read Img Error ",e);
            return null;
        }
        ByteArrayResource bar = new ByteArrayResource(fileByte);
        HttpEntity<ByteArrayResource> picturePart = new HttpEntity<>(bar, pictureHeader);
        MultiValueMap<String,Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("media",picturePart);
        String imgurl = null;
        try {
            String returnJsonString = restTemplate.postForObject(url, requestMap, String.class);
            Map returnMap= JSON.parseObject(returnJsonString,Map.class);
            Object errcode=returnMap.get("errcode");
            if(!StringUtils.isEmpty(errcode)&&!"0".equals(errcode)){
                log.error("Fail , return Data "+returnMap);
                throw new Exception(String.valueOf(returnMap.get("errmsg")));
            }
            imgurl= (String) returnMap.get("url");
            log.info("Success , return Data "+returnMap);
            //Success , return Data {"url":"http://mmbiz.qpic.cn/mmbiz_jpg/6iaFEL0ZmhPLwbJMs0XicAVRcDa20SFjKCJgCIlZRcoThiaL88qdnZqvCodNgArlIIKPVTWqNmsPicBibXp779y5gAA/0"}
        }catch (Exception e){
            log.error("upload Message Fail ",e);
            return null;
        }
        return imgurl;
    }

    /**
     * ????????????????????????
     * @return media_id
     */

    @Override
    public String AddNews(){
        //??????token?????????????????????
        String token=GetToken(appId,appSecret);
        String url=String.format(addNesURL,token);
        Map  returnMap=new HashMap();

        //??????Articles
        List<Article> articles=new ArrayList<Article>();
        Article article1=new Article();
        article1.setTitle("??????????????????");
        article1.setAuthor("LXW");
        article1.setContent("????????????  <br/> <img src=\"http://mmbiz.qpic.cn/mmbiz_jpg/6iaFEL0ZmhPLwbJMs0XicAVRcDa20SFjKCJgCIlZRcoThiaL88qdnZqvCodNgArlIIKPVTWqNmsPicBibXp779y5gAA/0\" />");
        article1.setThumb_media_id("ZwKrtn0CXvaXGm_OFPi-qripCqXkn6S6ogT292MGMVk");
        article1.setShow_cover_pic("0");
        article1.setContent_source_url("wwww.baidu.com");
        articles.add(article1);
        articles.add(article1);
        articles.add(article1);
        articles.add(article1);
        articles.add(article1);
        Map articleMap=new HashMap();
        articleMap.put("articles",articles);
        Object jsonString=JSON.toJSON(articleMap);
        log.info("toJSON Result : "+jsonString);
        try {
            String resultString = restTemplate.postForObject(url, jsonString, String.class);
            returnMap=JSON.parseObject(resultString,Map.class);
            Object errcode=returnMap.get("errcode");
            if(!StringUtils.isEmpty(errcode)&&!"0".equals(errcode)){
                log.error("Fail , return Data "+returnMap);
                throw new Exception(String.valueOf(returnMap.get("errmsg")));
            }
            //???????????????????????????media_id
            // {item=[], media_id=ZwKrtn0CXvaXGm_OFPi-qo_aBTgRBqWsHLGI8dZ_1Wg}
            log.info("Success , return Data "+returnMap);
        }catch (Exception e){
            log.error("add News Fail ",e);
            return null;
        }

        return (String) returnMap.get("media_id");
    }

    /**
     * ??????POST??????????????????????????????id???media??????????????????????????????????????????filename???filelength???content-type??????????????????????????????????????????????????????????????????????????????????????????????????????
     * @return media_id  url
     * ??????????????????????????????POST??????????????????id???description??????????????????????????????
     * curl "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE" -F media=@media.file -F description='{"title":VIDEO_TITLE, "introduction":INTRODUCTION}'
     */

    @Override
    public Map add_material(){
        String title="2014????????????";//?????????????????????
        String introduction="2014?????????????????????";//?????????????????????
        String type="image";//???????????????????????????????????????image???????????????voice???????????????video??????????????????thumb???
        Map  returnMap=new HashMap();

        //??????token?????????????????????
        String token=GetToken(appId,appSecret);
        String url=String.format(addMaterialURL,token,type);

        //E:\???????????????????????????\2014????????????\VID_20140201_220145.mp4
        File file = new File("E:\\?????????????????? ??????\\054.jpg");
        //??????header?????????????????????????????????
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type"," multipart/form-data; boundary=-------------------------"+System.currentTimeMillis());
        header.set("Content-Length",String.valueOf(file.length()));
        header.set("Content-Disposition","form-data; name=\"media\";filename=\""+file.getName()+"; filelength="+file.length());
//        pictureHeader.setContentType(MediaType.parseMediaType(MediaType.MULTIPART_FORM_DATA_VALUE));
        //????????????contentDisposition?????????file??????
//        pictureHeader.setContentDispositionFormData("file",file.getAbsolutePath());

//        byte[] fileByte = new byte[0];
//        try {
//            fileByte = Files.readAllBytes(file.toPath());
//        } catch (IOException e) {
//            log.error("read file Error ",e);
//            return returnMap;
//        }
//        ByteArrayResource bar = new ByteArrayResource(fileByte);

        MultiValueMap<String,Object> form = new LinkedMultiValueMap<>();
        FileSystemResource fileSystemResource=new FileSystemResource(file);
        form.add("media",fileSystemResource);
        form.add("title",title);
        form.add("introduction",introduction);
        HttpEntity<MultiValueMap> httpEntity = new HttpEntity<MultiValueMap>(form, header);
        try {
            String jsonString = restTemplate.postForObject(url, httpEntity, String.class);
            returnMap=JSON.parseObject(jsonString,Map.class);
            Object errcode=returnMap.get("errcode");
            if(!StringUtils.isEmpty(errcode)&&!"0".equals(errcode)){
                log.error("Fail , return Data "+returnMap);
                throw new Exception(String.valueOf(returnMap.get("errmsg")));
            }
            log.info("Success , return Data "+returnMap);
            //Success , return Data {item=[],/ZwKrtn0CXvaXGm_OFPi-qnicBNR1Vi0cEGtGzSfgO8w  media_id=ZwKrtn0CXvaXGm_OFPi-qripCqXkn6S6ogT292MGMVk, url=http://mmbiz.qpic.cn/mmbiz_jpg/6iaFEL0ZmhPJRb6WGQ29t7picld5UgEdJqgr46ic2HTO8DaudhibeNkxHflqcvwyOIVY6WRLzO6icnvZIYPUxOrntRg/0?wx_fmt=jpeg}
        }catch (Exception e){
            log.error("upload Message Fail ",e);
            return null;
        }
        return returnMap;
    }

    /**
     *?????????????????????????????????????????????????????????????????????
     * @return
     *
     */
    @Override
    public Map SendAll() {
        Map returnMap = new HashMap();

        //??????token?????????????????????
        String token = GetToken(testAppId,testAppSecret);
        String url = String.format(sendAllURL, token);

        Map sendMap = new HashMap();
        Map filterDataMap = new HashMap();
        Map mpnewsDataMap = new HashMap();


        filterDataMap.put("is_to_all", true);//????????????????????????????????????????????????true???false?????????true???????????????????????????????????????false?????????tag_id??????????????????????????????
        mpnewsDataMap.put("media_id", "ZwKrtn0CXvaXGm_OFPi-qo_aBTgRBqWsHLGI8dZ_1Wg");
        sendMap.put("msgtype", "mpnews");
        sendMap.put("send_ignore_reprint", "0");//????????????????????????????????????????????????????????? 1??????????????????????????????0?????????????????? ??????????????????0???
        sendMap.put("mpnews", mpnewsDataMap);
        sendMap.put("filter", filterDataMap);
        Object sendJsonString = JSON.toJSON(sendMap);
        log.info("SendAll toJSON Result : " + sendJsonString);
        try {
            String jsonString = restTemplate.postForObject(url, sendJsonString, String.class);
            returnMap = JSON.parseObject(jsonString, Map.class);
            Object errcode = returnMap.get("errcode");
            if (!StringUtils.isEmpty(errcode) && !"0".equals(errcode)) {
                log.error("Fail , return Data " + returnMap);
                throw new Exception(String.valueOf(returnMap.get("errmsg")));
            }
            log.info("Success , return Data " + returnMap);
            //Success , return Data {item=[],/ZwKrtn0CXvaXGm_OFPi-qnicBNR1Vi0cEGtGzSfgO8w  media_id=ZwKrtn0CXvaXGm_OFPi-qripCqXkn6S6ogT292MGMVk, url=http://mmbiz.qpic.cn/mmbiz_jpg/6iaFEL0ZmhPJRb6WGQ29t7picld5UgEdJqgr46ic2HTO8DaudhibeNkxHflqcvwyOIVY6WRLzO6icnvZIYPUxOrntRg/0?wx_fmt=jpeg}
        } catch (Exception e) {
            log.error("send All Fail ", e);
            return null;
        }
        return returnMap;
    }


    public boolean pushTestMessage() {
        String token=GetToken(testAppId,testAppSecret);
        String url=String.format(sendTextMessageURL,token);
        Map sendMap=new HashMap();
        sendMap.put("touser", "oZ8lC6nP1CLf0GgfGtiCxLGqeDZE");//?????????
        sendMap.put("msgtype", "text");
        Map textMap=new HashMap();
        textMap.put("content","??????????????????????????????????????????????????????854406842@qq.com");
        sendMap.put("text",textMap);
        Object sendJsonString = JSON.toJSON(sendMap);
        log.info("SendAll toJSON String : " + sendJsonString);
        String jsonString =  restTemplate.postForObject(url, sendJsonString, String.class);
        Map returnMap = JSON.parseObject(jsonString, Map.class);
        log.info("Success , return Data " + returnMap);
        return true;
    }

    @Override
    public void run(String... args) throws Exception {
        pushTestMessage();
    }
}
