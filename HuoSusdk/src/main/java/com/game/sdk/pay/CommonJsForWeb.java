package com.game.sdk.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.game.sdk.CKGameManager;
import com.game.sdk.SdkConstant;
import com.game.sdk.domain.PayResultBean;
import com.game.sdk.listener.OnInitSdkListener;
import com.game.sdk.listener.OnLogoutListener;
import com.game.sdk.log.T;
import com.game.sdk.ui.AdActivity;
import com.game.sdk.ui.BaseActivity;
import com.game.sdk.ui.WebViewActivity;
import com.game.sdk.util.AuthCodeUtil;
import com.game.sdk.util.BaseAppUtil;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.RSAUtils;
import com.game.sdk.util.ResourceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liu hong liang on 2016/10/13.
 */
public class CommonJsForWeb {
    private static final String TAG = CommonJsForWeb.class.getSimpleName();
    private Activity webContext;
    private float chargeMoney;
    private String authKey;
    private static String product_name;
    private IPayListener iPayListener;
    ILYPay iLYGamePay;
    Handler handler;

    private static Map<String, String> payMap;

    static {
        payMap = new HashMap();
        payMap.put("alipay", "impl.AliPayIml");
        payMap.put("paypal", "impl.PayPalIml");
//        payMap.put("spay", "impl.WftPayIml");
//        payMap.put("payeco", "impl.EcoPayIml");
//        payMap.put("heepay", "impl.HeepayPayIml");
        payMap.put("nowpay", "impl.IpaynowPayIml");
//        payMap.put("zwxpay", "impl.ZwxpayIml");
//        payMap.put("unionpay", "impl.UnionpayIml");
//        payMap.put("jubaopay", "impl.JuBaoPayIml");
    }

    public CommonJsForWeb(Activity activity, String authKey, IPayListener iPayListener) {
        this.webContext = activity;
        this.authKey = authKey;
        this.iPayListener = iPayListener;
        handler = new Handler();
    }

    /**
     * 设置支付的钱，只有用户传入金额的支付页面才会传
     *
     * @param chargeMoney
     */
    public void setChargeMoney(float chargeMoney) {
        this.chargeMoney = chargeMoney;
    }

    public static String getProduct_name() {
        return product_name;
    }

    public static void setProduct_name(String product_name) {
        CommonJsForWeb.product_name = product_name;
    }

