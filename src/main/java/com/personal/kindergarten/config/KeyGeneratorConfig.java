package com.personal.kindergarten.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.Arrays;

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
                StringBuffer key=new StringBuffer(String.valueOf(new Date(System.currentTimeMillis())));
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
