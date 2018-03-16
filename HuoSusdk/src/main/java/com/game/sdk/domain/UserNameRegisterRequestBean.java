package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/12.
 */

public class UserNameRegisterRequestBean extends BaseRequestBean{
    public static final int TYPE_UPGRADE = 1;
    public static final int TYPE_REGIST = 0;
    private String username;//	是	STRING	用户名，注册用户名
    private String password;//	是	STRING	密码，注册密码
    private int type;//1 手机号注册；  2 用户名注册 3 试玩
    private String introducer;//	否	STRING	介绍人 2017-03-11 吉米项目添加

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    private int update;
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIntroducer() {
        return introducer;
    }

    public void setIntroducer(String introducer) {
        this.introducer = introducer;
    }
}
