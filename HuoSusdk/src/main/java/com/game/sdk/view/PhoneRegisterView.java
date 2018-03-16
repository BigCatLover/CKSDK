package com.game.sdk.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.game.sdk.CKGameManager;
import com.game.sdk.db.LoginControl;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.RegisterMobileRequestBean;
import com.game.sdk.domain.RegisterResultBean;
import com.game.sdk.domain.SmsSendRequestBean;
import com.game.sdk.domain.SmsSendResultBean;
import com.game.sdk.http.HttpCallbackDecode;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.log.T;
import com.game.sdk.ui.CkAgreeActivity;
import com.game.sdk.ui.CkLoginActivity;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.RegExpUtil;
import com.game.sdk.util.ResourceUtils;
import com.kymjs.rxvolley.RxVolley;

/**
 * Created by zhanglei on 2017/5/30.
 */

public class PhoneRegisterView extends FrameLayout implements View.OnClickListener {
    private CkLoginActivity loginActivity;
    private ViewStackManager viewStackManager;
    private EditText phone;
    private EditText code;
    private EditText password;
    private TextView getcode;
    private TextView next;
    private RelativeLayout other;
    private ImageView ivreturn;
    private ImageView phoneDele;
    private ImageView codeDele;
    private ImageView passDele;
    private TextView agree;
    private CheckBox eye;
    private TextView title;
    private int type = 0;
    private String smsType = "";
    final public static int LYG_PHONE_REGIST = 0;
    final public static int LYG_VISITOR_UPGRATE = 1;
    final public static int LYG_VISITOR_UPGRATE_PAY = 2;

    public PhoneRegisterView(Context context) {
        super(context);
        setupUI();
    }

    public PhoneRegisterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public PhoneRegisterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    private void setupUI() {
        loginActivity = (CkLoginActivity) getContext();
        viewStackManager = ViewStackManager.getInstance(loginActivity);
        LayoutInflater.from(getContext()).inflate(MResource.getIdByName(getContext(), MResource.LAYOUT, "ck_include_mobile_regist"), this);
        phone = (EditText) findViewById(MResource.getIdByName(loginActivity, "R.id.fastregist_acountname"));
        code = (EditText) findViewById(MResource.getIdByName(loginActivity, "R.id.fastregist_code"));
        getcode = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.getcode"));
        password = (EditText) findViewById(MResource.getIdByName(loginActivity, "R.id.password"));
        next = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.next"));
        ivreturn = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.iv_return"));
        other = (RelativeLayout) findViewById(MResource.getIdByName(getContext(), "R.id.other_regist"));
        eye = (CheckBox) findViewById(MResource.getIdByName(getContext(), "R.id.eye"));
        title = (TextView) findViewById(MResource.getIdByName(getContext(), "R.id.title"));
        passDele = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.pass_dele"));
        phoneDele = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.phone_dele"));
        codeDele = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.code_dele"));
        agree = (TextView) findViewById(MResource.getIdByName(getContext(), "R.id.agree"));

        agree.setText(ResourceUtils.getString(loginActivity, "ck_agreed"));
        agree.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        String click_text = ResourceUtils.getString(loginActivity, "ck_protocol_title");


        SpannableString spStr = new SpannableString(click_text);

        spStr.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(0xff19ac93);       //设置文件颜色
                ds.setUnderlineText(false);      //设置下划线
            }