    @JavascriptInterface
    public void huoPay(final String result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //解密数据
                    if (TextUtils.isEmpty(result)) {
                        T.s(webContext, ResourceUtils.getString(webContext,"ck_server_err"));
                        return;
                    }
                    String decodeAuthData = AuthCodeUtil.authcodeDecode(result, authKey);
//                    Log.e(TAG,"解密后的数据="+decodeAuthData);
                    //使用
                    JSONObject jsonObject = new JSONObject(decodeAuthData);
                    String sign = jsonObject.optString("sign");
                    String responcedata = jsonObject.optString("responcedata");
                    //验证签名
//                    Log.e(TAG,"http_result_rsaKey="+ SdkConstant.RSA_PUBLIC_KEY);
//                    Log.e(TAG,"responcedata="+ responcedata);
                    boolean verify = RSAUtils.verify(responcedata.getBytes(), SdkConstant.RSA_PUBLIC_KEY, sign);
//                    Log.e(TAG,"verify="+ verify);
                    if (!verify) {
                        T.s(webContext, ResourceUtils.getString(webContext,"ck_verify_err"));
                        return;
                    }
                    PayResultBean payResultBean = GsonUtil.getGson().fromJson(responcedata, PayResultBean.class);
                    for (String payTypeKey : payMap.keySet()) {
//                        Log.e(TAG,"payTypeKey="+ payTypeKey);
                        if (payResultBean.getPaytype().equals(payTypeKey)) {
                            try {
                                String packageClassName = CommonJsForWeb.class.getName();
                                String payImplPath = packageClassName.substring(0,
                                        packageClassName.lastIndexOf(CommonJsForWeb.class.getSimpleName()));
                                iLYGamePay = (ILYPay) Class.forName(payImplPath + payMap.get(payTypeKey)).newInstance();
                                iLYGamePay.startPay(webContext, iPayListener, chargeMoney, payResultBean);
                            } catch (Exception e) {
                                e.printStackTrace();
                                T.s(webContext, ResourceUtils.getString(webContext,"ck_unsupport_paymethod"));
                            }
                            return;
                        }
                    }
                    if ("gamepay".equals(payResultBean.getPaytype())) {
                        if ("1".equals(payResultBean.getStatus())) {
                            iPayListener.payFail(payResultBean.getOrder_id(), chargeMoney, false, ResourceUtils.getString(webContext,"ck_unpay"));
                        } else if ("2".equals(payResultBean.getStatus())) {
                            iPayListener.paySuccess(payResultBean.getOrder_id(), chargeMoney);
                        } else if ("3".equals(payResultBean.getStatus())) {
                            iPayListener.payFail(payResultBean.getOrder_id(), chargeMoney, false, ResourceUtils.getString(webContext,"ck_payfail"));
                        }
                    } else {
                        Toast.makeText(webContext, ResourceUtils.getString(webContext,"ck_unsupport_paymethod"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(webContext, ResourceUtils.getString(webContext,"ck_pay_params_err"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @JavascriptInterface//重新初始化
    public void resetToken() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                webContext.finish();
                CKGameManager.getInstance().initSdk(CKGameManager.getInstance().getContext(),
                        new OnInitSdkListener() {
                            @Override
                            public void initSuccess(String code, String msg) {
                                //账号过期，退出登陆
                                CKGameManager.getInstance().logoutExecute(OnLogoutListener.TYPE_TOKEN_INVALID);
                            }

                            @Override
                            public void initError(String code, String msg) {
                                T.s(CKGameManager.getInstance().getContext(), msg);
                            }
                        });

            }
        });
    }

    @JavascriptInterface
    public void closeWeb() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                webContext.finish();
            }
        });
    }

    @JavascriptInterface
    public void activeCenter() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AdActivity.start(webContext, CKGameManager.getInstance().getAdList().size());
            }
        });
    }

    @JavascriptInterface
    public void changeAccount() {
        if (handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    CKGameManager lyGameSdkManager = CKGameManager.getInstance();
                    lyGameSdkManager.logoutExecute(OnLogoutListener.TYPE_SWITCH_ACCOUNT);
                    webContext.finish();
                }
            });
        }
    }

    /**
     * 打开一个新窗口
     */
    @JavascriptInterface
    public void openWeb(String info) {
        try {
            JSONObject jsonObject = new JSONObject(info);
            final int type = jsonObject.optInt("type");
            final String url = jsonObject.optString("url");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    WebViewActivity.start(webContext, null, url, type, WebViewActivity.REQUEST_TYPE_POST);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拨打电话，跳转到拨号界面
     *
     * @param context
     * @param phoneNumber
     */
    public static void callDial(Context context, String phoneNumber) {
        try {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void openQq(final String qq) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
                    webContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception e) {
                    // 未安装手Q或安装的版本不支持
                    Toast.makeText(webContext, ResourceUtils.getString(webContext,"ck_qq_unsupport"), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @JavascriptInterface
    public void openPhone(final String phone) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callDial(webContext, phone);
            }
        });
    }

    @JavascriptInterface
    public void joinQqgroup(final String key) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                joinQQGroup(key);
            }
        });

    }

    /**
     * 复制字符串
     *
     * @param data 要复制的文字
     */
    @JavascriptInterface
    public void copyString(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                BaseAppUtil.copyToSystem(webContext, data);
                T.s(webContext, ResourceUtils.getString(webContext,"ck_copy_success"));
            }
        });
    }

    /**
     * 外部浏览器打开网页
     *
     * @param url 要打开的网址
     */
    @JavascriptInterface
    public void outWeb(final String url) {
        if (!TextUtils.isEmpty(url)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    webContext.startActivity(intent);
                }
            });
        }
    }

    /**
     * 支付结果通知
     *
     * @param orderid 支付的订单号
     */
    @JavascriptInterface
    public void payNotify(final String orderid) {
        if (!TextUtils.isEmpty(orderid)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (iPayListener != null) {
                        iPayListener.payFail(orderid, chargeMoney, true, ResourceUtils.getString(webContext,"ck_pay_unknow"));
                    }
//                    try {
//                        PayResultBean payResultBean = GsonUtil.getGson().fromJson(info, PayResultBean.class);
//                        if(iPayListener!=null){
//                            if("1".equals(payResultBean.getStatus())){
//                                iPayListener.payFail(payResultBean.getOrder_id(),chargeMoney,false,"未支付");
//                            }else if("2".equals(payResultBean.getStatus())){
//                                iPayListener.paySuccess(payResultBean.getOrder_id(),chargeMoney);
//                            }else if("3".equals(payResultBean.getStatus())){
//                                iPayListener.payFail(payResultBean.getOrder_id(),chargeMoney,false,"支付失败");
//                            }
//                        }
//                    } catch (JsonSyntaxException e) {
//                        e.printStackTrace();
//                    }
                }
            });
        }
    }

    /**
     * 隐藏和或者显示标题栏
     *
     * @param type 要打开的网址
     */
    @JavascriptInterface
    public void changeTitleStatus(final String type) {
        if (!TextUtils.isEmpty(type)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (webContext instanceof BaseActivity) {
                        BaseActivity baseActivity = (BaseActivity) webContext;
                        if ("show".equals(type)) {
                            baseActivity.changeTitleStatus(true);
                        } else if ("hidden".equals(type)) {
                            baseActivity.changeTitleStatus(false);
                        }
                    }
                }
            });
        }
    }

    /****************
     *
     * 发起添加群流程。群号：测试群(594245585) 的 key 为： n62NA_2zzhPfmNicq-sZLioBGiN2v7Oq
     * 调用 joinQQGroup(n62NA_2zzhPfmNicq-sZLioBGiN2v7Oq) 即可发起手Q客户端申请加群 测试群(594245585)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            webContext.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(webContext, ResourceUtils.getString(webContext,"ck_qq_unsupport"), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void onDestory() {
        if (iLYGamePay != null) {
            iLYGamePay.onDestory();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (iLYGamePay != null) {
            iLYGamePay.onActivityResult(requestCode, resultCode, data);
        }
    }
}
