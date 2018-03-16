package com.game.sdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.game.sdk.SdkConstant;
import com.game.sdk.db.impl.AgentDbDao;
import com.game.sdk.domain.AgentDbBean;
import com.game.sdk.domain.NotProguard;
import com.game.sdk.log.L;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 读取zip文件渠道参数
 */
@NotProguard
public class ChannelNewUtil {
    public static final String AGENT_FILE = "META-INF/gamechannel";
    public static final String AGENT_FILE2 = "META-INF/huosdk";

    public static String getChannel(Context context) {
        // 从配置文件中 找到文件
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        String channelId = "";
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(AGENT_FILE) || entryName.startsWith(AGENT_FILE2)) {
                    ByteArrayOutputStream bos = null;
                    InputStream is = null;
                    try {
                        bos = new ByteArrayOutputStream();
                        is = zipfile.getInputStream(entry);
                        channelId = getAgentgame(is, bos);
                        if (!TextUtils.isEmpty(channelId)) {//读到了
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            bos.close();
                            is.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return channelId;
    }

    private static String getAgentgame(InputStream is, ByteArrayOutputStream bos) throws Exception {
        byte[] buffer = new byte[1024];
        int num = 0;
        while (-1 != (num = is.read(buffer))) {
            bos.write(buffer, 0, num);
            bos.flush();
        }
        String jsonStr = bos.toString("utf-8");
        bos.close();
        is.close();
        JSONObject o = new JSONObject(jsonStr);
        return o.getString("agentgame");
    }

    /**
     * 从外部app获取一个agent
     * @param context
     * @return
     */
    public static String getChannelByApp(Context context) {
        L.e("hongliang","准备app读取agent");
        String agent="";
        SharedPreferences sp = context.getSharedPreferences("agent.sp", Context.MODE_PRIVATE);
        int loaclIntallCode = sp.getInt(AgentDbBean.INSTALL_CODE, 0);
        int spVersionCode = sp.getInt(AgentDbBean.VERSION_CODE, 0);
        String localAgent = sp.getString(AgentDbBean.AGENT, null);
        int versionCode= DeviceUtil.getAppVersionCode(context);
        AgentDbBean agentDbBean = AgentDbDao.getInstance(context).getAgengDbBeanByPackageName(context.getPackageName());
        L.e("hongliang","获取到app包名="+ SdkConstant.APP_PACKAGENAME);
        if(!isInstallApp(context, SdkConstant.APP_PACKAGENAME)){//app被删除了
            agentDbBean=null;
            AgentDbDao.getInstance(context).deleteAgentDb();
        }
        if(spVersionCode==0){//新安装的，先看app存的agent被用过没有
            if(agentDbBean!=null&&agentDbBean.getInstallCode()!=null){
                String installCode = agentDbBean.getInstallCode();
                if(installCode.contains("_")){
                    String[] split = installCode.split("_");
                    if(split.length>1&&"0".equals(split[1])&&split[0].equals(versionCode+"")){//没有用过,且当前版本号和记录的版本号一致
                        AgentDbDao.getInstance(context).useInstallCode(agentDbBean.getPackageName(),split[0]+"_1");
                        agent=agentDbBean.getAgent();
                        L.e("hongliang","第一次从app拿到agent:"+agent);
                    }
                }
            }
        }else{
            if(agentDbBean!=null&&agentDbBean.getInstallCode()!=null){
                String installCode = agentDbBean.getInstallCode();
                if(installCode.contains("_")){
                    String[] split = installCode.split("_");
                    if(split.length>0&&split[0].compareTo(versionCode+"")>=0){//app记录的版本号比现在的要大
                        agent= agentDbBean.getAgent();
                        L.e("hongliang","非第一次安装从app拿到agent:"+agent);
                    }
                }
            }
        }
        //用过一次后保存版本号
        sp.edit().putInt(AgentDbBean.VERSION_CODE, DeviceUtil.getAppVersionCode(context)).commit();

        L.e("hongliang","从外部app拿到的agent:"+agent);
        return agent;
    }

    /**
     * 保存agent到本地存储区
     * @param context
     * @param agent
     */
    public static void saveAgentToSp(Context context,String agent){
        SharedPreferences sp = context.getSharedPreferences("agent.sp", Context.MODE_PRIVATE);
        int loaclIntallCode = sp.getInt(AgentDbBean.INSTALL_CODE, 0);
        int versionCode= DeviceUtil.getAppVersionCode(context);
        sp.edit().putString(AgentDbBean.AGENT, agent)
                .putInt(AgentDbBean.INSTALL_CODE, loaclIntallCode)
                .putInt(AgentDbBean.VERSION_CODE,versionCode)
                .commit();
    }
    private static Intent getIntentByPackageName(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    public static boolean isInstallApp(Context context, String packageName) {
        if(TextUtils.isEmpty(packageName)){
            return false;
        }
        return TextUtils.isEmpty(packageName)?false:getIntentByPackageName(context, packageName) != null;
    }
    /**
     * 保存agent到sp并且更新sdk agent
     * @param context
     * @param agent
     */
    public static void saveAgentAndUpdateSdkAgent(Context context,String agent){
        Log.e("hongliang","准备保存的agent="+agent);
        saveAgentToSp(context,agent);
        //更新外部sdk数据库的agent
        AgentDbDao.getInstance(context).updateAllAgent(agent);
    }

    public static String getEncryptAgentBySp(Context context){
        SharedPreferences sp = context.getSharedPreferences("agent.sp", Context.MODE_PRIVATE);
        String agent = sp.getString(AgentDbBean.AGENT, "");
        return agent;
    }

    /**
     * 根据apk文件获取版本号
     * @param context
     * @param apkFile
     * @return
     */
    public static int getVersionCodeFromApkFile(Context context,String apkFile){
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFile, PackageManager.GET_ACTIVITIES);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
