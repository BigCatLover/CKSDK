package com.game.sdk;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import com.game.sdk.log.L;
import com.game.sdk.util.DialogUtil;
import java.io.File;

/**
 * author janecer 2014年7月22日上午9:46:00 sdk系统核心类
 */
public class CKGameService extends Service {

    public static final String DOWNLOAD_APK_URL = "downLoadApkUrl";//下载apk的url常量
    public static final String DOWNLOAD_APK_VERSION = "version";
    private String downLoadApkUrl;//apk下载地址
    private String apkversion;
    private static Context mCtx;
    private boolean hasUnRegist=false;
    private static Dialog mDialog;
    private static final String TAG = CKGameService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context ctx) {
        Intent intent_service = new Intent(ctx, CKGameService.class);
        intent_service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startService(intent_service);
    }
    public static void startServiceByUpdate(final Context ctx, String downLoadApkUrl, String versionname) {
        mCtx=ctx;
        DialogUtil.showGameUpdateDialog(ctx, 3, new DialogUtil.ConfirmDialogListener() {
            @Override
            public void ok(Dialog dialog) {
                mDialog = dialog;
            }

            @Override
            public void cancle() {

            }
        });
        Intent intent_service = new Intent(ctx, CKGameService.class);
        intent_service.putExtra(DOWNLOAD_APK_VERSION, versionname);
        intent_service.putExtra(DOWNLOAD_APK_URL, downLoadApkUrl);
        ctx.startService(intent_service);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            downLoadApkUrl = intent.getStringExtra(DOWNLOAD_APK_URL);
            apkversion = intent.getStringExtra(DOWNLOAD_APK_VERSION);
            if (!TextUtils.isEmpty(downLoadApkUrl)) {
                // 调用下载
                initDownManager();
                return START_STICKY;
            }
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null&&!hasUnRegist) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    /**
     * 安卓系统下载类
     **/
    DownloadManager manager;
    /**
     * 接收下载完的广播
     **/
    DownloadCompleteReceiver receiver;

    /**
     * 初始化下载器
     **/
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver=new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(
                Uri.parse(downLoadApkUrl));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        //设置下载路径和下载的文件名
        String fileName=downLoadApkUrl;
        if(downLoadApkUrl.lastIndexOf("/")>=0){
            fileName=downLoadApkUrl.substring(downLoadApkUrl.lastIndexOf("/")+1,downLoadApkUrl.lastIndexOf("."))+apkversion+".apk";
        }
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        }else{
            down.setDestinationInExternalFilesDir(this, "", fileName);
        }
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        down.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        down.allowScanningByMediaScanner();
        L.d("hongliangsdk", "准备下载apk");
        // 将下载请求放入队列
        receiver.setDownloadId(manager.enqueue(down));
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {
        private Long downloadId;

        public Long getDownloadId() {
            return downloadId;
        }

        public void setDownloadId(Long downloadId) {
            this.downloadId = downloadId;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if(downloadId==null||downloadId!=downId){//不是对应的下载结果
                    return;
                }
                try {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downId);
                    Cursor c = manager.query(query);
                    if(c.moveToFirst()) {
                        //获取文件下载路径
                        String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        //如果文件路径不为空，说明已经存在了
                        if(filename != null){
                            File file= new File(filename);
                            if(file.exists()){
                                String name = filename.substring(filename.lastIndexOf("/")+1);
                                if(name.endsWith(".apk")){
                                    installApk(context,file);
                                }
                                //安装完毕注销广播
                                unregisterReceiver(this);
                                hasUnRegist=true;
                            }
                        }
                    }
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 安装apk by文件
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType("application/vnd.android.package-archive");
            intent.setData(Uri.fromFile(file));
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            L.d(TAG,"e:"+e);
            e.printStackTrace();
        }
    }
}
