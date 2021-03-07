package com.personal.kindergarten.config;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;
import com.personal.kindergarten.module.StockIdType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

//@ConfigurationProperties(prefix="lixinger")
@Data
@Component
@NacosConfigurationProperties(prefix="lixinger",dataId = "lixinger",type= ConfigType.YAML,autoRefreshed = true)
public class StockIdTypeProperties {
    private List<StockIdType> stockIdTypeList;
}
