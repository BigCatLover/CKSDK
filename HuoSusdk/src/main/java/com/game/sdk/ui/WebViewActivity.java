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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebBackForwardList;
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
import com.game.sdk.CKGameService;
import com.game.sdk.domain.WebLoadAssert;
import com.game.sdk.http.SdkApi;
import com.game.sdk.log.L;
import com.game.sdk.log.T;
import com.game.sdk.pay.CommonJsForWeb;
import com.game.sdk.util.BaseAppUtil;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.WebLoadByAssertUtil;
import com.kymjs.rxvolley.client.HttpParams;

import java.io.IOException;
import java.util.List;


public class WebViewActivity extends BaseActivity implements OnClickListener {
	public final static String WINDOW_TYPE = "window_type";
	public final static String TITLE_NAME = "titleName";
	public final static String REQUEST_TYPE = "REQUEST_TYPE";
	public final static int REQUEST_TYPE_GET = 1;
	public final static int REQUEST_TYPE_POST = 2;
	public final static String URL = "url";
	//是	INT	1 全屏无状态栏无导航栏 2 全屏无状态栏 有导航栏 3 全屏有状态栏无导航栏 4 全屏有状态栏有导航栏
	public static final int TYPE_NO_STATUS_NO_TITLE = 1;
	public static final int TYPE_NO_STATUS_TITLE = 2;
	public static final int TYPE_STATUS_NO_TITLE = 3;
	public static final int TYPE_STATUS_TITLE = 4;
	private static final String TAG = WebViewActivity.class.getName();
	private final static String INDEX_URL_FLAG="User/index";
	private int windowType;
	private int requestType;
	private WebView wv;
	private TextView tv_charge_title;
	private String url, title;
	private ImageView iv_return;
	private View lygame_sdk_rl_top;
	private CommonJsForWeb commonJsForWeb;
	List<WebLoadAssert> webLoadAssertList = WebLoadByAssertUtil.getWebLoadAssertList();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CKGameManager.getInstance().removeFloatView();
		initParams();
		initTheme();
		setContentView(MResource.getIdByName(getApplication(), "layout", "ck_sdk_activity_float_web"));
		initUI();
		webviewInit();
		HttpParams httpParams = SdkApi.getCommonHttpParams("");
		StringBuilder urlParams = httpParams.getUrlParams();
		if(requestType==REQUEST_TYPE_GET){
			wv.loadUrl(url + urlParams.toString(), httpParams.getHeaderMap());
			L.d(TAG, url + urlParams.toString() + "====>" + httpParams.getHeaderMap().toString());
		}else{
			wv.postUrl(url,urlParams.substring(1).getBytes());
			L.e(TAG, url + urlParams.toString() + "====>" + httpParams.getHeaderMap().toString());
		}
		L.e(TAG,"request_url="+url);
	}
	private void initParams(){
		Intent intent = getIntent();
		if (intent != null) {
			url = intent.getStringExtra(URL);
			title = intent.getStringExtra(TITLE_NAME);
			windowType = getIntent().getIntExtra(WINDOW_TYPE, 4);
			requestType = getIntent().getIntExtra(REQUEST_TYPE, 1);
		}

	}
	private void initTheme() {
		Log.e("hongliang","windowType="+windowType);
		switch (windowType) {
			case TYPE_NO_STATUS_NO_TITLE:
				setTheme(MResource.getIdByName(this,"R.style.huo_sdk_FullscreenTheme"));
				break;
			case TYPE_NO_STATUS_TITLE:
				setTheme(MResource.getIdByName(this,"R.style.huo_sdk_FullscreenTheme"));
				break;
			case TYPE_STATUS_NO_TITLE:
				setTheme(MResource.getIdByName(this,"R.style.huo_sdk_AppTheme"));
				break;
			case TYPE_STATUS_TITLE:
				setTheme(MResource.getIdByName(this,"R.style.huo_sdk_AppTheme"));
				break;
		}
	}
	private void initUI(){
		wv = (WebView) findViewById(MResource.getIdByName(getApplication(),
				"R.id.huo_sdk_wv_content"));
		iv_return = (ImageView) findViewById(MResource.getIdByName(
				getApplication(),  "R.id.huo_sdk_iv_return"));

		tv_charge_title = (TextView) findViewById(MResource.getIdByName(
				getApplication(),  "R.id.huo_sdk_tv_charge_title"));
		lygame_sdk_rl_top= findViewById(MResource.getIdByName(
				getApplication(),  "R.id.huo_sdk_rl_top"));
		setTitleView(lygame_sdk_rl_top);
		iv_return.setOnClickListener(this);
		//设置是否需要状态栏
		if(windowType==TYPE_NO_STATUS_NO_TITLE||windowType==TYPE_STATUS_NO_TITLE){
			lygame_sdk_rl_top.setVisibility(View.GONE);
		}else{
			lygame_sdk_rl_top.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(title)) {
			tv_charge_title.setText(title);
		}
	}
	private void webviewInit(){
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setLoadsImagesAutomatically(true);
		wv.getSettings().setAppCacheEnabled(false);
		wv.getSettings().setDomStorageEnabled(true);
		wv.getSettings().setDefaultTextEncodingName("UTF-8");
		//浮点里面的是没有回调的
		commonJsForWeb = new CommonJsForWeb(this,"",null);
		wv.addJavascriptInterface(commonJsForWeb,"huosdk");
//		wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (!DialogUtil.isShowing()) {
					DialogUtil.showDialog(WebViewActivity.this, "正在加载...");
				}
				L.e("testWebview onPageStarted","url="+url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				L.e(TAG,"url="+url);
				//返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				if(url.startsWith("http")||url.startsWith("https")||url.startsWith("ftp")){
					return false;
				}else{
					try{
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						view.getContext().startActivity(intent);
					}catch (ActivityNotFoundException e){
						Toast.makeText(view.getContext(), "手机还没有安装支持打开此网页的应用！",Toast.LENGTH_SHORT).show();
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
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){

					for(WebLoadAssert webLoadAssert:webLoadAssertList){
						if (url.contains(webLoadAssert.getName())){
							try {
								response = new WebResourceResponse(webLoadAssert.getMimeType(),"UTF-8",getAssets().open(webLoadAssert.getName()));
								L.e("hongliang","加载了："+webLoadAssert.getName());
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

				for(WebLoadAssert webLoadAssert:webLoadAssertList){
					if (request.getUrl().getPath().contains(webLoadAssert.getName())){
						try {
							response = new WebResourceResponse(webLoadAssert.getMimeType(),"UTF-8",getAssets().open(webLoadAssert.getName()));
							L.e("hongliang","加载了："+webLoadAssert.getName());
							return response;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return super.shouldInterceptRequest(view, request);
			}
		});
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if(!TextUtils.isEmpty(title)){
					tv_charge_title.setText(title);
				}else{
					if(TextUtils.isEmpty(WebViewActivity.this.title)){
						tv_charge_title.setText("火速sdk");
					}else{
						tv_charge_title.setText(WebViewActivity.this.title);
					}
				}
			}
		});
		wv.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				try{
					T.s(WebViewActivity.this,"已开始下载，可在通知栏查看进度");
					Intent intent = new Intent(WebViewActivity.this, CKGameService.class);
					intent.putExtra(CKGameService.DOWNLOAD_APK_URL,url);
					WebViewActivity.this.startService(intent);
				}catch (Exception e){
					Toast.makeText(WebViewActivity.this, "手机还没有安装支持打开此网页的应用！",Toast.LENGTH_SHORT).show();
				}
			}
		});
		webviewCompat(wv);
	}

	@Override
	public void onClick(View v) {
		if (v.getId()==iv_return.getId()) {
			String url = wv.getUrl();
			L.e(TAG,"当前页面的url="+url);
			if(url!=null&&(url.toLowerCase().contains(INDEX_URL_FLAG.toLowerCase()))){//是用户中心页面，直接finish
				finish();
			}else{//其它则直接返回用户中心
				WebBackForwardList webBackForwardList = wv.copyBackForwardList();
				L.e(TAG,"历史网页数量："+webBackForwardList.getSize());
				if(webBackForwardList.getSize()>1){
					String index_url = webBackForwardList.getItemAtIndex(0).getUrl();
					wv.goBackOrForward(1-webBackForwardList.getSize());
					L.e(TAG,"返回后加载的主页为："+index_url);
				}else {
					finish();
				}
			}
		}
	}
	/**
	 * 一些版本特性操作，需要适配、
	 * 
	 * @date 6/3
	 * @param mWebView webview
	 * @reason 在微蓝项目的时候遇到了 返回键 之后 wv显示错误信息
	 * */
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
		if(commonJsForWeb !=null){
			commonJsForWeb.onDestory();
		}
		if (wv!=null){
			wv.removeAllViews();
			wv = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(0,0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(commonJsForWeb !=null){
			commonJsForWeb.onActivityResult(requestCode,resultCode,data);
		}
	}

	public static void start(Context context, String titleName, String url, int windowType, int requestType) {
		if (!BaseAppUtil.isNetWorkConneted(context)) {
			T.s(context, "网络不通，请稍后再试！");
			return;
		}
		Intent starter = new Intent(context, WebViewActivity.class);
		starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_ANIMATION);
		starter.putExtra(TITLE_NAME, titleName);
		starter.putExtra(URL, url);
		starter.putExtra(WINDOW_TYPE, windowType);
		starter.putExtra(REQUEST_TYPE, requestType);
		context.startActivity(starter);
	}
}
