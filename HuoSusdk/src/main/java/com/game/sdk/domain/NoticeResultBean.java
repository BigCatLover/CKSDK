package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/14.
 */

public class NoticeResultBean {
    private String title;//	STRING	登出公告
    private String url;//	STRING	登出公告跳转URL
    private String content;//	STRING	登出公告内容

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
