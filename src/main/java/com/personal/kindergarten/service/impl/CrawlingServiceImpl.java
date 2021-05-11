package com.personal.kindergarten.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.personal.kindergarten.module.StockIdType;
import com.personal.kindergarten.service.CrawlingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig
/**
 * 爬取网站数据
 * @author lxw
 * @date 2021-01-17
 *
 */
public class CrawlingServiceImpl implements CrawlingService {

//    @Value("${lixinger.get-price-metrics-chart-info.url}")
    @NacosValue(value = "${lixinger.get-price-metrics-chart-info.url}", autoRefreshed = true)
    private String url;
//    @Value("${lixinger.get-price-metrics-chart-info.cookie}")
    @NacosValue(value = "${lixinger.get-price-metrics-chart-info.cookie}", autoRefreshed = true)
    private String cookie;
//    @Value("${lixinger.get-price-metrics-chart-info.reqestJSONString}")
    @NacosValue(value = "${lixinger.get-price-metrics-chart-info.reqestJSONString}", autoRefreshed = true)
    private String reqestJSONString;

    @Autowired
    private RestTemplate restTemplate;

    /**
     *
     * @param stockIdType 查询的指数ID
     * @return
     */
    @Override
    @Cacheable(value = "LiXingerInfo",keyGenerator = "keyGenerator")
    public Map getLiXingerInfo(StockIdType stockIdType){
        Map resultMap=new HashMap();
        Map PBMap=postEntityByParam(stockIdType,"pb");//获取最近一天PB分位点
        Map PEMap=postEntityByParam(stockIdType,"pe_ttm");//获取最近一天pe_ttm分位点
        BigDecimal PBValue= (BigDecimal) PBMap.get("value");
        BigDecimal PEValue= (BigDecimal) PEMap.get("value");
        DecimalFormat df = new DecimalFormat("0.00%");
        BigDecimal ETFtemperature=(PBValue.add(PEValue)).divide(new BigDecimal(2));
        String ETFtemperaturePercent=df.format(ETFtemperature);
        resultMap.put("temperature",ETFtemperaturePercent.replace("%",""));
        resultMap.put("date",PBMap.get("date"));
        resultMap.put("stockIdType",stockIdType);
        log.info(stockIdType+"指数温度为："+resultMap);
        return resultMap;
    }

    /**
     * 根据参数@param爬取网站数据
     * @param stockIdType 查询的指数ID
     * @param metricType 查询的类型pb pe_ttm
     * @return
     */
    private Map postEntityByParam(StockIdType stockIdType,String metricType){
        Map resultMap=new HashMap();
        HttpHeaders header = new HttpHeaders();
        header.set("cookie",cookie);
        String reqestString=String.format(reqestJSONString,stockIdType.getStockId(),metricType);
        Object requestJSON=JSON.parse(reqestString);
        log.info("请求报文头："+header);
        log.info("请求报文体："+requestJSON);
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(requestJSON, header);
        ResponseEntity<JSONObject> responseEntity=restTemplate.postForEntity(url,httpEntity,JSONObject.class);
        HttpStatus httpStatus = responseEntity.getStatusCode();
        if(HttpStatus.OK.equals(httpStatus)){
            log.info("成功返回："+httpStatus);
            JSONObject responseEntityBodyObject=responseEntity.getBody();
            log.debug("返回报文:"+responseEntityBodyObject);
            List priceMetricsPBList= (ArrayList) responseEntityBodyObject.get("priceMetricsList");
            log.info("返回"+stockIdType+"-"+metricType.toUpperCase()+"列表:"+priceMetricsPBList);
            List lastList= (List) priceMetricsPBList.stream().sorted((o1, o2)->{
                String date1String=(String)((Map)o1).get("date");
                Date date1= resolveUTCDate(date1String);
                String date2String=(String)((Map)o2).get("date");
                Date date2= resolveUTCDate(date2String);
                return date2.compareTo(date1);
            }).map(map->{
                String dateString=(String)((Map)map).get("date");
                Date date= resolveUTCDate(dateString);
                ((Map) map).put("date",date);
                return map;
            }).collect(Collectors.toList());
            Map lastMap= (Map) lastList.get(0);
            Map posMap=(Map)((Map) lastMap).get("pos");
            Map pbMap= (Map) posMap.get(metricType);
            Map mcwMap= (Map) pbMap.get("mcw");
            String cvpos= String.valueOf(mcwMap.get("cvpos"));//当前历史分位点
            BigDecimal lastValue=new BigDecimal(cvpos);
            log.info("返回最近一天的"+stockIdType+"-"+metricType.toUpperCase()+"Map:"+lastMap);
            log.info("返回最近一天的时间："+lastMap.get("date"));
            log.info("返回最近一天的"+stockIdType+"-"+metricType.toUpperCase()+"分位点:"+lastValue);
            resultMap.put("date",lastMap.get("date"));
            resultMap.put("value",lastValue);
        }else{
            log.error("返回失败："+httpStatus);
            log.info("返回失败报文:"+responseEntity.hasBody());
        }
        return resultMap;
    }

    /**
     * 处理UTC格式的DateString
     * * T表示分隔符，Z表示的是UTC。
     * * UTC：世界标准时间，在标准时间上加上8小时，即东八区时间，也就是北京时间。
     * * 2021-01-14T16:00:00.000Z
     * @param dateString
     * @return
     */
    private Date resolveUTCDate(String dateString){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        dateString=dateString.replace("Z"," UTC");
        java.util.Date date=new java.util.Date();
        try {
            date=simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            log.error("时间格式异常："+dateString,e);
        }
        return new Date(date.getTime());
    }
}
