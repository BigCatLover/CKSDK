package com.game.sdk.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
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
import android.widget.TextView;

import com.game.sdk.db.LoginControl;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.RegisterResultBean;
import com.game.sdk.domain.UserNameRegisterRequestBean;
import com.game.sdk.http.HttpCallbackDecode;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.log.T;
import com.game.sdk.ui.CkAgreeActivity;
import com.game.sdk.ui.CkLoginActivity;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.RegExpUtil;
import com.game.sdk.util.ResourceUtils;
import com.kymjs.rxvolley.RxVolley;

/**
 * Created by zhanglei on 2017/5/30.
 */

public class UserNameRegistView extends FrameLayout implements View.OnClickListener {
    private CkLoginActivity loginActivity;
    private ViewStackManager viewStackManager;
    private EditText account;
    private EditText password;
    private CheckBox eye;
    private TextView commit;
    private ImageView ivreturn;
    private ImageView accountDele;
    private TextView agree;
    private ImageView passDele;

    public UserNameRegistView(Context context) {
        super(context);
        setupUI();
    }

    public UserNameRegistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public UserNameRegistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    private void setupUI() {
        loginActivity = (CkLoginActivity) getContext();
        viewStackManager = ViewStackManager.getInstance(loginActivity);
        LayoutInflater.from(getContext()).inflate(MResource.getIdByName(getContext(), MResource.LAYOUT, "ck_include_username_regist"), this);
        account = (EditText) findViewById(MResource.getIdByName(loginActivity, "R.id.regist_acountname"));
        password = (EditText) findViewById(MResource.getIdByName(loginActivity, "R.id.password"));
        commit = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.regist_commit"));
        ivreturn = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.iv_return"));
        eye = (CheckBox) findViewById(MResource.getIdByName(getContext(), "R.id.eye"));
        passDele = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.pass_dele"));
        accountDele = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.account_dele"));
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

        eye.setOnClickListener(this);
        commit.setOnClickListener(this);
        ivreturn.setOnClickListener(this);
        passDele.setOnClickListener(this);
        accountDele.setOnClickListener(this);
        accountDele.setVisibility(GONE);
        passDele.setVisibility(GONE);
        account.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (account.getText().toString().trim().length() > 0) {
                        accountDele.setVisibility(VISIBLE);
                    } else {
                        accountDele.setVisibility(GONE);
                    }
                } else {
                    hideKeyboard(account);
                    accountDele.setVisibility(GONE);
                }
            }
        });
        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    accountDele.setVisibility(VISIBLE);
                } else {
                    accountDele.setVisibility(GONE);
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
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void hideKeyboard(View view) {
        //隐藏键盘
        InputMethodManager imm = (InputMethodManager) loginActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        } else if (v.getId() == commit.getId()) {
            submitRegister();
        } else if (v.getId() == ivreturn.getId()) {
            password.setText("");
            account.setText("");
            viewStackManager.removeTopView();
        } else if (v.getId() == passDele.getId()) {
            password.setText("");
        } else if (v.getId() == accountDele.getId()) {
            account.setText("");
        }
    }

    /**
     *
     */
    private void submitRegister() {
        final String username = account.getText().toString().trim();
        final String pass = password.getText().toString().trim();
        if (!RegExpUtil.isMatchRegistAccount(username)) {
            T.s(loginActivity, ResourceUtils.getString(loginActivity, "ck_accountname_format_err"));
            return;
        }
        if (!RegExpUtil.isMatchPassword(pass)) {
            T.s(loginActivity, ResourceUtils.getString(loginActivity, "ck_password_err"));
            return;
        }
        UserNameRegisterRequestBean userNameRegisterRequestBean = new UserNameRegisterRequestBean();
        userNameRegisterRequestBean.setUsername(username);
        userNameRegisterRequestBean.setPassword(pass);
        userNameRegisterRequestBean.setType(2);
        userNameRegisterRequestBean.setUpdate(UserNameRegisterRequestBean.TYPE_REGIST);
        userNameRegisterRequestBean.setIntroducer("");
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(userNameRegisterRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<RegisterResultBean>(loginActivity, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(RegisterResultBean data) {
                if (data != null) {
                    LoginControl.saveUserToken(data.getCp_user_token());
                    LoginControl.setMemid(data.getMem_id());
                    LoginControl.setVisitorFlag(data.getType() == 3);
                    if (data.getScore() != null && Integer.valueOf(data.getScore()) > 0) {
                        T.s(loginActivity, loginActivity.getString(ResourceUtils.getStringId("ck_points_add"), data.getScore()));
                    }
                    NeedBindingView needBindingView = (NeedBindingView) viewStackManager.getViewByClass(NeedBindingView.class);
                    if (needBindingView != null) {
                        needBindingView.switchUI(NeedBindingView.LYG_REGIST, username);
                        viewStackManager.addView(needBindingView);
                    }
                    //保存账号到数据库
                    if (!UserLoginInfodao.getInstance(loginActivity).findUserLoginInfoByName(username)) {
                        UserLoginInfodao.getInstance(loginActivity).saveUserLoginInfo(username, pass);
                    } else {
                        UserLoginInfodao.getInstance(loginActivity).deleteUserLoginByName(username);
                        UserLoginInfodao.getInstance(loginActivity).saveUserLoginInfo(username, pass);
                    }
                }
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(true);
        httpCallbackDecode.setLoadMsg(ResourceUtils.getString(loginActivity, "ck_regist_ing"));
        RxVolley.post(SdkApi.getRegister(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
