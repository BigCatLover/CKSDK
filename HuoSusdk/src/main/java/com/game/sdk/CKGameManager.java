package com.game.sdk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.game.sdk.db.LoginControl;
import com.game.sdk.dialog.OpenFloatPermissionDialog;
import com.game.sdk.domain.AdBean;
import com.game.sdk.domain.AdGetResultBean;
import com.game.sdk.domain.BaseRequestBean;
import com.game.sdk.domain.CustomPayParam;
import com.game.sdk.domain.GameBean;
import com.game.sdk.domain.GameResultBean;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.NotProguard;
import com.game.sdk.domain.Notice;
import com.game.sdk.domain.NoticeResultBean;
import com.game.sdk.domain.RegisterResultBean;
import com.game.sdk.domain.RoleInfo;
import com.game.sdk.domain.SdkPayRequestBean;
import com.game.sdk.domain.StartUpBean;
import com.game.sdk.domain.StartupResultBean;
import com.game.sdk.domain.SubmitRoleInfoCallBack;
import com.game.sdk.domain.UproleinfoRequestBean;
import com.game.sdk.domain.UserNameRegisterRequestBean;
import com.game.sdk.floatwindow.FloatViewManager;
import com.game.sdk.http.HttpCallbackDecode;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.listener.OnGetGamesListener;
import com.game.sdk.listener.OnInitSdkListener;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.listener.OnLogoutListener;
import com.game.sdk.listener.OnPaymentListener;
import com.game.sdk.log.L;
import com.game.sdk.log.SP;
import com.game.sdk.log.T;
import com.game.sdk.so.NativeListener;
import com.game.sdk.so.SdkNative;
import com.game.sdk.ui.CkLoginActivity;
import com.game.sdk.ui.WebPayActivity;
import com.game.sdk.util.Base64Util;
import com.game.sdk.util.BaseAppUtil;
import com.game.sdk.util.DeviceUtil;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.HLAppUtil;
import com.game.sdk.util.MiuiDeviceUtil;
import com.game.sdk.util.RegExpUtil;
import com.game.sdk.util.ResourceUtils;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.http.RequestQueue;
import com.kymjs.rxvolley.toolbox.HTTPSTrustManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * author janecer 2014年7月22日上午9:45:18
 */
public class CKGameManager {
    private final static int CODE_INIT_FAIL = -1;
    private final static int CODE_INIT_SUCCESS = 1;
    private static final boolean isDebug = false;
//    private boolean debug = false;
    private static boolean loginflag;
    private static final String TAG = CKGameManager.class.getSimpleName();
    private static CKGameManager instance;
    private Context mContext;
    private OnInitSdkListener onInitSdkListener;
    private OnPaymentListener paymentListener;
    private OnLoginListener onLoginListener;
    private OnLogoutListener onLogoutListener;
    private int initRequestCount = 0;
    public static Notice notice; //登录后公告
    public static boolean isSwitchLogin = false; //是否切换
    private String appid, clientid, clientkey;

    public static List<AdBean> getAdList() {
        return adList;
    }

    public static boolean getSwitchFlag() {
        return isSwitchLogin;
    }

    public static void setSwitchFlag(boolean isSwitch) {
        isSwitchLogin = isSwitch;
    }


    public static List<AdBean> adList = new ArrayList<>();
    public static List<GameBean> gameList = new ArrayList<>();

    public static void setIsUpgradeWhenPay(boolean isUpgradeWhenPay) {
        CKGameManager.isUpgradeWhenPay = isUpgradeWhenPay;
    }

    public static boolean isUpgradeWhenPay = false;

    public static void setLoginflag(boolean loginflag) {
        CKGameManager.loginflag = loginflag;
    }

