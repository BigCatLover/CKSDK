package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/1.
 */

public class WebLoadAssert {
    private String mimeType;
    private String name;

    public WebLoadAssert(String mimeType, String name) {
        this.mimeType = mimeType;
        this.name = name;
    }

    public WebLoadAssert() {
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
