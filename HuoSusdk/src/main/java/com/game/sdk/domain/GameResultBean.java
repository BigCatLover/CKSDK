package com.game.sdk.domain;

import java.util.List;

/**
 * Created by zhanglei on 2017/7/24.
 */

public class GameResultBean {
    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<GameBean> getData() {
        return data;
    }

    public void setData(List<GameBean> data) {
        this.data = data;
    }

    private List<GameBean> data;


}
