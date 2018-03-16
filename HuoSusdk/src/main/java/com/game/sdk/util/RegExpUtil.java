package com.game.sdk.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liu hong liang on 2016/11/14.
 */

public class RegExpUtil {

    public static boolean isMatchAccount(String data){
        Pattern pattern = Pattern.compile("[1234567890A-Za-z]{6,16}");
        return pattern.matcher(data).matches();
    }
    public static boolean isMatchPassword(String data){
//        Pattern pattern = Pattern.compile("[1234567890A-Za-z]{6,16}");
        Pattern pattern = Pattern.compile("\\S{6,16}");
        return pattern.matcher(data).matches();
    }
    public static boolean isMatchRegistAccount(String data){
        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{5,15}$");
        return pattern.matcher(data).matches();

    }
    /**
     * 判断是否是手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNumber(String mobiles) {
        Pattern p = Pattern.compile("^((1[0-9][0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
