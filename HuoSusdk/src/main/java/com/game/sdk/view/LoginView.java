package com.game.sdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.game.sdk.CKGameManager;
import com.game.sdk.SdkConstant;
import com.game.sdk.db.LoginControl;
import com.game.sdk.db.impl.UserLoginInfodao;
import com.game.sdk.domain.LoginRequestBean;
import com.game.sdk.domain.LoginResultBean;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.RegisterResultBean;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.domain.VisitorRequestBean;
import com.game.sdk.domain.WebRequestBean;
import com.game.sdk.http.HttpCallbackDecode;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.log.T;
import com.game.sdk.ui.CkLoginActivity;
import com.game.sdk.ui.FloatWebActivity;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.RegExpUtil;
import com.game.sdk.util.ResourceUtils;
import com.kymjs.rxvolley.RxVolley;

import java.util.List;

/**
 * Created by zhanglei on 2017/5/30.
 */

public class LoginView extends FrameLayout implements View.OnClickListener {
    private CkLoginActivity loginActivity;
    private ViewStackManager viewStackManager;
    private EditText account;
    private EditText password;
    private TextView visitorLogin;
    private TextView findPassword;
    private TextView commit;
    private ImageView ivreturn;
    private CheckBox eye;
    private ImageView ivMore;
    private TextView title;
    private ImageView pass_dele;
    private ImageView account_dele;
    private PopupWindow pw_select_user;
    private List<UserInfo> userInfoList;
    private RecordUserAdapter pw_adapter;

    public LoginView(Context context) {
        super(context);
        setupUI();
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupUI();
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupUI();
    }

