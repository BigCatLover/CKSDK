package com.game.sdk.log;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.game.sdk.db.LoginControl;

import java.util.Map;
import java.util.Set;

/**
 * Created by 刘红亮 on 2016/3/4.
 */
public  class SP {
    private static SharedPreferences sp =null;
    public static synchronized void init(Context context){
        if(sp==null){
            sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            LoginControl.init(context);
        }
    }
    public static  boolean getBoolean(String key, boolean defaultVal) {
        return sp.getBoolean(key, defaultVal);
    }

    public static  boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }


    public static  String getString(String key, String defaultVal) {
        return sp.getString(key, defaultVal);
    }

    public static  String getString(String key) {
        return sp.getString(key, null);
    }

    public static  int getInt(String key, int defaultVal) {
        return sp.getInt(key, defaultVal);
    }

    public static  int getInt(String key) {
        return sp.getInt(key, 0);
    }


    public static  float getFloat(String key, float defaultVal) {
        return sp.getFloat(key, defaultVal);
    }

    public static  float getFloat(String key) {
        return sp.getFloat(key, 0f);
    }

    public static  long getLong(String key, long defaultVal) {
        return sp.getLong(key, defaultVal);
    }

    public static  long getLong(String key) {
        return sp.getLong(key, 0l);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static  Set<String> getStringSet(String key, Set<String> defaultVal) {
        return sp.getStringSet(key, defaultVal);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static  Set<String> getStringSet(String key) {
        return sp.getStringSet(key, null);
    }

    public static  Map<String, ?> getAll() {
        return sp.getAll();
    }


    public static SharedPreferences.Editor  putString(String key, String value) {
        return sp.edit().putString(key, value);
//        sp.edit().commit();
//        return sp.edit();
    }

    public static  SharedPreferences.Editor putInt(String key, int value) {
        return sp.edit().putInt(key, value);
//        sp.edit().commit();
//        return sp.edit();
    }

    public static  SharedPreferences.Editor putFloat(String key, float value) {
        return sp.edit().putFloat(key, value);
//        sp.edit().commit();
//        return sp.edit();
    }

    public static  SharedPreferences.Editor putLong(String key, long value) {
       return sp.edit().putLong(key, value);
//        sp.edit().commit();
//        return sp.edit();
    }

    public static  SharedPreferences.Editor putBoolean(String key, boolean value) {
       return sp.edit().putBoolean(key, value);
//        sp.edit().commit();
//        return sp.edit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static  SharedPreferences.Editor putStringSet(String key, Set<String> value) {
        return sp.edit().putStringSet(key, value);
//        sp.edit().commit();
//        return sp.edit();
    }

    public static SharedPreferences getSp() {
        return sp;
    }

    public static void setSp(SharedPreferences sp) {
        SP.sp = sp;
    }
}
