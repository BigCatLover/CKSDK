package com.game.sdk.ui;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.game.sdk.CKGameManager;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.QueryOrderRequestBean;
import com.game.sdk.domain.QueryOrderResultBean;
import com.game.sdk.domain.WebLoadAssert;
import com.game.sdk.http.HttpCallbackDecode;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.listener.OnPaymentListener;
import com.game.sdk.log.T;
import com.game.sdk.pay.CommonJsForWeb;
import com.game.sdk.pay.IPayListener;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;
import com.game.sdk.util.WebLoadByAssertUtil;
import com.kymjs.rxvolley.RxVolley;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebPayActivity extends BaseActivity implements View.OnClickListener, IPayListener {
    private final static int CODE_PAY_FAIL = -1;//支付失败
    private final static int CODE_PAY_CANCEL = -2;//用户取消支付
    private WebView payWebview;
    private WebView wxWebview;
    private static float charge_money;
    private CommonJsForWeb checkPayJsForPay;
    private TextView tv_charge_title;
    private ImageView iv_return;
    List<WebLoadAssert> webLoadAssertList = WebLoadByAssertUtil.getWebLoadAssertList();
    private String authKey;//对称解密用的authKey
    private boolean callBacked = false, needQuery = false;//是否回调过了
    private String urlParams;
    int requestCount = 0;
    private static int queryCount;
    private final int MOBILE_QUERY = 1;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(this, MResource.LAYOUT,"ck_sdk_activity_web_pay"));
        setupUI();
        queryCount = 0;
    }

    @Override
    public void changeTitleStatus(boolean show) {
        super.changeTitleStatus(show);
    }

    private void setupUI() {
        CKGameManager lyGameSdkManager = CKGameManager.getInstance();
        lyGameSdkManager.removeFloatView();
        payWebview = (WebView) findViewById(MResource.getIdByName(this, "R.id.lyg_sdk_pay_webview"));
        wxWebview = (WebView) findViewById(MResource.getIdByName(this, "R.id.lyg_sdk_pay_wx"));
        initWebView(wxWebview);
        initWebView(payWebview);
        urlParams = getIntent().getStringExtra("urlParams");
        authKey = getIntent().getStringExtra("authKey");
        charge_money = getIntent().getFloatExtra("product_price", 0.00f);
        if (urlParams.startsWith("?")) {
            urlParams = urlParams.substring(1);
        }
        String product_name = getIntent().getStringExtra("product_name");
        payWebview.postUrl(SdkApi.getWebSdkPay(), urlParams.getBytes());
        checkPayJsForPay = new CommonJsForWeb(this, authKey, this);
        checkPayJsForPay.setChargeMoney(charge_money);//设置支付金额
        checkPayJsForPay.setProduct_name(product_name);
        payWebview.addJavascriptInterface(checkPayJsForPay, "huosdk");
        iv_return = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "R.id.lyg_sdk_iv_return"));
        //设置标题栏view
        setTitleView(findViewById(MResource.getIdByName(getApplication(), "R.id.lyg_sdk_rl_top")));
        tv_charge_title = (TextView) findViewById(MResource.getIdByName(
                getApplication(), "R.id.lyg_sdk_tv_charge_title"));
        tv_charge_title.setText(ResourceUtils.getString(WebPayActivity.this, "ck_charge_center"));
        iv_return.setOnClickListener(this);
    }

    private void initWebView(WebView webView) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!DialogUtil.isShowing()) {
                    DialogUtil.showDialog(WebPayActivity.this, ResourceUtils.getString(WebPayActivity.this, "ck_loading"));
                }
                if (SdkApi.getWebSdkPay().equals(url)) {
                    requestCount++;
//                    if (requestCount > 1) {
//                        finish();
//                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                if (url.startsWith("http") || url.startsWith("https") || url.startsWith("ftp")) {
                    return false;
                } else {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(view.getContext(), ResourceUtils.getString(WebPayActivity.this, "ck_url_unsupport"), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.startsWith("weixin://wap/pay?")) {
                    wxWebview.setVisibility(View.VISIBLE);
                }
                try {
                    DialogUtil.dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse response;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                    for (WebLoadAssert webLoadAssert : webLoadAssertList) {
                        if (url.contains(webLoadAssert.getName())) {
                            try {
                                response = new WebResourceResponse(webLoadAssert.getMimeType(), "UTF-8", getAssets().open(webLoadAssert.getName()));
                                return response;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }


            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse response;
                for (WebLoadAssert webLoadAssert : webLoadAssertList) {
                    if (request.getUrl().getPath().contains(webLoadAssert.getName())) {
                        try {
                            response = new WebResourceResponse(webLoadAssert.getMimeType(), "UTF-8", getAssets().open(webLoadAssert.getName()));
                            return response;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient());
        //设置缓存模式，默认是缓存静态资源
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    private boolean isWXPay;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && payWebview.canGoBack()) {
            payWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            payWebview.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 微付通支付结果回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (checkPayJsForPay != null) {
            checkPayJsForPay.onActivityResult(requestCode, resultCode, data);
        }
    }

    private boolean onPause;

    @Override
    protected void onPause() {
        onPause = true;
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        if (isWXPay && onPause) {
            wxWebview.setVisibility(View.GONE);
            payWebview.setVisibility(View.VISIBLE);

            queryOrder(orderId, money, ResourceUtils.getString(this, "ck_paysuccess_wait"));
        }
        super.onResume();
    }

    /**
     * 关闭的时候，将支付信息回调回去
     */
    @Override
    protected void onDestroy() {
        if (isWXPay) {
            isWXPay = false;
        }
        if (DialogUtil.isShowing()) {
            DialogUtil.dismissDialog();
        }
        if (payWebview != null) {
            payWebview.destroy();
        }
        if (checkPayJsForPay != null) {
            checkPayJsForPay.onDestory();
        }
        if (!callBacked) {//还没有回调过
            PaymentErrorMsg paymentErrorMsg = new PaymentErrorMsg();
            paymentErrorMsg.code = CODE_PAY_CANCEL;
            paymentErrorMsg.msg = ResourceUtils.getString(mContext, "ck_pay_cancle");
            paymentErrorMsg.money = charge_money;
            OnPaymentListener paymentListener = CKGameManager.getInstance().getPaymentListener();
            if (paymentListener != null) {
                paymentListener.paymentError(paymentErrorMsg);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == iv_return.getId()) {
            if (needQuery && queryCount < 3) {
                return;
            } else {
                finish();
            }
        }
    }

    @Override
    public void paySuccess(String orderId, final float money) {
        queryOrder(orderId, money, ResourceUtils.getString(this, "ck_paysuccess_wait"));
    }

    @Override
    public void payFail(String orderId, float money, boolean queryOrder, String msg) {
        if (queryOrder) {
            queryOrder(orderId, money, msg);
        } else {
            OnPaymentListener paymentListener = CKGameManager.getInstance().getPaymentListener();
            if (paymentListener != null) {
                PaymentErrorMsg paymentErrorMsg = new PaymentErrorMsg();
                paymentErrorMsg.code = CODE_PAY_FAIL;
                paymentErrorMsg.msg = msg;
                paymentErrorMsg.money = money;
                paymentListener.paymentError(paymentErrorMsg);
            }
            callBacked = true;
            finish();
        }
    }

    private String orderId;
    private float money;

    @Override
    public void loadWX(String orderId, float money, String url) {
        this.orderId = orderId;
        this.money = money;
        Map<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("Referer", "https://api.yoyock.com");
        wxWebview.loadUrl(url, extraHeaders);
        isWXPay = true;
    }

    /**
     * 向服务器查询支付结果
     */
    private void queryOrder(final String orderId, final float money, final String msg) {
        if (!DialogUtil.isShowing()) {
            DialogUtil.showDialog(WebPayActivity.this, ResourceUtils.getString(WebPayActivity.this, "ck_query_pay"));
        }
        QueryOrderRequestBean queryOrderRequestBean = new QueryOrderRequestBean();
        queryOrderRequestBean.setOrder_id(orderId);
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(queryOrderRequestBean));
        HttpCallbackDecode httpCallbackDecode = new HttpCallbackDecode<QueryOrderResultBean>(WebPayActivity.this,
                httpParamsBuild.getAuthkey()) {
            @Override
            public void onDataSuccess(QueryOrderResultBean data) {
                OnPaymentListener paymentListener = CKGameManager.getInstance().getPaymentListener();
                if (paymentListener != null) {
                    if (data != null) {
                        if ("2".equals(data.getStatus())) {
                            needQuery = false;
                            if ("2".equals(data.getCpstatus())) {
                                PaymentCallbackInfo paymentCallbackInfo = new PaymentCallbackInfo(ResourceUtils.getString(WebPayActivity.this, "ck_paysuccess"), money);
                                paymentListener.paymentSuccess(paymentCallbackInfo);
                            } else {
                                PaymentCallbackInfo paymentCallbackInfo = new PaymentCallbackInfo(ResourceUtils.getString(WebPayActivity.this, "ck_paysuccess_wait"), money);
                                paymentListener.paymentSuccess(paymentCallbackInfo);
                            }

                            if (data.getScore() != null && Float.valueOf(data.getScore()) > 0) {
                                T.s(mContext, mContext.getString(ResourceUtils.getStringId("ck_points_add"), data.getScore()));
                            }
                            callBacked = true;
                            finish();
                        } else {
                            needQuery = true;
                            queryCount++;
                            if (queryCount > 2) {
                                PaymentErrorMsg paymentErrorMsg = new PaymentErrorMsg(CODE_PAY_FAIL, msg, money);
                                paymentListener.paymentError(paymentErrorMsg);
                                callBacked = true;
                                finish();
                            } else {
                                Message msg = new Message();
                                msg.what = MOBILE_QUERY;
                                msg.obj = orderId + "," + money + "," + msg;
                                handler.sendMessageDelayed(msg, 2000);
                            }

                        }
                    } else {
                        PaymentErrorMsg paymentErrorMsg = new PaymentErrorMsg(CODE_PAY_FAIL, msg, money);
                        paymentListener.paymentError(paymentErrorMsg);
                        callBacked = true;
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(String code, String msg) {
                super.onFailure(code, msg);
                OnPaymentListener paymentListener = CKGameManager.getInstance().getPaymentListener();
                if (paymentListener != null) {
                    PaymentErrorMsg paymentErrorMsg = new PaymentErrorMsg(CODE_PAY_FAIL, msg, money);
                    paymentListener.paymentError(paymentErrorMsg);
                }
                callBacked = true;
                finish();
            }
        };
        httpCallbackDecode.setShowTs(true);
        httpCallbackDecode.setLoadingCancel(false);
        httpCallbackDecode.setShowLoading(false);
        httpCallbackDecode.setFlag(true);
//        httpCallbackDecode.setLoadMsg(ResourceUtils.getString(this, "ck_query_pay"));
        RxVolley.post(SdkApi.getQueryorder(), httpParamsBuild.getHttpParams(), httpCallbackDecode);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MOBILE_QUERY:
                    String obj = (String) msg.obj;
                    String[] s = obj.split(",");
                    queryOrder(s[0], Float.valueOf(s[1]), s[2]);
                    break;
            }
        }
    };

    public static void start(Context context, String urlParams, Float product_price, String product_name, String authKey) {
        Intent starter = new Intent(context, WebPayActivity.class);
        mContext = context;
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        starter.putExtra("urlParams", urlParams);
        starter.putExtra("authKey", authKey);
        starter.putExtra("product_name", product_name);
        starter.putExtra("product_price", product_price);
        context.startActivity(starter);
    }
}
