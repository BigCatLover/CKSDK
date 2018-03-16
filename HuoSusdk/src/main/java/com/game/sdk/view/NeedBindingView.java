package com.game.sdk.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.game.sdk.CKGameManager;
import com.game.sdk.db.LoginControl;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.WebRequestBean;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.ui.CkLoginActivity;
import com.game.sdk.ui.FloatWebActivity;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;

/**
 * Created by zhanglei on 2017/5/30.
 */

public class NeedBindingView extends FrameLayout implements View.OnClickListener {
    private CkLoginActivity loginActivity;
    private ViewStackManager viewStackManager;
    private TextView title;
    private TextView account;
    private TextView tip;
    private TextView binding;
    private TextView nobinding;
    private int type;
    public static final int LYG_REGIST = 0;
    public static final int LYG_LOGIN = 1;
    private OnLoginListener listener;

    public NeedBindingView(Context context) {
        super(context);
        setupUI();
    }

    public NeedBindingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public NeedBindingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    private void setupUI() {
        loginActivity = (CkLoginActivity) getContext();
        viewStackManager = ViewStackManager.getInstance(loginActivity);
        LayoutInflater.from(getContext()).inflate(MResource.getIdByName(getContext(), MResource.LAYOUT, "ck_include_need_binding"), this);

        title = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.title"));
        account = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.acountname"));
        tip = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.tip"));
        binding = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.goto_binding"));
        nobinding = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.no_binding"));

        binding.setText(ResourceUtils.getString(loginActivity, "ck_goto_binding"));
        nobinding.setText(ResourceUtils.getString(loginActivity, "ck_binding_cancle"));
//        String content="建议绑定手机或者邮箱以免游戏数据丢失无法找回";
//        String str="手机";
//        String str1 = "邮箱";
//        SpannableStringBuilder builder = new SpannableStringBuilder(content);
//        ForegroundColorSpan graySpan = new ForegroundColorSpan(ResourceUtils.getColorId(loginActivity,"background_green"));
//        //为不同位置字符串设置不同颜色
//        int start = content.indexOf(str);
//        int end = str.length();
//        int start1 = content.indexOf(str1);
//        int end1 = str1.length();
//        builder.setSpan(graySpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        builder.setSpan(graySpan, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tip.setText(content);

        binding.setOnClickListener(this);
        nobinding.setOnClickListener(this);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void switchUI(int type, String name) {
        account.setText(loginActivity.getString(ResourceUtils.getStringId("ck_loginsuccess_title"), name));
        this.type = type;
        switch (type) {
            case LYG_LOGIN:
                title.setText(ResourceUtils.getString(loginActivity, "ck_login_success"));
                break;
            case LYG_REGIST:
                title.setText(ResourceUtils.getString(loginActivity, "ck_regist_success"));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == binding.getId()) {
            HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(new WebRequestBean()));
            FloatWebActivity.start(loginActivity, SdkApi.getWebUser(), ResourceUtils.getString(loginActivity, "ck_usercenter"), httpParamsBuild.getHttpParams().getUrlParams().toString(),
                    httpParamsBuild.getAuthkey(), 1);
        } else if (v.getId() == nobinding.getId()) {
            listener = CKGameManager.getInstance().getOnLoginListener();
            if (listener != null) {
                listener.loginSuccess(new LogincallBack(LoginControl.getMemid(), LoginControl.getUserToken()));
            }
            loginActivity.callBackFinish();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
