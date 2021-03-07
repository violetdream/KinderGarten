package com.personal.kindergarten.module;

import org.springframework.stereotype.Component;


public enum StockIdEnumType {

    ZZ_500("中证500","1000000000905"),
    KC_50("科创50","1000000000688"),
    SZ_50("上证50","1000000000016"),
    HS_300("沪深300","1000000000300"),
    CY("创业板指数","1000000399006"),
    HL("红利指数","1000000000015"),
    ZGLT("军工龙头指数","1000000931066");

    private String stockId;
    private String name;

    StockIdEnumType(String name,String stockId){
        this.name=name;
        this.stockId=stockId;
    }

    @Override
    public String toString() {
        return "[" + stockId + "-" + name + "]";
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
