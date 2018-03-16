package com.game.sdk.floatwindow;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.game.sdk.db.LoginControl;
import com.game.sdk.domain.WebRequestBean;
import com.game.sdk.http.HttpParamsBuild;
import com.game.sdk.http.SdkApi;
import com.game.sdk.so.SdkNative;
import com.game.sdk.ui.FloatDialogActivity;
import com.game.sdk.ui.FloatWebActivity;
import com.game.sdk.util.AnimationUtils;
import com.game.sdk.util.BaseAppUtil;
import com.game.sdk.util.DialogUtil;
import com.game.sdk.util.GsonUtil;
import com.game.sdk.util.MResource;
import com.game.sdk.util.ResourceUtils;

/**
 * 实名认证的浮点实现
 */
public class IdentifyFloatViewImpl implements IFloatView {
    private static boolean hasShow = false;
    private static boolean hasHideViewShow = false;
    SensorManager sm;
    Sensor sensor;
    boolean mGoUp;
    int lastLocationX = 0;
    int width;
    int height;
    protected static final String TAG = IdentifyFloatViewImpl.class.getSimpleName();
    private static final int UPTATE_INTERVAL_DISTANCE = 80;
    /**
     * 悬浮view
     **/
    private static IdentifyFloatViewImpl instance = null;
    private RelativeLayout mFloatLayout;
    private RelativeLayout hideview;
    private RelativeLayout float_item_user;
    private RelativeLayout float_item_identify;
    private WindowManager.LayoutParams wmParams;
    private WindowManager.LayoutParams hideParams;
    private RelativeLayout pop_menu;
    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    private ImageView mFloatView;
    ImageView mHideView;
    private Context mContext, context;
    private final int MOBILE_QUERY = 1;
    private final int MOBILE_QUERY_HIDE = 2;
    private boolean isleft = true;
    private int ViewRawX;
    private int ViewRawY;
    private int lastRawY = 0;
    private boolean needVibrate = false;
    private boolean beside = false;

    private IdentifyFloatViewImpl(Context context) {
        this.context = context;
        this.mContext = context.getApplicationContext();
        GetSensorManager(mContext);
    }

