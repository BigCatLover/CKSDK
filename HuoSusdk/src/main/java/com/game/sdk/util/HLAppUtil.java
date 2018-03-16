package com.game.sdk.util;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * Created by liu hong liang on 2016/11/5.
 */

public class HLAppUtil {
    /**
     * 4.4 以上可以直接判断准确
     *
     * 4.4 以下非MIUI直接返回true
     *
     * 4.4 以下MIUI 可 判断 上一次打开app 时 是否开启了悬浮窗权限
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;

//        if(!DeviceUtil.isFlyme4() && !DeviceUtil.isMiui(context)){
//            return true;
//        }
        if (version >= 19) {
            return checkOp(context, 24);  //自己写就是24 为什么是24?看AppOpsManager //AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if(MiuiDeviceUtil.isMiui()){
                if ((context.getApplicationInfo().flags & 1 << 27) == 1 <<27 ) {
                    return true;
                } else {
                    return false;
                }
            }else{
                return true;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class managerClass = manager.getClass();
                Method method = managerClass.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int isAllowNum = (Integer) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());

                if (AppOpsManager.MODE_ALLOWED == isAllowNum) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return String.valueOf(info.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
