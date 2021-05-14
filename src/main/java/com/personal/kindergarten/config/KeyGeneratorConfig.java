package com.personal.kindergarten.config;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 刘仙伟
 */
@Configuration
@EnableCaching
public class KeyGeneratorConfig{
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(new Date());
                //16点一过则将换Key,重新缓存
                if(calendar.get(Calendar.HOUR_OF_DAY)>=16){
                    calendar.add(Calendar.DAY_OF_MONTH,1);
                }
                StringBuffer key=new StringBuffer(DateFormatUtils.format(calendar.getTime(),"yyyyMMdd"));
                if(Array.getLength(params)==0){
                    key.append(params[0]);
                }else {
                    key.append(Arrays.toString(params));
                }
                return key.toString();
            }
        };
    }
}