    /**
     * @param context
     * @return
     */
    public synchronized static IdentifyFloatViewImpl getInstance(Context context) {
        if (instance == null) {
            instance = new IdentifyFloatViewImpl(context);
        }
        return instance;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MOBILE_QUERY:
                    if (mWindowManager != null && pop_menu != null && hasShow) {
                        mWindowManager.removeView(pop_menu);
                        hasShow = false;
                    }
                    if (mFloatView != null) {
                        beside = true;
                        if (isleft) {
                            AnimationUtils.runToLeft(mFloatView, 2).start();
                        } else {
                            AnimationUtils.runToRight(mFloatView,2).start();
                        }
                    }
                    break;
            }
        }
    };

    private void createFloatView() {
        if (mFloatLayout == null) {
            try {
                wmParams = new WindowManager.LayoutParams();
                // 获取的是WindowManagerImpl.CompatModeWrapper
                mWindowManager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
//                Log.i(TAG, "mWindowManager--->" + mWindowManager);
                // 设置window type
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                // 设置图片格式，效果为背景透明
                wmParams.format = PixelFormat.RGBA_8888;
                // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
                wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                // 调整悬浮窗显示的停靠位置为左侧置顶
                wmParams.gravity = Gravity.LEFT | Gravity.TOP;
                // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
                wmParams.x = 0;
                wmParams.y = 0;
                // 设置悬浮窗口长宽数据
                wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // 获取浮动窗口视图所在布局
                mFloatLayout = (RelativeLayout) inflater.inflate(MResource
                        .getIdByName(mContext, MResource.LAYOUT, "ck_sdk_float_layout"), null);
                // 添加mFloatLayout
                mWindowManager.addView(mFloatLayout, wmParams);
                initUI();
            } catch (Exception e) {
            }
        }
    }

    private void createHideView() {
        if (hideview == null) {
            try {
                hideParams = new WindowManager.LayoutParams();
                hideParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                hideParams.format = PixelFormat.RGBA_8888;
                hideParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                hideParams.gravity = Gravity.BOTTOM;
                hideParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                hideParams.height = 410;
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                hideview = (RelativeLayout) inflater.inflate(MResource
                        .getIdByName(mContext, MResource.LAYOUT, "ck_float_hide_center"), null);
                mHideView = (ImageView) hideview.findViewById(ResourceUtils.getResourceId(mContext, "lyg_iv_hide"));
                mWindowManager.addView(hideview, hideParams);
                hasHideViewShow = true;
                mHideView.setImageResource(MResource.getIdByName(mContext, "drawable", "ck_hide_view"));
            } catch (Exception e) {
                Log.e(TAG, "e:" + e);
            }
        }
    }

    private long lastTime;

    private void initUI() {
        hasShow = false;
        width = mWindowManager.getDefaultDisplay().getWidth();
        height = mWindowManager.getDefaultDisplay().getHeight();
        String lastLocation = SdkNative.getLastFloatLocation(mContext);
        String[] last= new String[]{"0","0"};
        if(!lastLocation.isEmpty()){
            last = lastLocation.split("&");
        }

        if(Integer.valueOf(last[0])>width / 2){
            wmParams.x = width;
        }else {
            wmParams.x = 0;
        }
        wmParams.y = Integer.valueOf(last[1]);
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        // 浮动窗口按钮
        mFloatView = (ImageView) mFloatLayout.findViewById(MResource
                .getIdByName(mContext, "R.id.lyg_iv_float"));

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.alpha = 10;
                if (handler != null) {
                    handler.removeMessages(MOBILE_QUERY);
                }
                if (hasShow || beside) {
                    if (hasShow) {
                        mWindowManager.removeView(pop_menu);
                        hasShow = false;
                    }
                    return false;
                }
                // 刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTime = System.currentTimeMillis();
                        lastRawY = wmParams.y;
                        lastLocationX = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        ViewRawX = (int) event.getRawX()
                                - mFloatView.getMeasuredWidth() / 2;
                        ViewRawY = (int) event.getRawY()
                                - mFloatView.getMeasuredHeight() / 2 - 25;
                        long cha = System.currentTimeMillis() - lastTime;
                        if (cha > 120) {
                            wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                            wmParams.y = ViewRawY;
                        }
                        int distance = ViewRawX - lastLocationX;
                        if (Math.abs(distance) > UPTATE_INTERVAL_DISTANCE) {
                            createHideView();
                        }
                        if (hasHideViewShow) {
                            if ((ViewRawX > 300 && ViewRawX < width - 300) && ViewRawY > height / 2) {
                                mHideView.setImageResource(ResourceUtils.getDrawableId(mContext, "ck_hide_view2"));
                                if (!needVibrate) {
                                    Vibrate(mContext, 200);
                                }
                                needVibrate = true;
                            } else {
                                needVibrate = false;
                                mHideView.setImageResource(ResourceUtils.getDrawableId(mContext, "ck_hide_view"));
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        ViewRawX = (int) event.getRawX()
                                - mFloatView.getMeasuredWidth() / 2;
                        ViewRawY = (int) event.getRawY()
                                - mFloatView.getMeasuredHeight() / 2 - 25;
                        long dex = System.currentTimeMillis() - lastTime;
                        if (dex < 120) {
                            if (!hasShow) {
                                hasShow = true;
                                addPopMenu(isleft);
                            }
                        } else {
                            pullover(2000);
                        }
                        break;
                }
                return true;
            }
        });
        mFloatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beside) {
                    AnimationUtils.runBackFromBeside(mFloatView, 2).start();
                    beside = false;
                    handler.removeMessages(MOBILE_QUERY);
                    Message msg = handler.obtainMessage(MOBILE_QUERY);
                    handler.sendMessageDelayed(msg, 2000);
                }
            }
        });
        pullover(2000);
    }

    private void pullover(final int delayTime) {
        currentX = wmParams.x;
        wmParams.x = 0;
        isleft = true;
        if (hideview != null) {
            if ((ViewRawX > 300 || ViewRawX < width - 300) && ViewRawY > height / 2) {
                int showagain = SdkNative.getFloatTipFlag(mContext);
                if (showagain == 0) {
                    mFloatLayout.setVisibility(View.GONE);
                    DialogUtil.showFloatTipDialog(context, new DialogUtil.ConfirmDialogListener() {
                        @Override
                        public void ok(Dialog dialog) {
                            hideFloat();
                            sm.registerListener(mySensorListener, sensor,
                                    SensorManager.SENSOR_DELAY_NORMAL);
                        }

                        @Override
                        public void cancle() {
                            mFloatLayout.setVisibility(View.VISIBLE);
                            if (ViewRawX > width / 2) {
                                wmParams.x = width;
                                isleft = false;
                            }
                            moveToEdge(wmParams.x);
                            handler.removeMessages(MOBILE_QUERY);
                            Message msg = handler.obtainMessage(MOBILE_QUERY);
                            handler.sendMessageDelayed(msg, delayTime);
                        }
                    });
                } else {
                    sm.registerListener(mySensorListener, sensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                    hideFloat();
                }

            } else {
                if (ViewRawX > width / 2) {
                    wmParams.x = width;
                    isleft = false;
                }
                moveToEdge(wmParams.x);
                handler.removeMessages(MOBILE_QUERY);
                Message msg = handler.obtainMessage(MOBILE_QUERY);
                handler.sendMessageDelayed(msg, delayTime);
            }

            if (mWindowManager == null) {
                mWindowManager = (WindowManager) mContext
                        .getSystemService(mContext.WINDOW_SERVICE);
            }
            mWindowManager.removeView(hideview);
            hideview = null;
        } else {
            if (ViewRawX > width / 2) {
                wmParams.x = width;
                isleft = false;
            }
            moveToEdge(wmParams.x);
            handler.removeMessages(MOBILE_QUERY);
            Message msg = handler.obtainMessage(MOBILE_QUERY);
            handler.sendMessageDelayed(msg, delayTime);
        }

    }

    public static void Vibrate(final Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * 打开用户中心
     */
    public void openucenter() {
//        hideFloat();
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(new WebRequestBean()));
        FloatWebActivity.start(mContext, SdkApi.getWebUser(), ResourceUtils.getString(mContext,"ck_usercenter"), httpParamsBuild.getHttpParams().getUrlParams().toString(),
                httpParamsBuild.getAuthkey(), 0);
    }

    /**
     * 打开实名认证
     */
    public void openIdentify() {
        hideFloat();
        HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(new WebRequestBean()));
        FloatWebActivity.start(mContext, SdkApi.getWebIdentify(), ResourceUtils.getString(mContext,"ck_identify"),
                httpParamsBuild.getHttpParams().getUrlParams().toString(), httpParamsBuild.getAuthkey(), 0);
    }


    private View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == float_item_user.getId()) {
                HttpParamsBuild httpParamsBuild = new HttpParamsBuild(GsonUtil.getGson().toJson(new WebRequestBean()));
                FloatWebActivity.start(mContext, SdkApi.getWebUser(), ResourceUtils.getString(mContext,"ck_usercenter"), httpParamsBuild.getHttpParams().getUrlParams().toString(),
                        httpParamsBuild.getAuthkey(), 0);
                return;
            }
            if (v.getId() == float_item_identify.getId()) {
                openIdentify();
                return;
            }
        }
    };

    private WindowManager.LayoutParams popParams;

    private void addPopMenu(boolean isLeft) {
        if (pop_menu == null) {
            popParams = new WindowManager.LayoutParams();
            popParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            // 设置图片格式，效果为背景透明
            popParams.format = PixelFormat.RGBA_8888;
            // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
            popParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            // 调整悬浮窗显示的停靠位置为左侧置顶
            popParams.gravity = Gravity.LEFT | Gravity.TOP;
            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
            // 设置悬浮窗口长宽数据
            popParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            popParams.height = 120;
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 获取浮动窗口视图所在布局
            pop_menu = (RelativeLayout) inflater.inflate(MResource
                    .getIdByName(mContext, MResource.LAYOUT, "ck_float_menu"), null);
            float_item_user = (RelativeLayout) pop_menu.findViewById(MResource.getIdByName(mContext, "R.id.lyg_float_item_user"));
            float_item_identify = (RelativeLayout) pop_menu.findViewById(MResource.getIdByName(mContext, "R.id.lyg_float_item_identify"));
            float_item_user.setOnClickListener(onclick);
            float_item_identify.setOnClickListener(onclick);
        }
        popParams.y = wmParams.y;
        if (isLeft) {
            popParams.x = wmParams.x + mFloatView.getMeasuredWidth();
        } else {
            popParams.x = wmParams.x - 311 - mFloatView.getMeasuredWidth();
        }
        mWindowManager.addView(pop_menu, popParams);
        pullover(2000);
    }

    /**
     * 查看消息
     */
    public void checkMessages() {
        hideFloat();
        FloatDialogActivity.start(mContext, 1);
    }

    /**
     * 查看礼包
     */
    public void checkGifts() {
        hideFloat();
        FloatDialogActivity.start(mContext, 2);
    }

    // 移除悬浮窗口
    public void removeFloat() {
        hideFloat();
        instance = null;
    }

    // 显示悬浮窗口
    public void showFloat() {
        if (instance != null && LoginControl.isLogin() && BaseAppUtil.isApplicationBroughtToForground(mContext)) {
            createFloatView();
            pullover(2000);
        }
    }

    // 移除悬浮窗口
    public void hideFloat() {
        handler.removeCallbacksAndMessages(null);
        if (mFloatLayout != null && mWindowManager != null) {
            mWindowManager.removeView(mFloatLayout);
            mFloatLayout = null;
        }
        if (pop_menu != null && mWindowManager != null && hasShow) {
            mWindowManager.removeView(pop_menu);
            pop_menu = null;
        }
    }

    private int currentX = 0;
    private ValueAnimator moveToEdgeAnim;

    private void moveToEdge(int desX) {
        StringBuilder s = new StringBuilder();
        s.append(String.valueOf(wmParams.x)).append("&").append(String.valueOf(wmParams.y));
        SdkNative.setLastFloatLocation(mContext,s.toString());
        if (moveToEdgeAnim != null && moveToEdgeAnim.isRunning()) {
            moveToEdgeAnim.cancel();
        }
        moveToEdgeAnim = ValueAnimator.ofInt(currentX, desX);
        moveToEdgeAnim.setDuration(300);
        moveToEdgeAnim.setInterpolator(new BounceInterpolator());
        moveToEdgeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                wmParams.x = (int) animation.getAnimatedValue();
                try {
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        moveToEdgeAnim.start();
    }

    public void GetSensorManager(Context context) {
        sm = (SensorManager) context
                .getSystemService(Service.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGoUp = false;
    }

    public SensorEventListener mySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float[] values = event.values;
            float ax = values[0];
            float ay = values[2];


            double g = Math.sqrt(ax * ax + ay * ay);
            double cos = ay / g;
            if (cos > 1) {
                cos = 1;
            } else if (cos < -1) {
                cos = -1;
            }
            if (cos < -0.96) {
                mGoUp = true;
            }
            if (cos > 0.96 && mGoUp) {
                mGoUp = false;
                ViewRawY = 100;
                showFloat();
                sm.unregisterListener(mySensorListener);
            }

        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
