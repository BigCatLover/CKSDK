package com.game.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.game.sdk.CKGameManager;
import com.game.sdk.db.LoginControl;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.util.MResource;
import com.game.sdk.view.FirstLoginView;
import com.game.sdk.view.LoginView;
import com.game.sdk.view.NeedBindingView;
import com.game.sdk.view.PhoneRegisterView;
import com.game.sdk.view.UserNameRegistView;
import com.game.sdk.view.ViewStackManager;
import com.game.sdk.view.VisitorSecondLoginView;

import java.util.List;

public class CkLoginActivity extends Activity {
    private final static int CODE_LOGIN_FAIL=-1;//登陆失败
    private final static int CODE_LOGIN_CANCEL=-2;//用户取消登陆
    private boolean isRegist;
    FirstLoginView firstLoginView;
    PhoneRegisterView phoneRegisterView;
    UserNameRegistView userNameRegistView;
    NeedBindingView needBindingView;
    LoginView loginView;
    private boolean isVisitor;
    VisitorSecondLoginView visitorSecondLoginView;

    private ViewStackManager viewStackManager;
    private boolean callBacked;//是否已经回调过了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(this,MResource.LAYOUT,"ck_activity_login"));
        isRegist = getIntent().getBooleanExtra("isRegist",false);
        setupUI();
    }

    private void setupUI() {
        callBacked=false;
        viewStackManager=ViewStackManager.getInstance(this);
        loginView = (LoginView) findViewById(MResource.getIdByName(this, "R.id.lyg_sdk_loginView"));
        firstLoginView = (FirstLoginView) findViewById(MResource.getIdByName(this, "R.id.lyg_sdk_firstLoginView"));
        phoneRegisterView = (PhoneRegisterView) findViewById(MResource.getIdByName(this, "R.id.lyg_sdk_phoneRegistView"));
        userNameRegistView = (UserNameRegistView) findViewById(MResource.getIdByName(this, "R.id.lyg_sdk_userNameRegisterView"));
        visitorSecondLoginView = (VisitorSecondLoginView) findViewById(MResource.getIdByName(this, "R.id.lyg_sdk_visitorSecondLoginView"));
        needBindingView = (NeedBindingView) findViewById(MResource.getIdByName(this, "R.id.lyg_sdk_needbinding"));
        viewStackManager.addBackupView(loginView);
        viewStackManager.addBackupView(firstLoginView);
        viewStackManager.addBackupView(needBindingView);
        viewStackManager.addBackupView(phoneRegisterView);
        viewStackManager.addBackupView(userNameRegistView);
        viewStackManager.addBackupView(visitorSecondLoginView);

        switchUI();
    }
    public void switchUI(){
        List<UserInfo> userInfo = UserLoginInfodao.getInstance(this).getUserLoginInfo();
        if(isRegist) {
            LoginControl.setUpgradeforpay(false);
            PhoneRegisterView phoneRegistView = (PhoneRegisterView) viewStackManager.getViewByClass(PhoneRegisterView.class);
            if (phoneRegistView != null) {
                phoneRegistView.switchUI(PhoneRegisterView.LYG_VISITOR_UPGRATE_PAY);
                viewStackManager.addView(phoneRegistView);
            }
        }else {
            if (CKGameManager.getSwitchFlag()) {
                viewStackManager.addView(visitorSecondLoginView);
            } else if (userInfo != null && !userInfo.isEmpty()) {
                viewStackManager.addView(loginView);
            } else if (!LoginControl.getVisitorName().isEmpty()) {
                viewStackManager.addView(visitorSecondLoginView);
            } else {
                viewStackManager.addView(firstLoginView);
            }
        }

    }
    @Override
    public void onBackPressed() {
        if(viewStackManager.getCurrentView() == firstLoginView){
            viewStackManager.clear();
            super.onBackPressed();
        }else{
            viewStackManager.removeTopView();
        }
    }

    public LoginView getLygLoginView() {
        return loginView;
    }

    public FirstLoginView getLygFirstLoginView() {
        return firstLoginView;
    }

    public PhoneRegisterView getLygPhoneRegistView() {
        return phoneRegisterView;
    }


    public UserNameRegistView getUserNameRegistView() {
        return userNameRegistView;
    }

    public VisitorSecondLoginView getVisitorSecondLoginView() {
        return visitorSecondLoginView;
    }
    public NeedBindingView getNeedBindingView() {
        return needBindingView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(CKGameManager.isUpgradeWhenPay){
            CKGameManager.setIsUpgradeWhenPay(false);
        }else{
            if(!callBacked){//还没有回调过，是用户取消登陆
                LoginErrorMsg loginErrorMsg=new LoginErrorMsg(CODE_LOGIN_CANCEL,"用户取消登陆");
                OnLoginListener onLoginListener = CKGameManager.getInstance().getOnLoginListener();
                if(onLoginListener!=null){
                    onLoginListener.loginError(loginErrorMsg);
                }
            }
        }
        viewStackManager.clear();

    }

    /**
     * 通知回调成功并关闭activity
     */
    public void callBackFinish(){
        CKGameManager.setLoginflag(true);
        this.callBacked=true;
        if(CKGameManager.getInstance().getAdList()!=null&&!CKGameManager.getInstance().getAdList().isEmpty()){
            AdActivity.start(CkLoginActivity.this, CKGameManager.getInstance().getAdList().size());
        }else{
            CKGameManager.getInstance().showFloatView();
            finish();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
    public static void start(Context context,boolean isRegist) {
        Intent starter = new Intent(context, CkLoginActivity.class);
        starter.putExtra("isRegist",isRegist);
        starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(context instanceof Activity){
            ((Activity)context).overridePendingTransition(0, 0);
        }
        context.startActivity(starter);
    }
}
