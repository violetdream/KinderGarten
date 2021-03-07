package com.personal.kindergarten.service;


import com.personal.kindergarten.module.StockIdType;
import java.util.Map;

/**
 * @author  lxw
 * @Date 2021-01-16
 * 从理杏仁获取相关数据，并每天两次推送到我的公众号
 */
public interface CrawlingService {

    /**
     * 获取理杏仁上的投资信息
     * @return
     */
    public Map getLiXingerInfo(StockIdType stockIdType);

}
