package com.game.sdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.game.sdk.log.L;
import com.game.sdk.log.T;
import com.game.sdk.view.LoginView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * Created by liu hong liang on 2016/11/5.
 */

public class MiuiDeviceUtil {
    private static final String TAG = MiuiDeviceUtil.class.getSimpleName();

    public static boolean isMiui(){
        return !TextUtils.isEmpty(getMiuiVersionProperty());
    }
    /**
     * 经测试V5版本是有区别的
     *
     * @param context
     */
    public static void openMiuiPermissionActivity(Context context) {
//        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//
//        if ("V5".equals(getMiuiVersionProperty())) {
//            PackageInfo pInfo = null;
//            try {
//                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            } catch (PackageManager.NameNotFoundException e) {
//                L.e("canking", "error");
//            }
//            intent.setClassName("com.miui.securitycenter", "com.miui.securitycenter.permission.AppPermissionsEditor");
//            intent.putExtra("extra_package_uid", pInfo.applicationInfo.uid);
//        } else {
//            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//            intent.putExtra("extra_pkgname", context.getPackageName());
//        }
//        try {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        int versionCode = getMiuiVersion();
        if (versionCode == 5) {
            goToMiuiPermissionActivity_V5(context);
        } else if (versionCode == 6) {
            goToMiuiPermissionActivity_V6(context);
        } else if (versionCode == 7) {
            goToMiuiPermissionActivity_V7(context);
        } else if (versionCode == 8) {
            goToMiuiPermissionActivity_V8(context);
        } else {
        }

    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }
    public static int getMiuiVersion() {
        String version = getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
            }
        }
        return -1;
    }

    public static void goToMiuiPermissionActivity_V5(Context context) {
        Intent intent = null;
        String packageName = context.getPackageName();
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package" , packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
        }
    }


    public static void goToMiuiPermissionActivity_V6(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
        }
    }


    public static void goToMiuiPermissionActivity_V7(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
        }
    }


    public static void goToMiuiPermissionActivity_V8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
        }
    }
    private static boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null) {
            return false;
        }
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


    public static String getMiuiVersionProperty() {
        String property = "";
        try {
            Class<?> spClazz = Class.forName("android.os.SystemProperties");
            Method method = spClazz.getDeclaredMethod("get", String.class, String.class);
            property = (String) method.invoke(spClazz, "ro.miui.ui.version.name", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return property;
    }
}
