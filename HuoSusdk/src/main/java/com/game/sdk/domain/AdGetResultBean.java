package com.game.sdk.domain;

import java.util.List;

/**
 * Created by zhanglei on 2017/7/5.
 */

public class AdGetResultBean {
    private String count;
    private List<AdBean> adv_list;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<AdBean> getAdv_list() {
        return adv_list;
    }

    public void setAdv_list(List<AdBean> adv_list) {
        this.adv_list = adv_list;
    }
}
