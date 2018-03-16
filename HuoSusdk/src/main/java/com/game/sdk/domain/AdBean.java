package com.game.sdk.domain;

/**
 * Created by zhanglei on 2017/7/5.
 */

public class AdBean {
    private String name;
    private String img;
    private String url;
    private String start_time;
    private String end_time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    private String lab;
}
