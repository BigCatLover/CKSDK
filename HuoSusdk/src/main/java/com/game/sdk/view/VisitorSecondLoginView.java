package com.game.sdk.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.game.sdk.CKGameManager;
import com.game.sdk.SdkConstant;
import com.game.sdk.db.LoginControl;
import com.game.sdk.domain.LoginResultBean;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.VisitorRequestBean;
import com.game.sdk.http.HttpCallbackDecode;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.ui.CkLoginActivity;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;
import com.kymjs.rxvolley.RxVolley;

/**
 * Created by zhanglei on 2017/5/30.
 */

public class VisitorSecondLoginView extends FrameLayout implements View.OnClickListener {
    private CkLoginActivity loginActivity;
    private ViewStackManager viewStackManager;
    private TextView upgrade;
    private TextView changeAccount;
    private TextView enterGame;
    private TextView accountname;

    public VisitorSecondLoginView(Context context) {
        super(context);
        setupUI();
    }

    public VisitorSecondLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public VisitorSecondLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    private void setupUI() {
        loginActivity = (CkLoginActivity) getContext();
        viewStackManager = ViewStackManager.getInstance(loginActivity);
        LayoutInflater.from(getContext()).inflate(MResource.getIdByName(getContext(), MResource.LAYOUT, "ck_include_second_login_visitor"), this);
        upgrade = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.upgrade_now"));
        changeAccount = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.change_acount"));
        accountname = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.accountname"));
        enterGame = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.entergame"));

        upgrade.setOnClickListener(this);
        changeAccount.setOnClickListener(this);
        enterGame.setOnClickListener(this);
        if (!LoginControl.getVisitorName().isEmpty()) {
            accountname.setText(LoginControl.getVisitorName());
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == upgrade.getId()) {
            PhoneRegisterView phoneRegistView = (PhoneRegisterView) viewStackManager.getViewByClass(PhoneRegisterView.class);
            if (phoneRegistView != null) {
                phoneRegistView.switchUI(PhoneRegisterView.LYG_VISITOR_UPGRATE);
                viewStackManager.addView(phoneRegistView);
            }
        } else if (v.getId() == enterGame.getId()) {
            VisitorLogin();
        } else if (v.getId() == changeAccount.getId()) {
            viewStackManager.addView(loginActivity.getLygFirstLoginView());
        }
    }

    private void VisitorLogin() {
        final VisitorRequestBean visitorloginBean = new VisitorRequestBean();
        visitorloginBean.setType(3);
        visitorloginBean.setUsername(SdkConstant.deviceBean.getDevice_id());
        visitorloginBean.setPassword("123456");
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(visitorloginBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<LoginResultBean>(loginActivity, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(LoginResultBean data) {
                if (data != null) {
                    viewStackManager.hiddenAllView();
                    OnLoginListener onLoginListener = CKGameManager.getInstance().getOnLoginListener();
                    if (onLoginListener != null) {
                        onLoginListener.loginSuccess(new LogincallBack(data.getMem_id(), data.getCp_user_token()));
                        DialogUtil.showVisitorSuccessDialog(loginActivity, data.getMem_id());
                        LoginControl.saveUserToken(data.getCp_user_token());
                        LoginControl.savaVisitorName(data.getMem_id());
                        CKGameManager.setSwitchFlag(false);
                        LoginControl.setVisitorFlag(data.getType() == 3);
                    }

                }
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(true);
        httpCallbackDecode.setLoadMsg(ResourceUtils.getString(loginActivity, "ck_login_ing"));
        RxVolley.post(SdkApi.getLogin(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
