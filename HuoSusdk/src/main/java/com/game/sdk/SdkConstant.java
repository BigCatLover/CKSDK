package com.game.sdk;

import com.game.sdk.domain.DeviceBean;
import com.game.sdk.domain.NotProguard;
import com.game.sdk.domain.ThirdLoginInfo;

import java.util.List;

/**
 * Created by liu hong liang on 2016/11/7.
 * sdk常量
 *
 */
@NotProguard
public class SdkConstant {
    //nativie自动注入常量
//    public static String CK_APPID="";
//    public static String CK_CLIENTID="";//
//    public static String CK_CLIENTKEY="";
//    public static String CK_AGENT="";
    public static String HS_APPID="";
    public static String HS_CLIENTID="";//
    public static String HS_CLIENTKEY="";
    public static String HS_AGENT="";
    public static String HS_APPKEY="";
    public static String FROM="3";//1-WEB、2-WAP、3-Android、4-IOS、5-WP

    //地址信息
    public static String BASE_URL="";//域名
    public static String BASE_SUFFIX_URL="";//后缀
    public static String BASE_IP;
    //渠道编号
    public static String PROJECT_CODE;
    //使用地址方式
    public static String USE_URL_TYPE;//1域名，2ip

    public static String APP_PACKAGENAME;//app包名
    //是否显示邀请码
    public static String SHOW_INVITATION= SdkConfig.SHOW_INVITATION;

    //是否显示实名认证
    public static String SHOW_INDENTIFY= SdkConfig.SHOW_INDENTIFY;

    //rsa密钥
    public static String RSA_PUBLIC_KEY;
//    ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOvTgQeOuMIop6psK0Mk58fHur" +
//            "Sbx4pKye3reS5a6Lax3IrLazLGKQEnd+S+1q5BBVwc+JCJi/AUdbJeDkx+cCfE0M" +
//            "LbNt5DiZeKBN/hV4C+pOm0AjEkWQmJfIzsgfVpcifn1R5KsgZ0FtbfO7MOFAcYox" +
//            "HCYZduX4jhIZbgxrmwIDAQAB";

    /**
     * 初始化注入
     */
    public static String userToken="";

    /**
     * 服务器时间-本地时间
     */
    public static long SERVER_TIME_INTERVAL=0;

    public static DeviceBean deviceBean;

    public static List<ThirdLoginInfo> thirdLoginInfoList;//第三方登陆信息

    //sp中保存的rsa密钥
    public static String SP_RSA_PUBLIC_KEY="sp_rsa_public_key";
    public final static String SP_VERSION_CODE="versionCode";
    public final static String SP_OPEN_CNT="sp_openCnt";
    public final static String SP_FLOATTIP_FLAG="showtip_again";
    public final static String SP_LASTFLOAT_LOCATION="float_location";
    public final static boolean READ_APP_AGENT=false;



    public final static String CODE_SUCCESS="200";
    public final static String CODE_NOLOGIN="-1000";//未登陆

    public  static String packageName="";//包名

}