    private Handler lyGameSdkHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_INIT_FAIL:
                    if (msg.arg2 < 3) {//最多重试3次
                        initSdk(msg.arg2 + 1);
                    } else {
                        //关闭等待loading
                        onInitSdkListener.initError(String.valueOf(msg.arg1), msg.obj.toString());
                        DialogUtil.dismissDialog();
                    }
                    break;
                case CODE_INIT_SUCCESS:
                    initRequestCount++;
//                    if (debug) {
//                        setGameParams();
//                    }
                    //去初始化
                    gotoStartup(1);
                    break;
            }
        }
    };

    // 单例模式
    @NotProguard
    public static synchronized CKGameManager getInstance() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            L.d(TAG, "实例化失败,未在主线程调用");
            return null;
        }
        if (null == instance) {
            instance = new CKGameManager();
        }
        return instance;
    }

    @NotProguard
    public void setContext(Context context) {
        this.mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @NotProguard
    private CKGameManager() {
    }

    /**
     * 初始化设置
     */
    private void initSetting() {
        RxVolley.setDebug(isDebug);
        L.init(isDebug);
        HTTPSTrustManager.allowAllSSL();//开启https支持
        try {
            RxVolley.setRequestQueue(RequestQueue.newRequestQueue(BaseAppUtil.getDefaultSaveRootPath(mContext, "huoHttp")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化sdk
     *
     * @param context           上下文对象
     * @param onInitSdkListener 回调监听
     */
    @NotProguard
    public void initSdk(Context context, OnInitSdkListener onInitSdkListener) {
        this.onInitSdkListener = onInitSdkListener;
        this.mContext = context;
        loginflag = false;
        initSetting();
        CKGameService.startService(mContext);
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(mContext, "");
        //初始化设备信息
        SdkNative.soInit(context);
        //初始化sp
        SP.init(mContext);
        initRequestCount = 0;
        getAgent();
        initSdk(1);
    }

    private void getAgent() {
        ApplicationInfo appinfo = mContext.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF/lyg_game")) { //xxx 表示要读取的文件名
                    //利用ZipInputStream读取文件
                    long size = entry.getSize();
                    if (size > 0) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(zipfile.getInputStream(entry)));
                        String line;
                        String result = "";
                        while ((line = br.readLine()) != null) {  //文件内容都在这里输出了，根据你的需要做改变
                            System.out.println(line);
                            result = line;
                        }
                        String[] s = result.split("\"");
                        byte[] decode = Base64Util.decode(s[3]);
                        String rt = new String(decode);
                        SdkConstant.HS_AGENT = rt.replace("_LYG_GAME", "");
                        br.close();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            L.d(TAG, "IOException e:" + e);
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
    }


    /**
     * 初始化相关数据
     * count=1标示正常请求，2表示在初始化时发现rsakey错误后的重试流程
     */
    private void initSdk(final int count) {
        loginflag = false;
        isSwitchLogin = mContext.getSharedPreferences("huo_sdk_sp", Context.MODE_PRIVATE).getBoolean("switch_login", false);
        //TODO 如果判断有切换账号逻辑，则不执行nativeInit，将使用net获取的值,此时直接返回init_success
        if (isSwitchLogin) {
            Message message = Message.obtain();
            message.what = CODE_INIT_SUCCESS;
            message.arg2 = count;
            lyGameSdkHandler.sendMessage(message);
            return;
        }
        //初始化native
        AsyncTask<String, Integer, String> nativeAsyncTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //弹出等待loading在，installer和startup都完成后或者出现异常时关闭
                DialogUtil.showDialog(mContext, false, ResourceUtils.getString(mContext, "ck_init"));
            }

            @Override
            protected String doInBackground(String... params) {
//                SdkConstant.CK_APPID=HLAppUtil.getMetaData(mContext,"CK_APPID");
//                SdkConstant.CK_CLIENTID=HLAppUtil.getMetaData(mContext,"CK_CLIENTID");
//                SdkConstant.CK_CLIENTKEY=HLAppUtil.getMetaData(mContext,"CK_CLIENTKEY");

//                if((!SdkConstant.CK_APPID.isEmpty())&&(!SdkConstant.CK_CLIENTID.isEmpty())&&(!SdkConstant.CK_CLIENTKEY.isEmpty())){
//                    Message message = Message.obtain();
//                    message.what = CODE_INIT_SUCCESS;
//                    message.arg2 = count;
//                    lyGameSdkHandler.sendMessage(message);
//                }else {
//                    Message message = Message.obtain();
//                    message.what = CODE_INIT_FAIL;
//                    message.arg1 = 11;
//                    message.obj = ResourceUtils.getString(mContext,"ck_params_err");
//                    message.arg2 = count;
//                    lyGameSdkHandler.sendMessage(message);
//                }
                //初始化本地c配置
                if (SdkNative.initLocalConfig(mContext, SdkNative.TYPE_SDK)) {
                    SdkNative.initNetConfig(mContext, new NativeListener() {
                        @Override
                        public void onSuccess() {
                            Message message = Message.obtain();
                            message.what = CODE_INIT_SUCCESS;
                            message.arg2 = count;
                            lyGameSdkHandler.sendMessage(message);
                        }

                        @Override
                        public void onFail(int code, final String msg) {
                            Message message = Message.obtain();
                            message.what = CODE_INIT_FAIL;
                            message.arg1 = code;
                            message.obj = msg;
                            message.arg2 = count;
                            lyGameSdkHandler.sendMessage(message);
                        }
                    });
                } else {
                    Message message = Message.obtain();
                    message.what = CODE_INIT_SUCCESS;
                    message.arg2 = count;
                    lyGameSdkHandler.sendMessage(message);
                }
                return null;
            }
        };
        if (!BaseAppUtil.isNetWorkConneted(mContext)) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_link_error"), Toast.LENGTH_SHORT).show();
            return;
        }
        nativeAsyncTask.execute();
    }

    /**
     * count=1标示正常请求，2表示在初始化时发现rsakey错误后的重试流程
     *
     * @param count 当前是第几次请求
     */
    private void gotoStartup(final int count) {
        StartUpBean startUpBean = new StartUpBean();
        int open_cnt = SdkNative.addInstallOpenCnt(mContext);//增量更新openCnt
        SdkNative.setInstallOpenCnt(mContext, open_cnt);
        startUpBean.setOpen_cnt(open_cnt + "");

        startUpBean.setVersioncode(BaseAppUtil.getVersionCode(mContext) + "");
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(startUpBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<StartupResultBean>(mContext, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(final StartupResultBean data) {
                if (data != null) {
                    SdkConstant.userToken = data.getUser_token();
                    SdkConstant.SERVER_TIME_INTERVAL = data.getTimestamp() - System.currentTimeMillis();
                    SdkConstant.thirdLoginInfoList = data.getOauth_info();
                    String s = data.getUp_status();
                    if (!TextUtils.isEmpty(s)&&"1".equals(s)) {
                        SdkNative.resetInstall(mContext);//有更新重置install数据
                        if (!TextUtils.isEmpty(data.getUp_url())) {
                            final String newversion = data.getVersioncode().replace(".", "");
                            String url = data.getUp_url();
                            if (url.lastIndexOf("/") >= 0) {
                                url = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".")) + "_" + newversion + ".apk";
                            }
                            String filename = Environment.getExternalStorageDirectory() + File.separator + "Download" + File.separator + url;
                            final File file = new File(filename);
                            if (file.exists() && filename.endsWith(".apk")) {
                                DialogUtil.showGameUpdateDialog(mContext, 2, new DialogUtil.ConfirmDialogListener() {
                                    @Override
                                    public void ok(Dialog dialog) {
                                        dialog.dismiss();
                                        try {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                                            intent.setType("application/vnd.android.package-archive");
                                            intent.setData(Uri.fromFile(file));
                                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.startActivity(intent);
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void cancle() {

                                    }
                                });
                            } else {
                                DialogUtil.showGameUpdateDialog(mContext, 1, new DialogUtil.ConfirmDialogListener() {
                                    @Override
                                    public void ok(Dialog dialog) {
                                        dialog.dismiss();
                                        CKGameService.startServiceByUpdate(mContext, data.getUp_url().replace("huosu", "game"), "_" + newversion);
                                    }

                                    @Override
                                    public void cancle() {

                                    }
                                });
                            }

                        } else {
                            onInitSdkListener.initSuccess("200", ResourceUtils.getString(mContext, "ck_init_success"));
                            getAd();
                        }
                    } else {
                        onInitSdkListener.initSuccess("200", ResourceUtils.getString(mContext, "ck_init_success"));
                        getAd();
                    }
                }
            }

            @Override
            public void onFailure(String code, String msg) {
                if (count < 3) {
                    //1001	请求KEY错误	rsakey	解密错误
                    if (HttpCallbackDecode.CODE_RSA_KEY_ERROR.equals(code)) {//删除本地公钥，重新请求rsa公钥
                        SdkNative.resetInstall(mContext);
                        L.e(TAG, "rsakey错误，重新请求rsa公钥");
                        if (initRequestCount < 2) {//initSdk只重试一次rsa请求
                            initSdk(1000);
                            return;
                        }
                    }
                    super.onFailure(code, msg);
                    gotoStartup(count + 1);//重试
                } else {
                    super.onFailure(code, msg);
                    onInitSdkListener.initError(code, msg);
                }
            }
        };
        httpCallbackDecode.setShowTs(false);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(false);//对话框继续使用install接口，在startup联网结束后，自动结束等待loading
        RxVolley.post(SdkApi.getStartup(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }


    @NotProguard
    public boolean isLogin() {
        return LoginControl.isLogin();
    }


    private void getAd() {
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(baseRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<AdGetResultBean>(mContext, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(final AdGetResultBean data) {
                adList = data.getAdv_list();
            }

            @Override
            public void onFailure(String code, String msg) {
                adList.clear();
            }
        };
        httpCallbackDecode.setShowTs(false);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(false);
        RxVolley.post(SdkApi.getAdData(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    /**
     * 执行退出登陆
     *
     * @param type
     */
    public void logoutExecute(final int type) {
        if (!LoginControl.isLogin()) {
            if (onLogoutListener != null) {
                onLogoutListener.logoutSuccess(type, SdkConstant.CODE_NOLOGIN, ResourceUtils.getString(mContext, "ck_unlogin"));
            }
            return;
        }
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(baseRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<NoticeResultBean>(mContext, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(NoticeResultBean data) {
                removeFloatView();
                if (onLogoutListener != null) {
                    onLogoutListener.logoutSuccess(type, SdkConstant.CODE_SUCCESS, ResourceUtils.getString(mContext, "ck_logout_success"));
                }
                LoginControl.clearLogin();
                loginflag = false;

            }

            @Override
            public void onFailure(String code, String msg) {
                super.onFailure(code, msg);
                if (onLogoutListener != null) {
                    onLogoutListener.logoutError(type, code, msg);
                }
            }
        };
        httpCallbackDecode.setShowTs(false);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(false);
        RxVolley.post(SdkApi.getLogout(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    /**
     * 获取游戏列表 （满足商务需要提供demo选择已接入游戏进行充值的要求），商务测试demo用
     */
//    @NotProguard
    public void getGameList(Context c, final OnGetGamesListener listener) {
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(baseRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<GameResultBean>(c, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(GameResultBean data) {
                gameList = data.getData();
                if (listener != null) {
                    listener.getFinish(gameList);
                }
            }

            @Override
            public void onFailure(String code, String msg) {
                if (listener != null) {
                    listener.getError(code, msg);
                }
                gameList.clear();
            }
        };
        httpCallbackDecode.setShowTs(false);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(true);
        RxVolley.post(SdkApi.getGameAll(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }


    public void setGameParams() {
//        SdkConstant.CK_APPID = this.appid;
//        SdkConstant.CK_CLIENTID = this.clientid;
//        SdkConstant.CK_CLIENTKEY = this.clientkey;
    }

    /**
     * 修改游戏参数，商务测试demo用
     */
    @NotProguard
    public void setGameParams(String appid, String clientId, String clientKey) {
        this.appid = appid;
        this.clientid = clientId;
        this.clientkey = clientKey;
    }

    /**
     * 商务测试demo用
     */

//    @NotProguard
//    public void setDebug(boolean debug) {
//        this.debug = debug;
//    }

    /**
     * 自动登录注册，不显示界面，商务测试demo用
     */
//    @NotProguard
    public void AutoRegist(String name, String psw, boolean islogin) {
        if (name == null && psw == null) {
            int index = 0;
            String accout = getRandomAccount();
            for (int i = 0; i < accout.length(); i++) {
                char a = accout.charAt(i);
                if (a >= '0' && a <= '9') {
                    index = i;
                    break;
                }
            }
            String password = accout.substring(0, index) + "111111";
//            Log.e("zl", "password:" + password + " accout:" + accout);
            submitRegister(accout, password, islogin);
        } else {
            submitRegister(name, psw, islogin);
        }

    }

    private String getRandomAccount() {
        StringBuilder s = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //账号位数
        Random rand = new Random();
        int cnt = 0;
        int count = rand.nextInt(11) + 6;
        if (count < 8) {
            char c1 = chars.charAt((int) (Math.random() * 52));
            s.append(c1);
            cnt = 1;
        } else if (count < 10) {
            char c1 = chars.charAt((int) (Math.random() * 52));
            s.append(c1);
            c1 = chars.charAt((int) (Math.random() * 52));
            s.append(c1);
            cnt = 2;
        } else {
            char c1 = chars.charAt((int) (Math.random() * 52));
            s.append(c1);
            c1 = chars.charAt((int) (Math.random() * 52));
            s.append(c1);
            c1 = chars.charAt((int) (Math.random() * 52));
            s.append(c1);
            cnt = 3;
        }
        for (int i = 0; i < count - cnt; i++) {
            int ss = rand.nextInt(10);
            s.append(ss);
        }
        return s.toString();
    }

    private void submitRegister(final String accoutname, final String password, final boolean islogin) {
        if (!RegExpUtil.isMatchRegistAccount(accoutname)) {
            T.s(mContext, ResourceUtils.getString(mContext, "ck_account_err"));
            return;
        }
        if (!RegExpUtil.isMatchPassword(password)) {
            T.s(mContext, ResourceUtils.getString(mContext, "ck_password_err"));
            return;
        }
        String url = "";
        if (islogin) {
            url = SdkApi.getLogin();
        } else {
            url = SdkApi.getRegister();
        }
        UserNameRegisterRequestBean userNameRegisterRequestBean = new UserNameRegisterRequestBean();
        userNameRegisterRequestBean.setUsername(accoutname);
        userNameRegisterRequestBean.setPassword(password);
        userNameRegisterRequestBean.setType(2);
        userNameRegisterRequestBean.setUpdate(0);
        userNameRegisterRequestBean.setIntroducer("");
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(userNameRegisterRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<RegisterResultBean>(mContext, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(RegisterResultBean data) {
                if (data != null) {
                    LoginControl.saveUserToken(data.getCp_user_token());
//                    LoginControl.setVisitorFlag(data.getType()==3);
                    OnLoginListener onLoginListener = getOnLoginListener();
                    if (onLoginListener != null) {
                        onLoginListener.loginSuccess(new LogincallBack(accoutname, password));
                    }
                }
            }

            @Override
            public void onFailure(String code, String msg) {
                L.d(TAG, "regist fail:" + code + " " + msg);
                if (!islogin) {
                    AutoRegist(null, null, false);
                }
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        RxVolley.post(url, httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }


    /**
     * 退出登陆
     */
    @NotProguard
    public void logout() {
        logoutExecute(OnLogoutListener.TYPE_NORMAL_LOGOUT);
    }

    /**
     * 退出登录
     */
    @NotProguard
    public void addLogoutListener(final OnLogoutListener onLogoutListener) {
        this.onLogoutListener = onLogoutListener;
    }

    /**
     * 打开用户中心
     */
    @NotProguard
    public void openUcenter() {
        if (!LoginControl.isLogin()) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_login_first"), Toast.LENGTH_SHORT).show();
            return;
        }
        FloatViewManager.getInstance(mContext).openucenter();
    }

    /**
     * 显示登录
     */
    @NotProguard
    public void showLogin() {
        if (!isUpgradeWhenPay) {
            LoginControl.clearLogin();
            loginflag = false;
        }
        //普通登陆类型
        removeFloatView();
        CkLoginActivity.start(mContext, false);
    }

    /**
     * 切换账号
     */
    @NotProguard
    public void switchAccount() {
        if (LoginControl.isVisitorFlag()) {
            isSwitchLogin = true;
        }
        logoutExecute(OnLogoutListener.TYPE_SWITCH_ACCOUNT);
    }

    /**
     * 注册一个登录监听，需要在不使用的时候解除监听，例如onDestory方法中解除
     *
     * @param onLoginListener 登陆监听
     */
    @NotProguard
    public void addLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    /**
     * 解除登陆监听
     */
    @NotProguard
    public void removeLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = null;
    }

    /**
     * 启动支付
     *
     * @param payParam        支付参数
     * @param paymentListener 支付回调监听
     */
    @NotProguard
    public void showPay(CustomPayParam payParam, OnPaymentListener paymentListener) {
        if (LoginControl.isVisitorFlag()) {
            LoginControl.setUpgradeforpay(true);
            DialogUtil.showVisitorUpgradeTipDialog(mContext, ResourceUtils.getString(mContext, "ck_visitor_update_hint"), LoginControl.getVisitorName());
            return;
        }
        if (!checkPayParams(payParam)) {
            return;
        }
        payParam.setType(1);
        SdkPayRequestBean sdkPayRequestBean = new SdkPayRequestBean();
        sdkPayRequestBean.setOrderinfo(payParam);
        sdkPayRequestBean.setPay_ver("v2");
        sdkPayRequestBean.setRoleinfo(payParam.getRoleinfo());
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(sdkPayRequestBean));
        StringBuilder urlParams = httpParamsBuild.getHttpParams().getUrlParams();
        this.paymentListener = paymentListener;
        WebPayActivity.start(mContext, urlParams.toString(), payParam.getProduct_price(), payParam.getProduct_name(), httpParamsBuild.getAuthkey());
    }

    public void showRegist() {
        removeFloatView();
        CkLoginActivity.start(mContext, true);
    }


    private boolean checkPayParams(CustomPayParam payParam) {
        if (!BaseAppUtil.isNetWorkConneted(mContext)) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_link_error"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!LoginControl.isLogin()) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_login_first"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (payParam.getCp_order_id() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_product_orderid_empty"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (payParam.getProduct_price() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_productprice_empty"), Toast.LENGTH_SHORT).show();
            return false;
        }
        float price = payParam.getProduct_price();
        float tempPrice = price * 100;
        if (tempPrice - (int) tempPrice > 0) {
            payParam.setProduct_price((Float.valueOf((int) tempPrice)) / 100);
        }
        if (payParam.getProduct_id() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_productid_empty"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (payParam.getProduct_name() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_productname_empty"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (payParam.getExt() == null) {
            payParam.setExt("");
        }
        return checkRoleInfoParam(payParam.getRoleinfo());
    }

    public OnPaymentListener getPaymentListener() {
        return paymentListener;
    }

    @NotProguard
    public void setRoleInfo(RoleInfo roleInfo, final SubmitRoleInfoCallBack submitRoleInfoCallBack) {
        if (!BaseAppUtil.isNetWorkConneted(mContext)) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_link_error"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!LoginControl.isLogin()) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_login_first"), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkRoleInfoParam(roleInfo)) {
            return;
        }
        UproleinfoRequestBean uproleinfoRequestBean = new UproleinfoRequestBean();
        uproleinfoRequestBean.setRoleinfo(roleInfo);
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(uproleinfoRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<String>(mContext, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(String data) {
                submitRoleInfoCallBack.submitSuccess();
                Log.e("setRoleinfo", "success");
            }

            @Override
            public void onFailure(String code, String msg) {
                super.onFailure(code, msg);
                if (!TextUtils.isEmpty(msg)) {
                    submitRoleInfoCallBack.submitFail(msg);
                } else {
                    submitRoleInfoCallBack.submitFail(ResourceUtils.getString(mContext, "upload_roleinfo_fail"));
                }
                Log.e("setRoleinfo", "fail：" + code + "  " + msg);
            }
        };
        httpCallbackDecode.setShowTs(false);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(false);
        RxVolley.post(SdkApi.getUproleinfo(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    private boolean checkRoleInfoParam(RoleInfo roleInfo) {
        if (roleInfo == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_roleinfo_empty"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (roleInfo.getRole_type() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_roletype_empty_err"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (roleInfo.getServer_id() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_serverid_empty_err"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (roleInfo.getServer_name() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_servername_empty_err"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (roleInfo.getRole_id() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_roleid_empty_err"), Toast.LENGTH_SHORT).show();
            return false;

        }
        if (roleInfo.getRole_name() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_rolename_empty_err"), Toast.LENGTH_SHORT).show();
            return false;

        }
        if (roleInfo.getParty_name() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_partyname_empty_err"), Toast.LENGTH_SHORT).show();
            return false;

        }

        if (roleInfo.getRole_level() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_rolelevel_empty_err"), Toast.LENGTH_SHORT).show();
            return false;

        }
        if (roleInfo.getRole_vip() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_viplevel_empty_err"), Toast.LENGTH_SHORT).show();
            return false;

        }
        if (roleInfo.getRole_balence() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_rolebalance_empty_err"), Toast.LENGTH_SHORT).show();
            return false;

        }
        if (roleInfo.getRolelevel_ctime() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_rolecreatetime_empty_err"), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (roleInfo.getRolelevel_mtime() == null) {
            Toast.makeText(mContext, ResourceUtils.getString(mContext, "ck_rolelevel_changetime_empty_err"), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 显示浮标
     */
    @NotProguard
    public void showFloatView() {
        if (!LoginControl.isLogin() || !loginflag) {
            return;
        }
        FloatViewManager.getInstance(mContext).showFloat();
        boolean floatWindowOpAllowed = HLAppUtil.isFloatWindowOpAllowed(mContext);
        if (!floatWindowOpAllowed && (MiuiDeviceUtil.isMiui() || DeviceUtil.isMeizuFlymeOS() || DeviceUtil.IsHuaweiRom()
                || DeviceUtil.Is360Rom())) {
            new OpenFloatPermissionDialog().showDialog(mContext, true, null, new OpenFloatPermissionDialog.ConfirmDialogListener() {
                @Override
                public void ok() {
                    DeviceUtil.openSettingPermission(mContext);
                }

                @Override
                public void cancel() {
                }
            });
        }
    }

    /**
     * 隐藏浮标
     */
    @NotProguard
    public void removeFloatView() {
        try {
            FloatViewManager.getInstance(mContext).hideFloat();
            L.e(TAG, "浮点隐藏了");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 资源回收
     */
    @NotProguard
    public void recycle() {
        try {
            onLogoutListener = null;//登出监听置null
            logoutExecute(OnLogoutListener.TYPE_NORMAL_LOGOUT);
            // 移除浮标
            removeLoginListener(onLoginListener);
            LoginControl.clearLogin();
//            FloatViewManager.getInstance(mContext).removeFloat();
            Intent intent = new Intent(mContext, CKGameService.class);
            mContext.stopService(intent);
            mContext = null;
            loginflag = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户注册的登陆监听
     *
     * @return
     */
    public OnLoginListener getOnLoginListener() {
        return onLoginListener;
    }
}
