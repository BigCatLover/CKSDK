package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/12.
 */

public class RegisterOneResultBean {
   private String username;//STRING	注册获得的用户名
   private String password;//STRING	一键注册生成的密码 用户填密码就不用此参数 private加密
   private String agentgame;//STRING	渠道游戏编号

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

    public String getAgentgame() {
        return agentgame;
    }

    public void setAgentgame(String agentgame) {
        this.agentgame = agentgame;
    }
}
