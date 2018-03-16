package mytest.zl.com.paytest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sdk.CKGameManager;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.listener.OnInitSdkListener;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.listener.OnLogoutListener;
import com.game.sdk.log.T;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class LoginTestActivity extends Activity implements View.OnClickListener {
    private List<UserInfo> userlist = new ArrayList<>();
    private List<UserInfo> tempList = new ArrayList<>();
    private List<UserInfo> temp = new ArrayList<>();
    private Button login;
    private EditText count;
    private TextView finish;
    CKGameManager sdkManager;
    int flag;
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
    public static final String TABLENAME = "userlogin";
    public static final String TABLENAME_LAST = "userlogin_last";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private mytest.zl.com.paytest.DBHelper dbHelper = null;
    private static int finishcnt = 0;
    private int logincnt, registcnt, cnt_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_test);
        login = (Button) findViewById(R.id.login);
        count = (EditText) findViewById(R.id.cnt);
        finish = (TextView) findViewById(R.id.loginfinishcnt);
        dbHelper = new DBHelper(this, null, 2);
        count.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {//点击软键盘完成控件时触发的行为
                    //关闭光标并且关闭软键盘
                    count.setCursorVisible(false);
                    InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;//消费掉该行为
            }
        });
        login.setOnClickListener(this);
        sdkManager = CKGameManager.getInstance();
        flag = addInstallOpenCnt(this);
        Log.e("zl", "flag:" + flag);
        if (flag == 1) {
            login.setText("注册");
        } else {
            long currenttime = System.currentTimeMillis();
            long lasttime = Long.valueOf(getLogintime(this));
            if (currenttime > lasttime && !isSameDayOfMillis(currenttime, lasttime)) {
                login.setText("登录");
            } else {
                login.setText("登录");
            }
            count.setText(getCnt(this) + "");
        }
        doSDKInit();
    }

    private int addInstallOpenCnt(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        int openCnt = sharedPreferences.getInt("login_flag", 0);
        if (openCnt + 1 == Integer.MAX_VALUE - 100) {//达到最大值
            openCnt = 0;
        }
        sharedPreferences.edit().putInt("login_flag", openCnt + 1).commit();


        return openCnt + 1;
    }

    public void setCnt(Context context, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("login_cnt", value).commit();
    }


    public int getCnt(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        int cnt = sharedPreferences.getInt("login_cnt", 0);
        return cnt;
    }

    public void setLoginTime(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("login_time", value).commit();
    }

    public String getLogintime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String time = sharedPreferences.getString("login_time", "0");
        return time;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                Log.e("zl", "flag:" + flag);
                tempList.clear();
                finishcnt = 0;
                if (flag == 1) {
                    Log.e("zl", "regist in");
                    clearUserLoginInfo(TABLENAME);
                    long currenttime = System.currentTimeMillis();
                    setLoginTime(LoginTestActivity.this, currenttime + "");
                    String acc = count.getText().toString().trim();
                    int cnt = Integer.valueOf(acc);
                    setCnt(LoginTestActivity.this, cnt);
                    registcnt = cnt;
                    cnt_all = cnt;
                    sdkManager.AutoRegist(null, null, false);
                } else {
                    Log.e("zl", "login in");
                    temp.clear();
                    long currenttime = System.currentTimeMillis();
                    long lasttime = Long.valueOf(getLogintime(this));
                    if (currenttime > lasttime && !isSameDayOfMillis(currenttime, lasttime)) {
                        userlist = getUserLoginInfo(TABLENAME);
                        registcnt = getCnt(LoginTestActivity.this);
                        Log.e("zl", "last size:" + userlist.size() + " " + userlist.size() * 0.7);
                        if (userlist.size() > 0) {
                            if (flag % 2 == 0) {
                                for (int m = 0; m < userlist.size() * 0.7; m++) {
                                    tempList.add(userlist.get(m));
                                }
                            } else {
                                for (int m = userlist.size() - 1; m + 1 > userlist.size() * 0.3; m--) {
                                    tempList.add(userlist.get(m));
                                }
                            }
                            logincnt = tempList.size();
                        } else {
                        }
                        cnt_all = logincnt + registcnt;
                        sdkManager.AutoRegist(null, null, false);
                    } else {
                        finish.setText("请第二天再登录");
                        return;
                    }

                }
                break;
        }
    }

    public void saveUserInfo(String name, String username, String pwd) {
        SQLiteDatabase w_db = dbHelper.getWritableDatabase();
        if (w_db.isOpen()) {
            w_db.execSQL("insert into " + name + "(" + USERNAME + ","
                    + PASSWORD + ") values(?,?)", new Object[]{username,
                    "@" + pwd});
        }
        w_db.close();
        w_db = null;
    }

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        return false;
//        final long interval = ms1 - ms2;
//        return interval < MILLIS_IN_DAY
//                && interval > -1L * MILLIS_IN_DAY
//                && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    private void doSDKInit() {
        //sdk初始化
        sdkManager.initSdk(this, new OnInitSdkListener() {
            @Override
            public void initSuccess(String code, String msg) {
                Log.e("zl", "initSdk=" + msg);
            }

            @Override
            public void initError(String code, String msg) {
                T.s(LoginTestActivity.this, msg);
            }
        });
        //添加sdk登陆监听,包含正常登陆，切换账号登陆，登陆过期后重新登陆
        sdkManager.addLoginListener(new OnLoginListener() {
            @Override
            public void loginSuccess(LogincallBack logincBack) {
                finishcnt++;
                finish.setText("总数：" + cnt_all + "/" + "已完成数:" + finishcnt);
                Log.e("zl", finishcnt + " " + logincnt + " " + registcnt);
                //todo
                if (flag == 1) {
                    saveUserInfo(TABLENAME, logincBack.mem_id, logincBack.user_token);
                    if (finishcnt < registcnt) {
                        sdkManager.AutoRegist(null, null, false);
                    } else {
                        login.setText("登录");
                        flag = addInstallOpenCnt(LoginTestActivity.this);
                    }
//                    if(finishcnt==getCnt(LoginTestActivity.this)){
//                        login.setEnabled(false);
//                    }
                } else {
                    UserInfo user = new UserInfo();
                    user.username = logincBack.mem_id;
                    user.password = logincBack.user_token;
                    temp.add(user);
                    if (finishcnt < registcnt) {
                        sdkManager.AutoRegist(null, null, false);
                    } else if (finishcnt < cnt_all) {
                        int index = finishcnt - registcnt;
                        String name = tempList.get(index).username;
                        String password = tempList.get(index).password;
                        sdkManager.AutoRegist(name, password, true);
                    } else {
                        Log.e("zl", "22");
                        clearUserLoginInfo(TABLENAME);
                        for (int i = 0; i < temp.size(); i++) {
                            saveUserInfo(TABLENAME, temp.get(i).username, temp.get(i).password);
                        }
                    }
                }


            }

            @Override
            public void loginError(LoginErrorMsg loginErrorMsg) {
                Log.e("zl", " code=" + loginErrorMsg.code + "  msg=" + loginErrorMsg.msg);
            }
        });
        sdkManager.addLogoutListener(new OnLogoutListener() {
            @Override
            public void logoutSuccess(int type, String code, String msg) {
                Log.e("zl", "登出成功，类型type=" + type + " code=" + code + " msg=" + msg);
                if (type == OnLogoutListener.TYPE_NORMAL_LOGOUT) {//正常退出成功
                    Toast.makeText(LoginTestActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                }
                if (type == OnLogoutListener.TYPE_SWITCH_ACCOUNT) {//切换账号退出成功
                    //游戏此时可跳转到登陆页面，让用户进行切换账号
//                    Toast.makeText(MainActivity.this,"退出登陆",Toast.LENGTH_SHORT).show();

                }
                if (type == OnLogoutListener.TYPE_TOKEN_INVALID) {//登陆过期退出成功
                    //游戏此时可跳转到登陆页面，让用户进行重新登陆
                    sdkManager.showLogin();
                }
            }

            @Override
            public void logoutError(int type, String code, String msg) {
                Log.e("zl", "登出失败，类型type=" + type + " code=" + code + " msg=" + msg);
                if (type == OnLogoutListener.TYPE_NORMAL_LOGOUT) {//正常退出失败

                }
                if (type == OnLogoutListener.TYPE_SWITCH_ACCOUNT) {//切换账号退出失败

                }
                if (type == OnLogoutListener.TYPE_TOKEN_INVALID) {//登陆过期退出失败

                }
            }
        });
    }

    public List<UserInfo> getUserLoginInfo(String name) {
        List<UserInfo> userLogininfos = null;
        SQLiteDatabase r_db = dbHelper.getReadableDatabase();
        if (r_db.isOpen()) {
            Cursor cursor = r_db.rawQuery("select * from " + name, null);
            userLogininfos = new ArrayList<>();
            UserInfo ulinfo = null;

            String username;
            String pwd;
            try {
                if (cursor.moveToLast()) {
                    ulinfo = new UserInfo();
                    username = cursor
                            .getString(cursor.getColumnIndex(USERNAME));
                    pwd = cursor.getString(cursor.getColumnIndex(PASSWORD));

                    ulinfo.username = username;
                    ulinfo.password = pwd;
                    ulinfo.password = ulinfo.password.substring(1,
                            ulinfo.password.length());
                    userLogininfos.add(ulinfo);
                }
            } catch (Exception e) {

            }

            while (cursor.moveToPrevious()) {
                ulinfo = new UserInfo();
                username = cursor.getString(cursor.getColumnIndex(USERNAME));
                pwd = cursor.getString(cursor.getColumnIndex(PASSWORD));
                // isrepwd=cursor.getInt(cursor.getColumnIndex(ISREPWD));
                ulinfo.username = username;
                ulinfo.password = pwd;
                ulinfo.password = ulinfo.password.substring(1,
                        ulinfo.password.length());// "0000123 变成123问题"
                userLogininfos.add(ulinfo);
                ulinfo = null;
            }
            cursor.close();
        }
        r_db.close();
        r_db = null;
        return userLogininfos;
    }

    public void clearUserLoginInfo(String name) {
        List<UserInfo> userLogininfos = getUserLoginInfo(name);
        for (int i = userLogininfos.size() - 1; i > -1; i--) {
            deleteUserLoginByName(userLogininfos.get(i).username, name);
        }
    }

    public void deleteUserLoginByName(String username, String name) {
        SQLiteDatabase w_db = dbHelper.getWritableDatabase();
        if (w_db.isOpen()) {
            w_db.execSQL("delete from " + name + " where " + USERNAME
                    + "=?", new String[]{username});
        }
        w_db.close();
        w_db = null;
    }
}