            @Override
            public void onClick(View widget) {
                loginActivity.startActivity(new Intent(loginActivity, CkAgreeActivity.class));
            }
        }, 0, click_text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        agree.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        agree.append(spStr);
        agree.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件

        getcode.setOnClickListener(this);
        other.setOnClickListener(this);
        next.setOnClickListener(this);
        ivreturn.setOnClickListener(this);
        eye.setOnClickListener(this);
        passDele.setOnClickListener(this);
        codeDele.setOnClickListener(this);
        phoneDele.setOnClickListener(this);
        phone.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (phone.getText().toString().trim().length() > 0) {
                        phoneDele.setVisibility(VISIBLE);
                        phoneDele.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_delete"));
                    } else {
                        phoneDele.setVisibility(GONE);
                    }
                } else {
                    hideKeyboard(phone);
                    phoneDele.setVisibility(GONE);
                }
            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    phoneDele.setVisibility(VISIBLE);
                    phoneDele.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_delete"));
                } else {
                    phoneDele.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        code.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (code.getText().toString().trim().length() > 0) {
                        codeDele.setVisibility(VISIBLE);
                        codeDele.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_delete"));
                    } else {
                        codeDele.setVisibility(GONE);
                    }
                } else {
                    hideKeyboard(code);
                    codeDele.setVisibility(GONE);
                }
            }
        });
        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    codeDele.setVisibility(VISIBLE);
                    codeDele.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_delete"));
                } else {
                    codeDele.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (password.getText().toString().trim().length() > 0) {
                        passDele.setVisibility(VISIBLE);
                        passDele.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_delete"));
                    } else {
                        passDele.setVisibility(GONE);
                    }
                } else {
                    hideKeyboard(password);
                    passDele.setVisibility(GONE);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    passDele.setVisibility(VISIBLE);
                    passDele.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_delete"));
                } else {
                    passDele.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone.setText("");
        code.setText("");
        password.setText("");
    }

    private void hideKeyboard(View view) {
        //隐藏键盘
        InputMethodManager imm = (InputMethodManager) loginActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == eye.getId()) {
            if (eye.isChecked()) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                password.setSelection(password.getText().toString().trim().length());
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                password.setSelection(password.getText().toString().trim().length());
            }
        } else if (v.getId() == getcode.getId()) {
            sendSms();
        } else if (v.getId() == next.getId()) {
            submitRegister();
        } else if (v.getId() == ivreturn.getId()) {
            phone.setText("");
            code.setText("");
            password.setText("");
            viewStackManager.removeTopView();
        } else if (v.getId() == other.getId()) {
            viewStackManager.addView(loginActivity.getUserNameRegistView());
        } else if (v.getId() == passDele.getId()) {
            password.setText("");
        } else if (v.getId() == phoneDele.getId()) {
            phone.setText("");
        } else if (v.getId() == codeDele.getId()) {
            code.setText("");
        }
    }

    private void sendSms() {
        final String account = phone.getText().toString().trim();
        if (!RegExpUtil.isMobileNumber(account)) {
            T.s(loginActivity, ResourceUtils.getString(loginActivity, "ck_err_phonenum"));
            return;
        }
//        if(UserLoginInfodao.getInstance(loginActivity).findUserLoginInfoByName(account)){
//            T.s(loginActivity, "手机号已被注册");
//            return;
//        }
        SmsSendRequestBean smsSendRequestBean = new SmsSendRequestBean();
        smsSendRequestBean.setMobile(account);
        smsSendRequestBean.setSmstype(SmsSendRequestBean.TYPE_REGISTER);
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(smsSendRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<SmsSendResultBean>(loginActivity, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(SmsSendResultBean data) {
                if (data != null) {
                    //开始计时控件
                    startCodeTime(60);
                }
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(true);
        httpCallbackDecode.setLoadMsg(ResourceUtils.getString(loginActivity, "ck_sending"));
        RxVolley.post(SdkApi.getSmsSend(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    Handler handler = new Handler();

    private void startCodeTime(int time) {
        getcode.setTag(time);
        if (time <= 0) {
            getcode.setText(ResourceUtils.getString(loginActivity, "ck_getcode"));
            getcode.setClickable(true);
            return;
        } else {
            getcode.setClickable(false);
            getcode.setText(time + "秒");
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int delayTime = (int) getcode.getTag();
                startCodeTime(--delayTime);

            }
        }, 1000);
    }

    private void submitRegister() {
        final String account = phone.getText().toString().trim();
        final String pass = password.getText().toString().trim();
        int registType = 0;
        if (type == LYG_PHONE_REGIST) {
            registType = 1;
        } else {
            registType = 3;
        }
        String authCode = code.getText().toString().trim();
        if (!RegExpUtil.isMobileNumber(account)) {
            T.s(loginActivity, ResourceUtils.getString(loginActivity, "ck_err_phonenum"));
            return;
        }

        if (pass.length() < 6) {
            T.s(loginActivity, ResourceUtils.getString(loginActivity, "ck_password_too_short"));
            return;
        }

        if (!RegExpUtil.isMatchPassword(pass)) {
            T.s(loginActivity, ResourceUtils.getString(loginActivity, "ck_password_err"));
            return;
        }
        if (TextUtils.isEmpty(authCode)) {
            T.s(loginActivity, ResourceUtils.getString(loginActivity, "ck_input_yzm"));
            return;
        }
        if (type == LYG_PHONE_REGIST) {
            smsType = SmsSendRequestBean.TYPE_REGISTER;
        } else if (type == LYG_VISITOR_UPGRATE) {
            smsType = SmsSendRequestBean.TYPE_UPDATE_PWD;
        } else {

        }
        RegisterMobileRequestBean registerMobileRequestBean = new RegisterMobileRequestBean();
        registerMobileRequestBean.setMobile(account);
        registerMobileRequestBean.setPassword(pass);
        registerMobileRequestBean.setSmscode(authCode);
        registerMobileRequestBean.setIntroducer("");
        registerMobileRequestBean.setType(registType);
        registerMobileRequestBean.setUpdate(type == LYG_PHONE_REGIST ? RegisterMobileRequestBean.TYPE_REGIST : RegisterMobileRequestBean.TYPE_UPGRADE);
        registerMobileRequestBean.setSmstype(SmsSendRequestBean.TYPE_REGISTER);
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(registerMobileRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<RegisterResultBean>(loginActivity, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(RegisterResultBean data) {
                if (data != null) {
                    //接口回调通知
                    if (type != LYG_PHONE_REGIST) {
                        LoginControl.clearVisitorName();
                    }
                    CKGameManager.setSwitchFlag(false);
                    LoginControl.saveUserToken(data.getCp_user_token());
                    LoginControl.setMemid(data.getMem_id());
                    LoginControl.setVisitorFlag(false);
                    viewStackManager.hiddenAllView();
                    OnLoginListener onLoginListener = CKGameManager.getInstance().getOnLoginListener();
                    if (data.getScore() != null && Integer.valueOf(data.getScore()) > 0) {
                        T.s(loginActivity, loginActivity.getString(ResourceUtils.getStringId("ck_points_add"), data.getScore()));
                    }
                    if (onLoginListener != null) {
                        onLoginListener.loginSuccess(new LogincallBack(data.getMem_id(), data.getCp_user_token()));
                        //登录成功后统一弹出弹框
                        if (type == LYG_PHONE_REGIST) {
                            DialogUtil.showSuccessDialog(loginActivity, account, true);
                        } else {
                            DialogUtil.showSuccessDialog(loginActivity, account, false);
                        }
                    }

                    //保存账号到数据库
                    if (!UserLoginInfodao.getInstance(loginActivity).findUserLoginInfoByName(account)) {
                        UserLoginInfodao.getInstance(loginActivity).saveUserLoginInfo(account, pass);
                    } else {
                        UserLoginInfodao.getInstance(loginActivity).deleteUserLoginByName(account);
                        UserLoginInfodao.getInstance(loginActivity).saveUserLoginInfo(account, pass);
                    }

                }
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(true);
        httpCallbackDecode.setLoadMsg(ResourceUtils.getString(loginActivity, "ck_regist_ing"));
        RxVolley.post(SdkApi.getRegisterMobile(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    public void switchUI(int type) {
        this.type = type;
        switch (type) {
            case LYG_PHONE_REGIST:
                title.setText(ResourceUtils.getString(loginActivity, "ck_fast_regist"));
                other.setVisibility(VISIBLE);
                break;
            case LYG_VISITOR_UPGRATE:
                title.setText(ResourceUtils.getString(loginActivity, "ck_upgrade"));
                other.setVisibility(GONE);
                break;
            case LYG_VISITOR_UPGRATE_PAY:
                title.setText(ResourceUtils.getString(loginActivity, "ck_upgrade"));
                other.setVisibility(GONE);
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
