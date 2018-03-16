package com.game.sdk.ui;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.game.sdk.CKGameService;
import com.game.sdk.CKGameManager;
import com.game.sdk.log.L;
import com.game.sdk.log.T;
import com.game.sdk.util.BaseAppUtil;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;

public class AdWebActivity extends BaseActivity {
    private static final String TAG = "AdWebActivity";
    private WebView wv;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CKGameManager.getInstance().removeFloatView();
        setContentView(MResource.getIdByName(getApplication(), "layout", "ck_sdk_ad_web"));
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        wv = (WebView) findViewById(MResource.getIdByName(getApplication(),
                "R.id.lyg_sdk_wv_content"));
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadsImagesAutomatically(true);
        wv.getSettings().setAppCacheEnabled(false);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setDefaultTextEncodingName("UTF-8");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!DialogUtil.isShowing()) {
                    DialogUtil.showDialog(AdWebActivity.this, ResourceUtils.getString(AdWebActivity.this, "ck_loading"));
                }
                L.e("testWebview onPageStarted", "url=" + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                L.e(TAG, "url=" + url);
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
                        Toast.makeText(view.getContext(), ResourceUtils.getString(AdWebActivity.this, "ck_url_unsupport"), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webviewCompat(wv);
                try {
                    DialogUtil.dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                super.onFormResubmission(view, dontResend, resend);
                resend.sendToTarget();
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse response;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                }
                return super.shouldInterceptRequest(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse response;
                return super.shouldInterceptRequest(view, request);
            }
        });
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        wv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    T.s(AdWebActivity.this, ResourceUtils.getString(AdWebActivity.this, "ck_download_start"));
                    Intent intent = new Intent(AdWebActivity.this, CKGameService.class);
                    intent.putExtra(CKGameService.DOWNLOAD_APK_URL, url);
                    AdWebActivity.this.startService(intent);
                } catch (Exception e) {
                    Toast.makeText(AdWebActivity.this, ResourceUtils.getString(AdWebActivity.this, "ck_url_unsupport"), Toast.LENGTH_SHORT).show();
                }
            }
        });
        webviewCompat(wv);
        wv.loadUrl(url);
    }

    private void webviewCompat(WebView mWebView) {
        if (BaseAppUtil.isNetWorkConneted(mWebView.getContext())) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            mWebView.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()) {
            wv.goBack();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wv != null) {
            wv.removeAllViews();
            wv = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void start(Context context, String url) {
        Intent starter = new Intent(context, AdWebActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        starter.putExtra("url", url);
        context.startActivity(starter);
    }
}
