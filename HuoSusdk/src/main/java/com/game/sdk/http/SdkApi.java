package com.game.sdk.http;

import android.text.TextUtils;
import android.util.Log;

import com.game.sdk.SdkConstant;
import com.game.sdk.log.L;
import com.game.sdk.util.MD5;
import com.kymjs.rxvolley.client.HttpParams;

/**
 * Created by liu hong liang on 2016/11/9.
 */

public class SdkApi {
    private static final String TAG = SdkApi.class.getSimpleName();
    // https://env-test-api-game.gank.tv/api/v7/
    //https://env-test-api.yoyock.com/api/v7/
    public static String requestUrl="https://api-game.gank.tv/api/v7/";

    private static String getRequestUrl(){
        if(!TextUtils.isEmpty(requestUrl)){
            return requestUrl;
        }
        if(!TextUtils.isEmpty(SdkConstant.BASE_URL)||!TextUtils.isEmpty(SdkConstant.BASE_IP)){
            requestUrl=SdkConstant.BASE_URL+SdkConstant.BASE_SUFFIX_URL;
//            if("1".equals(SdkConstant.USE_URL_TYPE)){
//                requestUrl=SdkConstant.BASE_URL+SdkConstant.BASE_SUFFIX_URL;
//            }else{
//                requestUrl="http://";
//                requestUrl+=SdkConstant.BASE_IP+"/api/v7/";
//            }
        }
        return requestUrl;
    }
    public static String getStartup() {
//        printUrl("system/startup");
        return getRequestUrl()+"system/startup";
    }

    public static String getAdData() {
//        printUrl("system/startup");gameall/index
        return getRequestUrl()+"Advert/index";
    }

    public static String getGameAll() {
        return getRequestUrl()+"gameall/index";
    }

    public static String getLogin() {
        printUrl("user/login");
        return getRequestUrl()+"user/login";
    }

    /**
     * 第三方登陆
     * @return
     */
    public static String getLoginoauth() {
        printUrl("user/loginoauth");
        return getRequestUrl()+"user/loginoauth";
    }

    public static String getRegisterMobile() {
        printUrl("user/registermobile");
        return getRequestUrl()+"user/registermobile";
    }
    public static String getSmsSend() {
        printUrl("sms/send");
        return getRequestUrl()+"sms/send";
    }
    public static String getRegisterOne() {
        return getRequestUrl()+"user/registerone";
    }
    public static String getRegister() {
        return getRequestUrl()+"user/register";
    }
    public static String getUproleinfo() {
        return getRequestUrl()+"user/uproleinfo";
    }
    public static String getNotice() {
        return getRequestUrl()+"system/notice";
    }
    public static String getLogout() {
        return getRequestUrl()+"user/logout";
    }


    public static String getQueryorder() {
        return getRequestUrl()+"pay/queryorder";
    }
    //下面的都是网页请求
    public static String getWebSdkPay() {
        return getRequestUrl()+"pay/sdkpay";
    }

    public static String getWebUser() {
        return getRequestUrl()+"web/user/index";
    }
    public static String getWebIdentify() {
        return getRequestUrl()+"web/indentify/index";
    }
    public static String getWebBbs() {
        return getRequestUrl()+"web/bbs/index";
    }
    public static String getWebGift() {
        return getRequestUrl()+"web/gift/index";
    }
    public static String getWebHelp() {
        return getRequestUrl()+"web/help/index";
    }
    public static String getWebForgetpwd() {
        return getRequestUrl()+"web/forgetpwd/index";
    }

    private static void printUrl(String path){
        L.e(TAG,"http_url="+getRequestUrl()+path);
    }
    public static HttpParams getCommonHttpParams(String apiName){
        HttpParams httpParams=new HttpParams();
        httpParams.put("app_id", SdkConstant.HS_APPID);
        httpParams.put("client_id", SdkConstant.HS_CLIENTID);
        httpParams.put("from", SdkConstant.FROM);
        long timestamp=System.currentTimeMillis()/1000;
        httpParams.put("timestamp", timestamp+"");
        //MD5(game/gametype+timestamp+client_key)
        httpParams.put("sign", MD5.md5(new StringBuffer(apiName).append(timestamp).append(SdkConstant.HS_CLIENTKEY).toString()));
        httpParams.put("agentgame",SdkConstant.HS_AGENT);
        if(!TextUtils.isEmpty(SdkConstant.userToken)){
            httpParams.put("user_token",SdkConstant.userToken);
        }
        return httpParams;
    }
}
