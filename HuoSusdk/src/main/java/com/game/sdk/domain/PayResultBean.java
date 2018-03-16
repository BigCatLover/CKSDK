package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/15.
 */

public class PayResultBean {
    private String paytype	;//是	STRING	支付类型标识 【支付类型对应标识表】
    private String order_id	;//是	STRING	此次支付 平台订单ID
    private float real_amount;//	是	DOUBLE(10,2）	此次支付实际支付金额(单位: 元) 保留两位有效数字
    private String token	;//是	STRING	对应支付的token
    private String status	;//游戏币必须	INT	paytype为gamepay时,标识游戏币支付状态 1未支付 2 成功 3 失败




    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public float getReal_amount() {
        return real_amount;
    }

    public void setReal_amount(float real_amount) {
        this.real_amount = real_amount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
