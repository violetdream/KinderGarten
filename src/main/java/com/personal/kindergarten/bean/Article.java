package com.personal.kindergarten.bean;

import lombok.Data;

@Data
public class Article {
    private String title;
    private String thumb_media_id;//图文消息的封面图片素材id（必须是永久mediaID）
    private String author;//作者
    private String digest;//图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空。如果本字段为没有填写，则默认抓取正文前64个字。
    private String show_cover_pic;//是否显示封面，0为false，即不显示，1为true，即显示
    private String content;//图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。
    private String content_source_url;//图文消息的原文地址，即点击“阅读原文”后的URL
    private String need_open_comment;//Uint32 是否打开评论，0不打开，1打开
    private String only_fans_can_comment;//Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
}
