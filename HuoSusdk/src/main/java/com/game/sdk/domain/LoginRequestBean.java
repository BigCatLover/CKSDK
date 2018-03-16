package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/11.
 */

public class LoginRequestBean extends BaseRequestBean{
    private String username;//是	STRING	用户名，注册用户名
    private String password;//是	STRING	密码，注册密码

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

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
}
