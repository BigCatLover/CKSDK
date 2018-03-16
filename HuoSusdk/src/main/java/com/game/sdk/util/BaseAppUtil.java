package com.game.sdk.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.game.sdk.log.L;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 当前程序是否后台运行
 * 当前手机是否处于睡眠
 * 当前网络是否已连接
 * 当前网络是否wifi状态
 * 安装apk
 * 初始化view的高度
 * 初始化view的高度后不可见
 * 判断是否为手机
 * 获取屏幕宽度
 * 获取屏幕高度
 * 获取设备的IMEI
 * 获取设备的mac地址
 * 获取当前应用的版本号
 * 收集设备信息并以Properties返回
 * 收集设备信息并以String返回
 */
public class BaseAppUtil {

    public static String getVersionCode(Context context){
        String version="";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//            L.e(TAG, "该应用的版本号: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            L.e(TAG, "getAppVersion", e);
        }
        return version;
    }

    public static boolean isApplicationBroughtToForground(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    private static final String TAG = BaseAppUtil.class.getSimpleName();

    /**
     * 设置状态栏沉浸，包含顶部状态栏和底部状态栏
     * @param activity
     * @param config 配置
     */
    public static void setStatusImmerse(Activity activity, SystemBarTintManager.SystemBarTintConfig config){
        if("m1".equals(Build.MODEL)){
            return;
        }
        if(config.isDarkmode()){
            setMiuiStatusBarDarkMode(true, activity);
            setMeiZuStatusBarDarkIcon(activity.getWindow(),true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if(config.getStatusColor()!=null&&config.getNavColor()!=null){
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setStatusBarTintColor(config.getStatusColor());
                tintManager.setStatusBarTintEnabled(true);
                if(config.getStatusBarAlpha()!=null){
                    tintManager.setStatusBarAlpha(config.getStatusBarAlpha());
                }
                if(config.getNavigationAlpha()!=null){
                    tintManager.setNavigationBarAlpha(config.getNavigationAlpha());
                }
                tintManager.setNavigationBarTintColor(config.getNavColor());
                tintManager.setNavigationBarTintEnabled(true);
                setRootView(activity,true);
            }else if(config.getStatusColor()!=null){
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setStatusBarTintColor(config.getStatusColor());
                if(config.getStatusBarAlpha()!=null){
                    tintManager.setStatusBarAlpha(config.getStatusBarAlpha());
                }
                tintManager.setStatusBarTintEnabled(true);
                setRootView(activity,true);
            }else if(config.getNavColor()!=null){
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setNavigationBarTintColor(config.getNavColor());
                if(config.getNavigationAlpha()!=null){
                    tintManager.setNavigationBarAlpha(config.getNavigationAlpha());
                }
                tintManager.setNavigationBarTintEnabled(true);
                setRootView(activity,true);
            }
        }
    }
    /**
     * 设置根布局参数 是否需要fitsSystemWindows
     */
    public static void setRootView(Activity activity,boolean fitsSystemWindows) {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setFitsSystemWindows(fitsSystemWindows);
        rootView.setClipToPadding(fitsSystemWindows);
    }
    /**
     *      设置miui状态栏黑色字体
     * @param darkmode
     * @param activity
     */
    public static void setMiuiStatusBarDarkMode(boolean darkmode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置魅族状态栏黑色字体
     * @param window
     * @param dark
     * @return
     */
    public static boolean setMeiZuStatusBarDarkIcon(Window window, boolean dark) {
        boolean result = false;
        if(window != null) {
            try {
                WindowManager.LayoutParams e = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt((Object)null);
                int value = meizuFlags.getInt(e);
                if(dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }

                meizuFlags.setInt(e, value);
                window.setAttributes(e);
                result = true;
            } catch (Exception var8) {
                L.e("StatusBar", "setStatusBarDarkIcon: failed");
            }
        }
        return result;
    }
    /**
     * 网络是否是连接状态
     *
     * @return true表示可能，false网络不可用
     */
    public static boolean isNetWorkConneted(Context ctx) {
        try {
            ConnectivityManager cmr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cmr != null) {
                NetworkInfo networkinfo = cmr.getActiveNetworkInfo();

                if (networkinfo != null && networkinfo.isConnectedOrConnecting()) {
                    return networkinfo.isAvailable();
                }
            }
        } catch (Exception e) {
            //Toast.makeText(ctx, "网络连接错误，请检查当前网络状态！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
        //return null != networkinfo && networkinfo.isConnectedOrConnecting();
    }
    public static void copyToSystem(Context context, String content) {
        ClipboardManager cm = (ClipboardManager)context.getSystemService("clipboard");
        cm.setText(content);
    }
    /**
     * 获取一个默认的
     * @param context
     * @return
     */
    public static File getDefaultSaveRootPath(Context context, String name) {
        File sdFile = null;
        try {
            sdFile = context.getExternalCacheDir();
            if(!sdFile.exists()||!sdFile.isDirectory()){
                sdFile=context.getCacheDir();
            }
            sdFile=new File(sdFile,name);
            if(!sdFile.exists()){
                sdFile.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sdFile=new File(context.getCacheDir(),name);
            if(!sdFile.exists()){

            }
        }
        return sdFile;
    }
}
