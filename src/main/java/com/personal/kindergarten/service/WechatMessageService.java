package com.personal.kindergarten.service;


import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

public interface WechatMessageService {
    /**
     * GetToken
     * 每6000ms执行一次，不会因上一次任务未执行完而阻塞
     */
    public String GetToken(String appId,String appSecret);

    /**
     * 上传图片素材至微信服务器
     * @return 图片素材访问URL
     * 上传图文消息内的图片获取URL
     * 本接口所上传的图片不占用公众号的素材库中图片数量的100000个的限制。图片仅支持jpg/png格式，大小必须在1MB以下
     */
    public String UploadImg();

    /**
     * 新增永久图文素材
     * @return media_id
     */

    public String AddNews();

    /**
     * 通过POST表单来调用接口，表单id为media，包含需要上传的素材内容，有filename、filelength、content-type等信息。请注意：图片素材将进入公众平台官网素材管理模块中的默认分组。
     * @return media_id  url
     * 在上传视频素材时需要POST另一个表单，id为description，包含素材的描述信息
     * curl "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE" -F media=@media.file -F description='{"title":VIDEO_TITLE, "introduction":INTRODUCTION}'
     */

    public Map add_material();


    /**
     *根据标签进行群发【订阅号与服务号认证后均可用】
     * @return
     *
     */
    public Map SendAll();
}
