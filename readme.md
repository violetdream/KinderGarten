# Nacos安装

您可以在Nacos的[release notes](https://github.com/alibaba/nacos/releases)及[博客](https://nacos.io/zh-cn/blog/index.html)中找到每个版本支持的功能的介绍，当前推荐的稳定版本为1.4.1。

``` shell
wget -i 

https://github.com/alibaba/nacos/releases/download/2.0.0-BETA/nacos-server-2.0.0-BETA.tar.gz

tar -zxvf  nacos-server-2.0.0-BETA.tar.gz
cd nacos/bin
sh startup.sh -m standalone
```



输入http://120.79.28.199:8848/nacos

建立三个dataid，

wechat

``` yaml
wechat:
  GetToken_URL: https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s
  AppId: wx1b40edaafb411fcb
  AppSecret: 88bd7e6ec57d613f2676b2e395c26dac
  UploadImg_URL: https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=%s
  AddNews_URL: https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=%s
  AddMaterial_URL: https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=%s&type=%s
  SendAll_URL: https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s
  GetAllUser_URL: https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s&next_openid=%s
  SendTextMessage_URL: https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s
```



lixinger

``` yaml
lixinger:
  get-price-metrics-chart-info:
    url: https://www.lixinger.com/api/analyt/stock-collection/price-metrics/get-price-metrics-chart-info
    cookie: jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2MDE1MzU0MzdmMmY3YTYxZGY1MjgzZDkiLCJpYXQiOjE2MTIwMDI2MjcsImV4cCI6MTYxMzIxMjIyN30.TahRPH6wJXmpgPDK_2OrYszOZmFj_TvQGxJuraOEths
    reqestJSONString: '{"stockIds": [%s],"granularity": "y10","metricTypes": ["mcw"], "leftMetricNames": ["%s"], "rightMetricNames": ["cp"]}'
```



common

```yaml
spring:
  application:
    name: KindergartenPushMessage
  datasource:
    url: jdbc:mysql://120.79.28.199:3306/kindergarten?serverTimezone=UTC&useUnicode=true&useSSL=false&characterEncoding=UTF-8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
```



