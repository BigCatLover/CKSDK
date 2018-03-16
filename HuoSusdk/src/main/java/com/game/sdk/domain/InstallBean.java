package com.game.sdk.domain;

import com.game.sdk.SdkConstant;

/**
 * Created by liu hong liang on 2016/11/9.
 */

public class InstallBean extends BaseRequestBean {
    private String channel_url;//是	STRING	客户服务器URL OR IP
    private String channel_id= SdkConstant.PROJECT_CODE;//是	INT	客户编号
    private String client_key= SdkConstant.HS_CLIENTKEY;//是	STRING	客户端KEY

    public InstallBean() {
        setUser_token(null);
    }

    public String getChannel_url() {
        return channel_url;
    }

    public void setChannel_url(String channel_url) {
        this.channel_url = channel_url;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getClient_key() {
        return client_key;
    }

    public void setClient_key(String client_key) {
        this.client_key = client_key;
    }
}
