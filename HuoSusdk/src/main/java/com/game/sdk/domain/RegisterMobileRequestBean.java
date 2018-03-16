package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/12.
 */

public class RegisterMobileRequestBean extends BaseRequestBean {
    public static final int TYPE_UPGRADE = 1;
    public static final int TYPE_REGIST = 0;
    private String mobile;//	是	STRING	玩家注册手机号
    private String password;//	是	STRING	密码，注册密码
    private String smstype;//	是	STRING	短信类型 1 注册 2 登陆 3 修改密码 4 信息变更
    private String smscode;//	是	STRING	短信校验码
    private String introducer;//	否	STRING	介绍人 2017-03-11 吉米项目添加
    private int type; //1 手机注册 2 账号注册 3 游客

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    private int update;//1 升级 0 普通注册

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmstype() {
        return smstype;
    }

    public void setSmstype(String smstype) {
        this.smstype = smstype;
    }

    public String getSmscode() {
        return smscode;
    }

    public void setSmscode(String smscode) {
        this.smscode = smscode;
    }

    public String getIntroducer() {
        return introducer;
    }

    public void setIntroducer(String introducer) {
        this.introducer = introducer;
    }
}
