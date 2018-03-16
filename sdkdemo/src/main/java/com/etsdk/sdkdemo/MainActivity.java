package com.etsdk.sdkdemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.game.sdk.CKGameManager;
import com.game.sdk.domain.CustomPayParam;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.RoleInfo;
import com.game.sdk.domain.SubmitRoleInfoCallBack;
import com.game.sdk.listener.OnInitSdkListener;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.listener.OnLogoutListener;
import com.game.sdk.listener.OnPaymentListener;
import com.game.sdk.log.T;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener  {
    private static final String TAG = MainActivity.class.getSimpleName();
    Button btnTestLogin;
    EditText etTestMoney;
    Button btnTestCharger;
    Button btnTestSendRoleinfo;
    public static CKGameManager sdkManager;
    private Button btn_test_logout;
    private Button btn_test_switchAccount, logincheck;
    private String userid, token;
    private boolean islogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkPermission()){
            setupUI();
        }
        if (!isTaskRoot()) {
            finish();
            return;
        }
    }


    private String getMD5(String userid, String token) {
        try {
            String sSecret = "app_id=363178&mem_id=" + userid + "&user_token=" + token + "&app_key=be0e584cd96a9eaf0b4af68e4e96f114";
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();// 加密
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    //权限申请对话框
    private AlertDialog phoneInfoMD;
    private AlertDialog storageMD;
    private final List<AlertDialog> requestMD = new ArrayList<>();
    private final List<String> mPermissions = new ArrayList<>();
    public static final int PERMISSIONS_REQUEST_CODE = 200;
    public static final int SINGLE_PERMISSIONS_REQUEST_CODE = 201;
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            mPermissions.clear();
            requestMD.clear();
            int s = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            Log.e("zl","phone "+s) ;
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PERMISSION_GRANTED) {
                mPermissions.add(Manifest.permission.READ_PHONE_STATE);
                if (phoneInfoMD == null) {
                    phoneInfoMD = new AlertDialog.Builder(this)
                            .setMessage(R.string.a_0181)
                            .setTitle(R.string.a_0182)
                            .setPositiveButton(R.string.a_0133, this)
                            .setNegativeButton(R.string.a_0134, this)
                            .setCancelable(false)
                            .show();
                } else {
                    phoneInfoMD.show();
                }
                requestMD.add(phoneInfoMD);
                return false;
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                mPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (storageMD == null) {
                    storageMD = new AlertDialog.Builder(this)
                            .setMessage(R.string.a_0183)
                            .setTitle(R.string.a_0184)
                            .setPositiveButton(R.string.a_0133, this)
                            .setNegativeButton(R.string.a_0134, this)
                            .setCancelable(false)
                            .show();
                } else {
                    storageMD.show();
                }
                requestMD.add(storageMD);
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {//搜索权限申请
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    requestMD.get(i).show();
                    return;
                }
            }
            if (checkPermission()) {
                setupUI();
            }
        } else if (requestCode == SINGLE_PERMISSIONS_REQUEST_CODE) {//单个权限申请
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    requestMD.get(i).show();
                    return;
                }
            }
            if (checkPermission()) {
                setupUI();
            }
        }
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dialog == storageMD) {//存储空间
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SINGLE_PERMISSIONS_REQUEST_CODE);
            } else if (dialog == phoneInfoMD) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, SINGLE_PERMISSIONS_REQUEST_CODE);
            }
        }
    }



    private void setupUI() {
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this);
        btnTestLogin = (Button) findViewById(R.id.btn_test_login);
        etTestMoney = (EditText) findViewById(R.id.et_test_money);
        btnTestCharger = (Button) findViewById(R.id.btn_test_charger);
        btnTestSendRoleinfo = (Button) findViewById(R.id.btn_test_sendRoleinfo);
        btn_test_logout = (Button) findViewById(R.id.btn_test_logout);
        btn_test_switchAccount = (Button) findViewById(R.id.btn_test_switchAccount);
        logincheck = (Button) findViewById(R.id.btn_logincheck);
        btnTestCharger.setOnClickListener(this);
        logincheck.setOnClickListener(this);
        btnTestLogin.setOnClickListener(this);
        btnTestSendRoleinfo.setOnClickListener(this);
        btn_test_logout.setOnClickListener(this);
        btn_test_switchAccount.setOnClickListener(this);
        //获得sdk单例
        sdkManager = CKGameManager.getInstance();
        //sdk初始化
        sdkManager.initSdk(this, new OnInitSdkListener() {
            @Override
            public void initSuccess(String code, String msg) {
                Log.e(TAG, "initSdk=" + msg);
            }

            @Override
            public void initError(String code, String msg) {
                T.s(MainActivity.this, msg);
            }
        });
        //添加sdk登陆监听,包含正常登陆，切换账号登陆，登陆过期后重新登陆
        sdkManager.addLoginListener(new OnLoginListener() {
            @Override
            public void loginSuccess(LogincallBack logincBack) {
                //一般登陆成功后需要显示浮点
                islogin = true;
                userid = logincBack.mem_id;
                token = logincBack.user_token;
                sdkManager.showFloatView();
            }

            @Override
            public void loginError(LoginErrorMsg loginErrorMsg) {
                Log.e("lygame", " code=" + loginErrorMsg.code + "  msg=" + loginErrorMsg.msg);
            }
        });
        sdkManager.addLogoutListener(new OnLogoutListener() {
            @Override
            public void logoutSuccess(int type, String code, String msg) {
                Log.e("lygame", "登出成功，类型type=" + type + " code=" + code + " msg=" + msg);
                if (type == OnLogoutListener.TYPE_NORMAL_LOGOUT) {//正常退出成功
                    Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                }
                if (type == OnLogoutListener.TYPE_SWITCH_ACCOUNT) {//切换账号退出成功
                    //游戏此时可跳转到登陆页面，让用户进行切换账号
                    Toast.makeText(MainActivity.this, "切换账号", Toast.LENGTH_SHORT).show();

                }
                if (type == OnLogoutListener.TYPE_TOKEN_INVALID) {//登陆过期退出成功
                    //游戏此时可跳转到登陆页面，让用户进行重新登陆
                    sdkManager.showLogin();
                }
            }

            @Override
            public void logoutError(int type, String code, String msg) {
                Log.e("lygame", "登出失败，类型type=" + type + " code=" + code + " msg=" + msg);
                if (type == OnLogoutListener.TYPE_NORMAL_LOGOUT) {//正常退出失败

                }
                if (type == OnLogoutListener.TYPE_SWITCH_ACCOUNT) {//切换账号退出失败

                }
                if (type == OnLogoutListener.TYPE_TOKEN_INVALID) {//登陆过期退出失败

                }
            }
        });

    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //完成主界面更新,拿到数据
                    String data = (String) msg.obj;
                    T.s(MainActivity.this, data + "");
                    break;
                default:
                    break;
            }
        }

    };

    public void LoginCheck(final String userid, final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                try {
                    String url1 = "https://api-game.gank.tv/api/cp/user/check?app_id=363178&mem_id=" + userid + "&user_token=" + token
                            + "&sign=" + getMD5(userid, token);
                    URL url = new URL(url1);
                    connection = (HttpsURLConnection) url.openConnection();
                    // 设置请求方法，默认是GET
                    connection.setConnectTimeout(3000);     //设置连接超时时间
                    connection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
                    connection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
                    connection.setRequestMethod("POST");     //设置以Post方式提交数据
                    connection.setUseCaches(false);
                    // 设置字符集
                    connection.setRequestProperty("Charset", "UTF-8");
                    int response = connection.getResponseCode();
                    if (response == HttpURLConnection.HTTP_OK) {
                        InputStream inptStream = connection.getInputStream();
                        String s = dealResponseResult(inptStream);
                        Message msg = new Message();
                        msg.obj = s;//可以是基本类型，可以是对象，可以是List、map等；
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_test_login:
                sdkManager.showLogin();
                break;
            case R.id.btn_test_charger:
                String money_str = etTestMoney.getText().toString().trim();
                String money = "1";
                if (!TextUtils.isEmpty(money_str) && !"".equals(money_str)) {
                    money = money_str;
                }
                CustomPayParam customPayParam = new CustomPayParam();
                initTestParam(customPayParam, money);
                customPayParam.setRoleinfo(initTestRoleInfo());
                sdkManager.showPay(customPayParam, new OnPaymentListener() {
                    @Override
                    public void paymentSuccess(PaymentCallbackInfo callbackInfo) {
                        double money = callbackInfo.money;
                        String msg = callbackInfo.msg;

                        // 弹出支付成功信息，一般不用
                        Log.e("lygame", "pay success:" +
                                callbackInfo.money + " 消息提示：" + callbackInfo.msg);
                    }

                    @Override
                    public void paymentError(PaymentErrorMsg errorMsg) {
                        // TODO Auto-generated method stub
                        int code = errorMsg.code;
                        double money = errorMsg.money;
                        String msg = errorMsg.msg;
                        // 弹出支付失败信息，一般不用
                        Log.e("lygame", "充值失败：code:" +
                                errorMsg.code + "  ErrorMsg:" + errorMsg.msg +
                                "  预充值的金额：" + errorMsg.money);
                    }
                });
                break;
            case R.id.btn_test_sendRoleinfo:
                RoleInfo roleInfo = initTestRoleInfo();
                roleInfo.setRole_type(1);
                sdkManager.setRoleInfo(roleInfo, new SubmitRoleInfoCallBack() {
                    @Override
                    public void submitSuccess() {
                        Toast.makeText(MainActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void submitFail(String msg) {
                        T.s(MainActivity.this, msg);
                    }
                });
                break;
            case R.id.btn_test_logout:
                //调用此方法前请先设置登出监听
                sdkManager.logout();
                break;
            case R.id.btn_test_switchAccount:
                //切换账号会退出登陆，请在登出监听中接收切换退出结果
                sdkManager.switchAccount();
                break;
            case R.id.btn_logincheck:
                LoginCheck(userid, token);
                break;
        }
    }

    private RoleInfo initTestRoleInfo() {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRolelevel_ctime("" + System.currentTimeMillis() / 1000);
        roleInfo.setRolelevel_mtime("" + System.currentTimeMillis() / 1000);
        roleInfo.setParty_name("");
        roleInfo.setRole_balence(1.00f);
        roleInfo.setRole_id("11");
        roleInfo.setRole_level(1);
        roleInfo.setRole_name("candy");
        roleInfo.setRole_vip(0);
        roleInfo.setServer_id("11");
        roleInfo.setServer_name("捞月1区");
        return roleInfo;
    }

    private void initTestParam(CustomPayParam payParam, String money) {
        payParam.setCp_order_id("20161028111");
        payParam.setProduct_price(Float.parseFloat(money));
        payParam.setProduct_count(1);
        payParam.setProduct_id("1");
        payParam.setProduct_name("元宝");
        payParam.setProduct_desc("很好");
        payParam.setExchange_rate(1);
        payParam.setCurrency_name("金币");
        payParam.setExt("穿透");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 在游戏销毁时需要调用sdk的销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sdkManager.recycle();
    }

    /**
     * 游戏一般在界面可使用时显示浮点
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (islogin) {
            sdkManager.showFloatView();
        }
    }

    /**
     * 游戏一般在界面不可见时移除浮点
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (islogin) {
            sdkManager.removeFloatView();
        }
    }
}
