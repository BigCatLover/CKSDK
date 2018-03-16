package com.game.sdk.domain;

/**
 * Created by zhanglei on 2017/7/24.
 */

public class GameBean {
    private String name;
    private String app_id;
    private String client_id;
    private String client_key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_key() {
        return client_key;
    }

    public void setClient_key(String client_key) {
        this.client_key = client_key;
    }
}
