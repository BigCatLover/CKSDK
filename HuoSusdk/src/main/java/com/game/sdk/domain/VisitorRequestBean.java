package com.game.sdk.domain;

/**
 * Created by zhanglei on 2017/6/8.
 */

public class VisitorRequestBean extends BaseRequestBean{
    private int type;//1 手机号注册；  2 用户名注册 3 试玩
    private String introducer;//	否	STRING	介绍人 2017-03-11 吉米项目添加

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    private String password;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIntroducer() {
        return introducer;
    }

    public void setIntroducer(String introducer) {
        this.introducer = introducer;
    }
}
