package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/12.
 */

public class SdkPayRequestBean extends BaseRequestBean {
    private CustomPayParam orderinfo;

    public String getPay_ver() {
        return pay_ver;
    }

    public void setPay_ver(String pay_ver) {
        this.pay_ver = pay_ver;
    }

    private String pay_ver; //兼容替换支付宝参数前后版本，目前传入v2，下次替换请＋1
    private RoleInfo roleinfo =new RoleInfo();

    public CustomPayParam getOrderinfo() {
        return orderinfo;
    }

    public void setOrderinfo(CustomPayParam orderinfo) {
        this.orderinfo = orderinfo;
    }

    public RoleInfo getRoleinfo() {
        return roleinfo;
    }

    public void setRoleinfo(RoleInfo roleinfo) {
        this.roleinfo = roleinfo;
    }
}
