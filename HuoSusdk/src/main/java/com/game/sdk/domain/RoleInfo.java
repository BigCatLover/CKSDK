package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/10/29.
 */

public class RoleInfo {
    private Integer role_type=1;//INT	是	数据类型，1为进入游戏，2为创建角色，3为角色升级，4为退出,5其他
    private String server_id;//STRING	是　	游戏服务器id，默认为0。
    private String server_name;//STRING　	是	所在服务器名称
    private String role_id;//STRING	是　	角色id
    private String role_name;//STRING　	是	角色名称。
    private String party_name;//STRING　	是	工会、帮派名称，如果没有，请填空字符串””。
    private Integer role_level;//STRING　	是	角色等级，如果没有，请填0。
    private Integer role_vip;//STRING　	是	vip等级，如果没有，请填0。
    private Float role_balence=0f;//FLOAT(10,2)	是	用户游戏币余额，如果没有账户余额，请填0。
    private String rolelevel_ctime;//string	是	创建角色的时间 时间戳
    private String rolelevel_mtime;//string	是	角色等级变化时间 时间戳

    public Integer getRole_type() {
        return role_type;
    }

    public void setRole_type(Integer role_type) {
        this.role_type = role_type;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getParty_name() {
        return party_name;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
    }

    public Integer getRole_level() {
        return role_level;
    }

    public void setRole_level(Integer role_level) {
        this.role_level = role_level;
    }

    public Integer getRole_vip() {
        return role_vip;
    }

    public void setRole_vip(Integer role_vip) {
        this.role_vip = role_vip;
    }

    public Float getRole_balence() {
        return role_balence;
    }

    public void setRole_balence(Float role_balence) {
        this.role_balence = role_balence;
    }

    public String getRolelevel_ctime() {
        return rolelevel_ctime;
    }

    public void setRolelevel_ctime(String rolelevel_ctime) {
        this.rolelevel_ctime = rolelevel_ctime;
    }

    public String getRolelevel_mtime() {
        return rolelevel_mtime;
    }

    public void setRolelevel_mtime(String rolelevel_mtime) {
        this.rolelevel_mtime = rolelevel_mtime;
    }
}
