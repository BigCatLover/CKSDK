package com.game.sdk.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @作者: XQ
 * @创建时间：15-7-20 下午10:08
 * @类说明:登录控制类
 */
public class LoginControl {
    public static final String PRFS_USER_NAME = "prfs_user_name";

    public static String getBindMobile() {
        return bindMobile;
    }

    public static void setBindMobile(String value) {
        bindMobile = value;
    }

    private static String bindMobile;

    public static boolean isUpgradeforpay() {
        return upgradeforpay;
    }

    public static void setUpgradeforpay(boolean upgradeforpay) {
        LoginControl.upgradeforpay = upgradeforpay;
    }

    private static boolean upgradeforpay;

    public static String getVisitorname() {
        return visitorname;
    }

    public static void setVisitorname(String visitorname) {
        LoginControl.visitorname = visitorname;
    }

    private static String visitorname;

    protected static SharedPreferences visitor_sp = null;
    protected static String mUserToken;

    public static String getMemid() {
        return memid;
    }

    public static void setMemid(String memid) {
        LoginControl.memid = memid;
    }

    protected static String memid;

    public static boolean isVisitorFlag() {
        return visitorFlag;
    }

    public static void setVisitorFlag(boolean visitorFlag) {
        LoginControl.visitorFlag = visitorFlag;
    }

    protected static boolean visitorFlag;

    public static synchronized void init(Context context) {
        if (visitor_sp == null) {
            visitor_sp = context.getSharedPreferences(LoginControl.class.getName(), Context.MODE_PRIVATE);
        }
    }

    public static void savaVisitorName(String value) {
        if (!TextUtils.isEmpty(value)) {
            visitor_sp.edit().putString(PRFS_USER_NAME, value).commit();
        }
    }

    public static void clearVisitorName() {
        if (visitor_sp != null) {
            if (visitor_sp.edit() != null) {
                visitor_sp.edit().clear().commit();
            }
        }
    }

    public static String getVisitorName() {
        return visitor_sp.getString(PRFS_USER_NAME, "");
    }

    public static void saveUserToken(String userToken) {
        if (!TextUtils.isEmpty(userToken)) {
            mUserToken = userToken;
        }
    }

    public static String getUserToken() {
        return mUserToken;
    }

    public static boolean isLogin() {
        if (isVisitorFlag()) {
            return true;
        } else {
            return !TextUtils.isEmpty(getUserToken());
        }
    }

    public static void clearLogin() {
        mUserToken = null;
    }
}
