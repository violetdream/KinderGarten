package com.personal.kindergarten;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.personal.kindergarten.config.StockIdTypeProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Arrays;

@EnableScheduling
@EnableAsync
@SpringBootApplication
@MapperScan(basePackages = "com.personal.kindergarten.dao")
@EnableCaching
//用@ConfigurationProperties注解时需要，用@NacosConfigurationProperties注解时不需要，不然找不到会报错
//@EnableConfigurationProperties({StockIdTypeProperties.class})
//@NacosPropertySources({
//        @NacosPropertySource(dataId = "wechat",autoRefreshed = true),
//        @NacosPropertySource(dataId = "lixinger",autoRefreshed = true),
//        @NacosPropertySource(dataId = "common",autoRefreshed = true)
//        }
//        )
public class PushMessageBootStrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(PushMessageBootStrapApplication.class, args);
        try {
            String url=URLDecoder.decode("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx59b12b935cde7511&redirect_uri=https%3A%2F%2Flite3.sunlands.com%2Ffund-index%2F%3Fstate%3D&response_type=code&scope=snsapi_base&connect_redirect=1#wechat_redirect","UTF-8");
            System.out.println("url = " + url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
