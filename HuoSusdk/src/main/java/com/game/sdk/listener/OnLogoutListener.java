package com.game.sdk.listener;

import com.game.sdk.domain.NotProguard;

/**
 * Created by liu hong liang on 2016/12/7.
 */
@NotProguard
public interface OnLogoutListener {
    int TYPE_NORMAL_LOGOUT=1;//正常退出账号
    int TYPE_SWITCH_ACCOUNT=2;//切换账号
    int TYPE_TOKEN_INVALID=3;//登陆过期
    void logoutSuccess(int type,String code,String msg);
    void logoutError(int type,String code,String msg);
}
