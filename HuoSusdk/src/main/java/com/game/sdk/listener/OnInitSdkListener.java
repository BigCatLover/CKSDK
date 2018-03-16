package com.game.sdk.listener;

import com.game.sdk.domain.NotProguard;

/**
 * Created by liu hong liang on 2016/12/7.
 */
@NotProguard
public interface OnInitSdkListener {
    void initSuccess(String code,String msg);
    void initError(String code,String msg);
}
