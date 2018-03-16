package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2017/5/23.
 */

public class ThirdLoginRequestBean  extends BaseRequestBean{
   private String openid;//	是	STRING	第三方openid
   private String access_token;//	是	STRING	第三方登陆token
   private String nickname;//	否	STRING	昵称
   private String head_img;//	否	STRING	头像URL
   private String expires_date;//	是	INT	过期时间 不过期为0
   private String userfrom;//	是	INT	1试玩 2 qq 3 微信 4微博
   private String introducer;//	否	STRING	介绍人 2017-03-11 吉米项目添加

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getExpires_date() {
        return expires_date;
    }

    public void setExpires_date(String expires_date) {
        this.expires_date = expires_date;
    }

    public String getUserfrom() {
        return userfrom;
    }

    public void setUserfrom(String userfrom) {
        this.userfrom = userfrom;
    }

    public String getIntroducer() {
        return introducer;
    }

    public void setIntroducer(String introducer) {
        this.introducer = introducer;
    }
}
