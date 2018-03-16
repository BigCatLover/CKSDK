package com.game.sdk.floatwindow;

import android.content.Context;
import android.util.Log;

import com.game.sdk.SdkConstant;
import com.game.sdk.domain.WebRequestBean;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.ui.FloatWebActivity;
import com.game.sdk.util.GsonUtil;

/**
 * Created by liu hong liang on 2017/5/10.
 * 浮点view管理器
 */

public class FloatViewManager {
    private static FloatViewManager instance = null;
    private  Context mContext;
    private IFloatView iFloatView;
    private FloatViewManager(Context context) {
        this.mContext = context.getApplicationContext();
        if("0".equals(SdkConstant.SHOW_INDENTIFY)){//不需要实名认证
            iFloatView=FloatViewImpl.getInstance(mContext);
        }else{
            iFloatView=IdentifyFloatViewImpl.getInstance(context);
        }
    }

    /**
     * @param context
     * @return
     */
    public synchronized static FloatViewManager getInstance(Context context) {
        if (instance == null) {
            instance = new FloatViewManager(context);
        }
        return instance;
    }
    // 移除悬浮窗口
    public void removeFloat() {
        try {
            iFloatView.removeFloat();
            instance = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 显示悬浮窗口
    public void showFloat() {
        try {
            iFloatView.showFloat();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    // 移除悬浮窗口
    public void hideFloat() {
        try {
            iFloatView.hideFloat();
            instance = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 打开网页
     */
    public void openUrl(String url,String title){
        hideFloat();
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(new WebRequestBean()));
        FloatWebActivity.start(mContext, url, title, httpParamsBuild.getHttpParams().getUrlParams().toString(),
                httpParamsBuild.getAuthkey(),0);
    }
    /**
     * 打开用户中心
     */
    public void openucenter() {
        hideFloat();
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(new WebRequestBean()));
        FloatWebActivity.start(mContext, SdkApi.getWebUser(), "用户中心", httpParamsBuild.getHttpParams().getUrlParams().toString(),
                httpParamsBuild.getAuthkey(),0);
    }
}
