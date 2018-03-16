/*
 * Copyright (c) 2017.
 * MD5.java   UTF-8
 * Create by liuhongliang <752284118@qq.com> on 17-1-20 下午3:55
 *
 * Last modified 16-10-18 上午9:33
 */

package com.game.sdk.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liu hong liang on 2017/1/20.
 * MD5加密
 */
public class MD5 {
    public static final String TAG = MD5.class.getSimpleName();
    public static String md5(String str) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();  //md5 32bit
            // result = buf.toString().substring(8, 24))); //md5 16bit
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
