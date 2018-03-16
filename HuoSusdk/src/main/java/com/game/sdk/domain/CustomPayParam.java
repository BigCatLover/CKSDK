package com.game.sdk.domain;

import java.io.Serializable;

/**
 * Created by liu hong liang on 2016/10/29.
 */

public class CustomPayParam implements Serializable{

    private int type;
    private String cp_order_id;       //是	STRING	游戏传入的外部订单号。服务器会根据这个订单号生成对应的平台订单号，请保证每笔订单传入的订单号的唯一性。
    private Float product_price;       //是	FLOAT(10,2)	商品价格，以元为单位;建议传入整数,可以保留两位小数
    private Integer product_count=1;       //是	INT	产品数量,默认为1
    private String product_id;       //是	STRING	商品id。
    private String product_name;       //是	STRING　	商品名称。
    private String product_desc="";       //否	STRING　	商品描述
    private Integer exchange_rate=1;       //否	INT	游戏币与人民币兑换比率,例如100为1元人民币兑换100游戏币。默认为1
    private String currency_name="金币";       //否	STRING　	游戏货币名称，默认为金币
    private String ext="";       //是	STRING	cp扩展参数，透传给cp

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    private transient RoleInfo roleinfo;

    public RoleInfo getRoleinfo() {
        return roleinfo;
    }

    public void setRoleinfo(RoleInfo roleinfo) {
        this.roleinfo = roleinfo;
    }

    public String getCp_order_id() {
        return cp_order_id;
    }

    public void setCp_order_id(String cp_order_id) {
        this.cp_order_id = cp_order_id;
    }

    public Float getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Float product_price) {
        this.product_price = product_price;
    }

    public Integer getProduct_count() {
        return product_count;
    }

    public void setProduct_count(Integer product_count) {
        this.product_count = product_count;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public Integer getExchange_rate() {
        return exchange_rate;
    }

    public void setExchange_rate(Integer exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
