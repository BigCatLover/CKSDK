package com.game.sdk.util;

import android.util.Base64;

import com.game.sdk.domain.NotProguard;

/**
 * Created by liu hong liang on 2016/11/14.
 */
@NotProguard
public class Base64Util {
    public final static String CHARACTER="utf-8";
    public static String encode(String data){
        return Base64.encodeToString(data.getBytes(),Base64.NO_WRAP);
    }
    public static String encode(byte[] data){
        return Base64.encodeToString(data,Base64.NO_WRAP);
    }
    public static byte[] decode(String data){
        return Base64.decode(data.getBytes(),Base64.NO_WRAP);
    }
}
