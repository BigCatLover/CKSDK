package mytest.zl.com.paytest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sdk.CKGameManager;
import com.game.sdk.domain.CustomPayParam;
import com.game.sdk.domain.GameBean;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.RoleInfo;
import com.game.sdk.listener.OnGetGamesListener;
import com.game.sdk.listener.OnInitSdkListener;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.listener.OnLogoutListener;
import com.game.sdk.listener.OnPaymentListener;
import com.game.sdk.log.T;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener{
    private Button regeist,pay;
    private EditText money,registcnt,mau;
    private TextView payinfo;
    public static final String TABLENAME = "userlist_pay";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private Spinner gamelist;
    CKGameManager sdkManager;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private List<GameBean> gameList = new ArrayList<>();
    private List<UserInfo> lastUserlist = new ArrayList<>();
    private List<UserInfo> tempList = new ArrayList<>();
    private DBHelper dbHelper = null;
    private int cnt=0,finishcnt,maup=0,all,logincnt=0;//flag=0
    private String payaccount="",paygame;
    private boolean isfirst=false;
    private StringBuilder userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isfirst=true;
        setContentView(R.layout.paytest_activity_main);
        regeist = (Button) findViewById(R.id.regist);
        money = (EditText) findViewById(R.id.money);
        registcnt = (EditText) findViewById(R.id.regstcnt);
        mau = (EditText) findViewById(R.id.mau);
        pay = (Button) findViewById(R.id.pay);
        gamelist = (Spinner)findViewById(R.id.gamelist);
        payinfo = (TextView)findViewById(R.id.payinfo);
        regeist.setOnClickListener(this);
        pay.setOnClickListener(this);
        userinfo = new StringBuilder();
//        flag = addInstallOpenCnt(this);
//        cnt = getCnt(this);
//        if(flag!=1){
//            regeist.setEnabled(false);
//            registcnt.setText(cnt+"");
//            registcnt.setEnabled(false);
//        }

        dbHelper = new DBHelper(this, null, 2);
        sdkManager = CKGameManager.getInstance();
        sdkManager.setDebug(true);
        getGameList();
    }

    private void doSDKInit(){
        //sdk初始化
        sdkManager.initSdk(this, new OnInitSdkListener() {
            @Override
            public void initSuccess(String code, String msg) {
                List<UserInfo> userInfoList = getUserLoginInfo(TABLENAME);
                if(isfirst){
                    isfirst=false;
                    return;
                }else if(userInfoList.size()>1){
                    Random random = new Random();
                    if(cnt>0){
                        int i = random.nextInt(cnt);
                        String name = userInfoList.get(i).username;
                        String pass = userInfoList.get(i).password;
                        sdkManager.AutoRegist(name,pass,true);
                    }
                }

            }

            @Override
            public void initError(String code, String msg) {
                T.s(MainActivity.this, msg);
            }
        });
        //添加sdk登陆监听,包含正常登陆，切换账号登陆，登陆过期后重新登陆
        sdkManager.addLoginListener(new OnLoginListener() {
            @Override
            public void loginSuccess( LogincallBack logincBack) {
                Log.e("zll",finishcnt+" "+cnt);
                saveUserInfo(TABLENAME,logincBack.mem_id,logincBack.user_token);
                if(finishcnt<cnt){
                    Add(logincBack.mem_id,logincBack.user_token);
                    sdkManager.AutoRegist(null,null,false);
                    finishcnt++;
                    regeist.setText("总数："+all+"("+"注册数："+cnt+"/ 登录数："+logincnt+")"+"   "+"已完成数:"+finishcnt);
                }else if(finishcnt<all){
                    int index = finishcnt-cnt;
                    String name = tempList.get(index).username;
                    String password = tempList.get(index).password;
                    sdkManager.AutoRegist(name,password,true);
                    finishcnt++;
                    regeist.setText("总数："+all+"("+"注册数："+cnt+"/ 登录数："+logincnt+")"+"   "+"已完成数:"+finishcnt);
                }else {
                    saveFile();
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
                Log.e("zl","登出成功，类型type="+type+" code="+code+" msg="+msg);
                if(type==OnLogoutListener.TYPE_NORMAL_LOGOUT){//正常退出成功
                    Toast.makeText(MainActivity.this,"退出成功",Toast.LENGTH_SHORT).show();
                }
                if(type==OnLogoutListener.TYPE_SWITCH_ACCOUNT){//切换账号退出成功
                    //游戏此时可跳转到登陆页面，让用户进行切换账号
//                    Toast.makeText(MainActivity.this,"退出登陆",Toast.LENGTH_SHORT).show();

                }
                if(type==OnLogoutListener.TYPE_TOKEN_INVALID){//登陆过期退出成功
                    //游戏此时可跳转到登陆页面，让用户进行重新登陆
                    sdkManager.showLogin();
                }
            }

            @Override
            public void logoutError(int type, String code, String msg) {
                Log.e("zl","登出失败，类型type="+type+" code="+code+" msg="+msg);
                if(type==OnLogoutListener.TYPE_NORMAL_LOGOUT){//正常退出失败

                }
                if(type==OnLogoutListener.TYPE_SWITCH_ACCOUNT){//切换账号退出失败

                }
                if(type==OnLogoutListener.TYPE_TOKEN_INVALID){//登陆过期退出失败

                }
            }
        });
    }

    public void Add(String name,String pass) {
        Log.e("zll","add "+name+" "+pass);
        userinfo.append("account:").append(name).append("\n").append("password:").append(pass).append("\n");
    }

    public void saveFile(){
        Log.e("zll","save "+userinfo.toString());
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // SD卡根目录的hello.text
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "LYGUser.txt";
        } else  // 系统下载缓存根目录的hello.text
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "LYGUser.txt";

        try {
            File file = new File(filePath);
            if(file.exists()){
                file.delete();
            }
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(userinfo.toString().getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGameList(){
        Log.e("zl","getGameList");
        data_list = new ArrayList<>();
        //todo 获取游戏列表（每个游戏 包含  APPID clientID clientkey 游戏名称，游戏名称添加至data_list）
        sdkManager.getGameList(this,new OnGetGamesListener() {
            @Override
            public void getFinish(List<GameBean> list) {
                gameList=list;
                for(int i=0;i<list.size();i++){
                    data_list.add(list.get(i).getName());
                }
                InitGameList();
            }

            @Override
            public void getError(String s, String s1) {
                Toast.makeText(MainActivity.this,s1+"code "+s,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void InitGameList(){
        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gamelist.setAdapter(arr_adapter);
        gamelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner=(Spinner) parent;
                Log.e("zl","selected game :"+spinner.getItemAtPosition(position)+" "+position);
                paygame = gameList.get(position).getName();
                sdkManager.setGameParams(gameList.get(position).getApp_id(),gameList.get(position).getClient_id(),
                        gameList.get(position).getClient_key());
                doSDKInit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "没有改变的处理", Toast.LENGTH_LONG).show();
            }

        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regist:
                finishcnt=0;
                String acc = registcnt.getText().toString().trim();
                String str1 = mau.getText().toString().trim();
                if(acc.isEmpty()||str1.isEmpty()){
                    Toast.makeText(MainActivity.this,"活跃数和注册数必填",Toast.LENGTH_SHORT).show();
                    return;
                }
                cnt = Integer.valueOf(acc);
                maup = Integer.valueOf(str1);

                if(maup>100||maup<0){
                    Toast.makeText(MainActivity.this,"活跃数必须大于0小于100",Toast.LENGTH_SHORT).show();
                    return;
                }
//                setCnt(MainActivity.this,cnt);
                lastUserlist.clear();
                tempList.clear();
                lastUserlist = getUserLoginInfo(TABLENAME);
                if(lastUserlist.size()>0){
                    Random random = new Random();
                    int flag = random.nextInt(2)+1;
                    if(flag%2==0){
                        for(int m=0;m<lastUserlist.size()*maup/100;m++){
                            tempList.add(lastUserlist.get(m));
                        }
                    }else {
                        for(int m=lastUserlist.size()-1;m+1>lastUserlist.size()*(100-maup)/100;m--){
                            tempList.add(lastUserlist.get(m));
                        }
                    }
                    logincnt=tempList.size();
                    clearUserLoginInfo(TABLENAME);
                    all = logincnt+cnt;
                }else{
                    all = cnt;
                }
                sdkManager.AutoRegist(null,null,false);
                break;
            case R.id.pay:
                String money_str = money.getText().toString().trim();
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

                        payinfo.setText("充值金额："+callbackInfo.money+" 充值账号："+payaccount+" 充值的游戏："+paygame);
                        // 弹出支付成功信息，一般不用
                        Toast.makeText(getApplication(), "充值金额数：" +
                                        callbackInfo.money + " 消息提示：" + callbackInfo.msg,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void paymentError(PaymentErrorMsg errorMsg) {
                        // TODO Auto-generated method stub
                        int code = errorMsg.code;
                        double money = errorMsg.money;
                        String msg = errorMsg.msg;
                        // 弹出支付失败信息，一般不用
                        Toast.makeText(getApplication(), "充值失败：code:" +
                                        errorMsg.code + "  ErrorMsg:" + errorMsg.msg +
                                        "  预充值的金额：" + errorMsg.money,
                                Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }

    public void clearUserLoginInfo(String name){
        List<UserInfo> userLogininfos =getUserLoginInfo(name);
        for(int i=userLogininfos.size()-1;i>-1;i--){
            deleteUserLoginByName(userLogininfos.get(i).username,name);
        }
    }

    public void deleteUserLoginByName(String username,String name) {
        SQLiteDatabase w_db = dbHelper.getWritableDatabase();
        if (w_db.isOpen()) {
            w_db.execSQL("delete from " + name + " where " + USERNAME
                    + "=?", new String[] { username });
        }
        w_db.close();
        w_db = null;
    }

    private RoleInfo initTestRoleInfo() {
        RoleInfo roleInfo = new RoleInfo();
        roleInfo.setRolelevel_ctime("" + System.currentTimeMillis() / 1000);
        roleInfo.setRolelevel_mtime("" + System.currentTimeMillis() / 1000);
        roleInfo.setParty_name("");
        roleInfo.setRole_balence(1.00f);
        roleInfo.setRole_id("Role_id");
        roleInfo.setRole_level(0);
        roleInfo.setRole_name("roleName");
        roleInfo.setRole_vip(0);
        roleInfo.setServer_id("Server_id");
        roleInfo.setServer_name("serverName");
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
//        sdkManager.showFloatView();
    }
    /**
     * 游戏一般在界面不可见时移除浮点
     */
    @Override
    protected void onStop() {
        super.onStop();
//        sdkManager.removeFloatView();
    }

    private int addInstallOpenCnt(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        int openCnt = sharedPreferences.getInt("login_flag", 0);
        if(openCnt+1==Integer.MAX_VALUE-100){//达到最大值
            openCnt=0;
        }
        sharedPreferences.edit().putInt("login_flag",openCnt+1).commit();


        return openCnt+1;
    }

    public void setCnt(Context context,int value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("login_cnt",value).commit();
    }



    public int getCnt(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        int cnt = sharedPreferences.getInt("login_cnt", 0);
        return cnt;
    }

    public void saveUserInfo(String name ,String username, String pwd) {
        SQLiteDatabase w_db = dbHelper.getWritableDatabase();
        if (w_db.isOpen()) {
            w_db.execSQL("insert into " + name + "(" + USERNAME + ","
                    + PASSWORD + ") values(?,?)", new Object[] { username,
                    "@" + pwd });
        }
        w_db.close();
        w_db = null;
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


}