    /**
     *
     */
    private void setupUI() {
        loginActivity = (CkLoginActivity) getContext();
        viewStackManager = ViewStackManager.getInstance(loginActivity);
        LayoutInflater.from(getContext()).inflate(MResource.getIdByName(getContext(), MResource.LAYOUT, "ck_include_login"), this);
        account = (EditText) findViewById(MResource.getIdByName(loginActivity, "R.id.acountname"));
        password = (EditText) findViewById(MResource.getIdByName(loginActivity, "R.id.password"));
        visitorLogin = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.visitor_login"));
        findPassword = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.find_password"));
        commit = (TextView) findViewById(MResource.getIdByName(loginActivity, "R.id.commit"));
        ivreturn = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.iv_return"));
        eye = (CheckBox) findViewById(MResource.getIdByName(getContext(), "R.id.pass_eye"));
        title = (TextView) findViewById(MResource.getIdByName(getContext(), "R.id.title"));
        account_dele = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.account_dele"));
        pass_dele = (ImageView) findViewById(MResource.getIdByName(getContext(), "R.id.pass_dele"));
        ivMore = (ImageView) findViewById(MResource.getIdByName(loginActivity, "R.id.iv_more"));

        pass_dele.setVisibility(GONE);
        account_dele.setVisibility(GONE);

        account.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pw_select_user != null && pw_select_user.isShowing()) {
                    pw_select_user.dismiss();
                }
                userInfoList = UserLoginInfodao.getInstance(loginActivity).getUserLoginInfo();
                if (null == userInfoList || userInfoList.isEmpty() || userInfoList.size() < 0) {
                    if (s.length() > 0) {
                        account_dele.setVisibility(GONE);
                        ivMore.setVisibility(VISIBLE);
                        ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_delete"));
                    } else {
                        account_dele.setVisibility(GONE);
                        ivMore.setVisibility(GONE);
                    }
                } else {
                    if (s.length() > 0) {
                        account_dele.setVisibility(VISIBLE);
                        ivMore.setVisibility(VISIBLE);
                        ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_bottom"));
                        ;
                    } else {
                        account_dele.setVisibility(GONE);
                        ivMore.setVisibility(GONE);
                        ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_bottom"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!account.isCursorVisible()) {
                    account_dele.setVisibility(GONE);
                }
            }
        });
        account.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                userInfoList = UserLoginInfodao.getInstance(loginActivity).getUserLoginInfo();
                if (hasFocus) {
                    if (null == userInfoList || userInfoList.isEmpty() || userInfoList.size() < 0) {
                        if (account.getText().toString().trim().length() > 0) {
                            if (account.isCursorVisible()) {
                                account_dele.setVisibility(GONE);
                                ivMore.setVisibility(VISIBLE);
                                ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_delete"));
                            } else {
                                account_dele.setVisibility(GONE);
                                ivMore.setVisibility(GONE);
                            }
                        } else {
                            account_dele.setVisibility(GONE);
                            ivMore.setVisibility(GONE);
                        }
                    } else {
                        if (account.getText().toString().trim().length() > 0) {
                            if (account.isCursorVisible()) {
                                account_dele.setVisibility(VISIBLE);
                            } else {
                                account_dele.setVisibility(GONE);
                            }
                            ivMore.setVisibility(VISIBLE);
                            ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_bottom"));
                        } else {
                            account_dele.setVisibility(GONE);
                            ivMore.setVisibility(GONE);
                            ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_bottom"));
                        }
                    }
                } else {
                    hideKeyboard(account);
                    account_dele.setVisibility(GONE);
                    if (null == userInfoList && userInfoList.isEmpty()) {
                        ivMore.setVisibility(GONE);
                    } else {
                        ivMore.setVisibility(VISIBLE);
                        ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_bottom"));
                    }
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
                    pass_dele.setVisibility(VISIBLE);
                } else {
                    pass_dele.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!password.isCursorVisible()) {
                    pass_dele.setVisibility(GONE);
                }
            }
        });
        password.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && password.getText().toString().trim().length() > 0) {
                    pass_dele.setVisibility(VISIBLE);
                } else {
                    hideKeyboard(password);
                    pass_dele.setVisibility(GONE);
                }
            }
        });
        account.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    account.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    password.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });

        eye.setOnClickListener(this);
        visitorLogin.setOnClickListener(this);
        findPassword.setOnClickListener(this);
        commit.setOnClickListener(this);
        ivreturn.setOnClickListener(this);
        account_dele.setOnClickListener(this);
        pass_dele.setOnClickListener(this);
        ivMore.setOnClickListener(this);

        UserInfo userInfoLast = UserLoginInfodao.getInstance(loginActivity).getUserInfoLast();
        if (userInfoLast != null && userInfoLast.username != null && !userInfoLast.username.isEmpty()) {
            account.setText(userInfoLast.username);
            password.setText(userInfoLast.password);
            account.setSelection(userInfoLast.username.length());
            password.setSelection(userInfoLast.password.length());
            account.setCursorVisible(false);
            password.setCursorVisible(false);
            account_dele.setVisibility(GONE);
            pass_dele.setVisibility(GONE);
        }

        userInfoList = UserLoginInfodao.getInstance(loginActivity).getUserLoginInfo();
        if (null == userInfoList && userInfoList.isEmpty()) {
            ivMore.setVisibility(View.GONE);
        } else {
            ivMore.setVisibility(View.VISIBLE);
            ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_bottom"));
        }

        findPassword.setText(ResourceUtils.getString(loginActivity, "ck_forgot_pass"));
        visitorLogin.setText(ResourceUtils.getString(loginActivity, "ck_visitor_login1"));
        title.setText(ResourceUtils.getString(loginActivity, "ck_account_login"));
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
                if (password.getText().toString().trim().length() > 0) {
                    password.setSelection(password.getText().toString().trim().length());
                    password.setCursorVisible(false);
                }
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                if (password.getText().toString().trim().length() > 0) {
                    password.setSelection(password.getText().toString().trim().length());
                    password.setCursorVisible(false);
                }

            }
        } else if (v.getId() == findPassword.getId()) {
            HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(new WebRequestBean()));
            FloatWebActivity.start(loginActivity, SdkApi.getWebForgetpwd(), "忘记密码",
                    httpParamsBuild.getHttpParams().getUrlParams().toString(), httpParamsBuild.getAuthkey(), 0);
        } else if (v.getId() == commit.getId()) {
            submitLogin();
        } else if (v.getId() == ivreturn.getId()) {
            isShow = false;
            pw_select_user = null;
            viewStackManager.addView(loginActivity.getLygFirstLoginView());
        } else if (v.getId() == visitorLogin.getId()) {
            if (!LoginControl.getVisitorName().isEmpty()) {
                VisitorLogin();
            } else {
                VisitorRegist();
            }
        } else if (v.getId() == account_dele.getId()) {
            account.setText("");
            password.setText("");
            account_dele.setVisibility(GONE);
        } else if (v.getId() == pass_dele.getId()) {
            password.setText("");
            pass_dele.setVisibility(GONE);
        } else if (v.getId() == ivMore.getId()) {
            userInfoList = UserLoginInfodao.getInstance(loginActivity).getUserLoginInfo();
            if (null == userInfoList || userInfoList.isEmpty()) {
                account.setText("");
                ivMore.setVisibility(GONE);
            } else {
                ivMore.setVisibility(VISIBLE);
                userselect(account, account.getWidth());
            }
        }
    }

    private void submitLogin() {
        final String name = account.getText().toString().trim();
        final String pass = password.getText().toString().trim();
        int type = 1;
        if (RegExpUtil.isMatchRegistAccount(name)) {
            type = 2;
        } else if (RegExpUtil.isMobileNumber(name)) {
            type = 1;
        }
        if (!RegExpUtil.isMatchPassword(pass)) {
            T.s(loginActivity, "密码只能由6至16位非空字符组成");
            return;
        }
        final LoginRequestBean loginRequestBean = new LoginRequestBean();
        loginRequestBean.setUsername(name);
        loginRequestBean.setPassword(pass);
        loginRequestBean.setType(type);
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(loginRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<LoginResultBean>(loginActivity, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(LoginResultBean data) {
                if (data != null) {
                    viewStackManager.hiddenAllView();
                    LoginControl.saveUserToken(data.getCp_user_token());
                    LoginControl.setVisitorFlag(data.getType() == 3);

                    OnLoginListener onLoginListener = CKGameManager.getInstance().getOnLoginListener();
                    if (data.getScore() != null && Integer.valueOf(data.getScore()) > 0) {
                        T.s(loginActivity, "月牙值+" + data.getScore() + "！");
                    }
                    if (onLoginListener != null) {
                        DialogUtil.showSuccessDialog(loginActivity, name, false);
                        onLoginListener.loginSuccess(new LogincallBack(data.getMem_id(), data.getCp_user_token()));
                    }
                    //保存账号到数据库
                    if (!UserLoginInfodao.getInstance(loginActivity).findUserLoginInfoByName(name)) {
                        UserLoginInfodao.getInstance(loginActivity).saveUserLoginInfo(name, pass);
                    } else {
                        UserLoginInfodao.getInstance(loginActivity).deleteUserLoginByName(name);
                        UserLoginInfodao.getInstance(loginActivity).saveUserLoginInfo(name, pass);
                    }
                }
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(true);
        httpCallbackDecode.setLoadMsg("正在登录...");
        RxVolley.post(SdkApi.getLogin(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void VisitorLogin() {
        viewStackManager.addView(loginActivity.getVisitorSecondLoginView());
    }

    private void VisitorRegist() {
        VisitorRequestBean visitorRegisterRequestBean = new VisitorRequestBean();
        visitorRegisterRequestBean.setType(3);
        visitorRegisterRequestBean.setIntroducer("");
        visitorRegisterRequestBean.setUsername(SdkConstant.deviceBean.getDevice_id());
        visitorRegisterRequestBean.setPassword("123456");
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(visitorRegisterRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<RegisterResultBean>(loginActivity, httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(RegisterResultBean data) {
                if (data != null) {
//                    T.s(loginActivity,"登陆成功："+data.getCp_user_token());
                    //接口回调通知
                    LoginControl.saveUserToken(data.getCp_user_token());
                    LoginControl.setMemid(data.getMem_id());
                    LoginControl.setVisitorFlag(data.getType() == 3);
                    OnLoginListener onLoginListener = CKGameManager.getInstance().getOnLoginListener();
                    if (onLoginListener != null) {
                        onLoginListener.loginSuccess(new LogincallBack(data.getMem_id(), data.getCp_user_token()));
                        viewStackManager.hiddenAllView();
                        DialogUtil.showVisitorSuccessDialog(loginActivity, data.getMem_id());
                        LoginControl.savaVisitorName(data.getMem_id());
                    }
                }
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(true);
        httpCallbackDecode.setLoadMsg("注册中...");
        RxVolley.post(SdkApi.getRegister(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    private void hideKeyboard(View view) {
        //隐藏键盘
        InputMethodManager imm = (InputMethodManager) loginActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isShow = false;

    private void userselect(View v, int width) {
        hideKeyboard(account);
        if (pw_select_user != null && isShow) {
            pw_select_user.dismiss();
            isShow = false;
            ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_bottom"));
        } else {
            userInfoList = UserLoginInfodao.getInstance(loginActivity).getUserLoginInfo();
            if (null == userInfoList || userInfoList.isEmpty()) {
                return;
            }
            if (null == pw_adapter) {
                pw_adapter = new RecordUserAdapter();
            }
            if (pw_select_user == null) {
                // View
                View view = LayoutInflater.from(loginActivity).inflate(MResource.getIdByName(loginActivity, MResource.LAYOUT,"ck_sdk_pop_record_account"), null);
                // ListView lv_pw=(ListView) view.findViewById(R.id.lv_pw);
                ListView lv_pw = (ListView) view.findViewById(MResource
                        .getIdByName(loginActivity, "R.id.lygame_sdk_lv_pw"));
                // LinearLayout.LayoutParams lp=new
                // LinearLayout.LayoutParams(200,-2 );
                // lv_pw.setLayoutParams(lp);
                lv_pw.setCacheColorHint(0x00000000);
                lv_pw.setAdapter(pw_adapter);
                lv_pw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterview,
                                            View view, int position, long row) {
                        pw_select_user.dismiss();
                        UserInfo userInfo = userInfoList.get(position);
                        account.setText(userInfo.username);
                        password.setText(userInfo.password);
//                        accountName.setSelection(userInfo.username.length());
                    }
                });
                pw_select_user = new PopupWindow(view, width,
                        LinearLayout.LayoutParams.WRAP_CONTENT, false);
//                pw_select_user.setBackgroundDrawable(new ColorDrawable(
//                        0x00000000));
                pw_select_user.setContentView(view);
                pw_select_user.setOutsideTouchable(true);

            } else {
                pw_adapter.notifyDataSetChanged();
            }
            pw_select_user.showAsDropDown(v, 0, 0);
            isShow = true;
            ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_top"));
        }
    }

    /**
     * popupwindow显示已经登录用户的设配器
     *
     * @author Administrator
     */
    private class RecordUserAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return userInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (null == convertView) {
                View view = LayoutInflater.from(loginActivity).inflate(MResource.getIdByName(loginActivity,MResource.LAYOUT,
                        "ck_sdk_pop_record_account_list_item"), null);

                convertView = view;
            }
            TextView tv_username = (TextView) convertView.findViewById(MResource.getIdByName(loginActivity, "R.id.lygame_tv_username"));
            ImageView iv_delete = (ImageView) convertView.findViewById(MResource.getIdByName(loginActivity, "R.id.lygame_iv_delete"));
            iv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 如果删除的用户名与输入框中的用户名一致将删除输入框中的用户名与密码
                    if (account.getText().toString().trim().equals(userInfoList.get(position).username)) {
                        account.setText("");
                        password.setText("");
                    }
                    UserLoginInfodao.getInstance(loginActivity).deleteUserLoginByName(userInfoList.get(position).username);
                    userInfoList.remove(position);
                    userInfoList = UserLoginInfodao.getInstance(loginActivity).getUserLoginInfo();
                    if (null == userInfoList || userInfoList.isEmpty() || userInfoList.size() < 1) {
                        ivMore.setVisibility(View.GONE);
                    } else {
                        ivMore.setVisibility(View.VISIBLE);
                        ivMore.setImageResource(ResourceUtils.getDrawableId(loginActivity, "ck_bottom"));
                    }
                    if (null != pw_adapter) {
                        if (userInfoList.isEmpty()) {
                            pw_select_user.dismiss();
                        }
                        notifyDataSetChanged();
                    }
                }
            });
            tv_username.setText(userInfoList.get(position).username);
            return convertView;
        }
    }
}
