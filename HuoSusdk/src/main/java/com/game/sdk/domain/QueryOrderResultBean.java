package com.game.sdk.domain;

/**
 * Created by liu hong liang on 2016/11/15.
 */

public class QueryOrderResultBean {
    private String order_id	;//STRING	所查询的平台订单号
    private String status	;//STRING	平台状态 1 待支付 2 支付成功 3 支付失败
    private String cpstatus	;//STRING	通知游戏状态 1 待通知 2 通知成功 3 通知失败
    private String score;
    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }


    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCpstatus() {
        return cpstatus;
    }

    public void setCpstatus(String cpstatus) {
        this.cpstatus = cpstatus;
    }
}
