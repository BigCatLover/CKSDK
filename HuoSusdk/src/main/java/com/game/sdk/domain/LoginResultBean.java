package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/11.
 */

public class LoginResultBean {
    private String mem_id;	//STRING	用户在平台的用户ID
    private String cp_user_token;//	STRING	CP用user_token
    private String agentgame;//	STRING	渠道游戏编号

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    private String score;

    public String getAgentgame() {
        return agentgame;
    }

    public void setAgentgame(String agentgame) {
        this.agentgame = agentgame;
    }

    public String getCp_user_token() {
        return cp_user_token;
    }

    public void setCp_user_token(String cp_user_token) {
        this.cp_user_token = cp_user_token;
    }

    public String getMem_id() {
        return mem_id;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }
}
