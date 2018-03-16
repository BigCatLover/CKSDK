package com.game.sdk.view;

/**
 * Created by zhanglei on 2017/5/30.
 */
import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.sdk.CKGameManager;
import com.game.sdk.SdkConstant;
import com.game.sdk.db.LoginControl;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.RegisterResultBean;
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


public class FirstLoginView extends FrameLayout implements View.OnClickListener {
    private CkLoginActivity loginActivity;
    private ViewStackManager viewStackManager;
    private TextView fastRegist;
    private TextView login;
    private TextView visitorplay;
    private ImageView close;
    public FirstLoginView(Context context) {
        super(context);
        setupUI();
    }

    public FirstLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public FirstLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }
    private void setupUI() {
        loginActivity = (CkLoginActivity) getContext();
        viewStackManager = ViewStackManager.getInstance(loginActivity);
        LayoutInflater.from(getContext()).inflate(MResource.getIdByName(getContext(), MResource.LAYOUT, "ck_include_firstlogin"), this);
        fastRegist = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.fastregist"));
        login = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.acountlogin"));
        visitorplay = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.visitorplay"));
        close= (ImageView) findViewById(MResource.getIdByName(getContext(),"R.id.close"));

        fastRegist.setOnClickListener(this);
        login.setOnClickListener(this);
        visitorplay.setOnClickListener(this);
        close.setOnClickListener(this);
    }
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==fastRegist.getId()){
            PhoneRegisterView phoneRegistView = (PhoneRegisterView) viewStackManager.getViewByClass(PhoneRegisterView.class);
            if(phoneRegistView!=null){
                phoneRegistView.switchUI(PhoneRegisterView.LYG_PHONE_REGIST);
                viewStackManager.addView(phoneRegistView);
            }
        }else if(v.getId()==login.getId()){
            viewStackManager.addView(loginActivity.getLygLoginView());
        }else if(v.getId()==visitorplay.getId()){
            if(!LoginControl.getVisitorName().isEmpty()){
                VisitorLogin();
            }else{
                VisitorRegist();
            }
        }else if(v.getId()==close.getId()){
            loginActivity.finish();
        }
    }

    private void VisitorRegist(){
        VisitorRequestBean visitorRegisterRequestBean=new VisitorRequestBean();
        visitorRegisterRequestBean.setType(3);
        visitorRegisterRequestBean.setUsername(SdkConstant.deviceBean.getDevice_id());
        visitorRegisterRequestBean.setPassword("123456");
        visitorRegisterRequestBean.setIntroducer("");
        HttpParamsBuild httpParamsBuild=new HttpParamsBuild(GsonUtil.getGson().toJson(visitorRegisterRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<RegisterResultBean>(loginActivity, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(RegisterResultBean data) {
                if(data!=null){
                    OnLoginListener onLoginListener = CKGameManager.getInstance().getOnLoginListener();
                    if(onLoginListener!=null){
                        onLoginListener.loginSuccess(new LogincallBack(data.getMem_id(),data.getCp_user_token()));
                        viewStackManager.hiddenAllView();
                        DialogUtil.showVisitorSuccessDialog(loginActivity,data.getMem_id());
                        LoginControl.savaVisitorName(data.getMem_id());
                        LoginControl.saveUserToken(data.getCp_user_token());
                        LoginControl.setVisitorFlag(data.getType()==3);
                        LoginControl.savaVisitorName(data.getMem_id());
                    }
                }
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(true);
        httpCallbackDecode.setLoadMsg(ResourceUtils.getString(loginActivity,"ck_regist_ing"));
        RxVolley.post(SdkApi.getRegister(), httpParamsBuild.getHttpParams(),httpCallbackDecode);
    }

    private void VisitorLogin(){
        viewStackManager.addView(loginActivity.getVisitorSecondLoginView());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
