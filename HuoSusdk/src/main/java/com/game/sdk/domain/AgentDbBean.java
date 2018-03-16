package com.game.sdk.domain;


import android.content.ContentValues;

/**
 * Created by liu hong liang on 2016/9/29.
 * 存储数据库agent信息的bean
 */

public class AgentDbBean {

    public static final String PACKAGE_NAME="packageName";
    public static final String INSTALL_CODE="installCode";
    public static final String VERSION_CODE="versionCode";
    public static final String AGENT="agent";
    private String packageName;
    private String installCode;
    private String agent;

    public AgentDbBean(String packageName, String installCode, String agent) {
        this.packageName = packageName;
        this.installCode = installCode;
        this.agent = agent;
    }

    public AgentDbBean() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getInstallCode() {
        return installCode;
    }

    public void setInstallCode(String installCode) {
        this.installCode = installCode;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }



    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(PACKAGE_NAME, packageName);
        cv.put(INSTALL_CODE, installCode);
        cv.put(AGENT, agent);
        return cv;
    }

    @Override
    public String toString() {
        return "AgentDbBean{" +
                "packageName='" + packageName + '\'' +
                ", installCode=" + installCode +
                ", agent='" + agent + '\'' +
                '}';
    }
}
