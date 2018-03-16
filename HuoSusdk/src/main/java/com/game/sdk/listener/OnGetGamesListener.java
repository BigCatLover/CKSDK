package com.game.sdk.listener;

import com.game.sdk.domain.GameBean;
import com.game.sdk.domain.NotProguard;

import java.util.List;

/**
 * Created by zhanglei on 2017/8/14.
 */
@NotProguard
public interface OnGetGamesListener {
    void getFinish(List<GameBean> gamelist);
    void getError(String code,String msg);
}
