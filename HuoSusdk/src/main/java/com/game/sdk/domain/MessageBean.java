package com.game.sdk.domain;

/**
 * Created by zhanglei on 2017/7/25.
 */

public class MessageBean {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private String title;
    private String content;
    private int status;
}
