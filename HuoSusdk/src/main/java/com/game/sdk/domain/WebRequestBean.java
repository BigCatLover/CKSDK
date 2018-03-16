package com.game.sdk.domain;

import com.game.sdk.SdkConstant;

/**
 * Created by liu hong liang on 2016/11/15.
 */

public class WebRequestBean {
    private long timestamp;//	是	STRING	客户端时间戳 timestamp
    private String user_token= SdkConstant.userToken;    //是	STRING	此次连接token

    public WebRequestBean() {
        timestamp=System.currentTimeMillis()+ SdkConstant.SERVER_TIME_INTERVAL;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }
}
