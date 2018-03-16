package com.game.sdk.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.game.sdk.CKGameManager;
import com.game.sdk.db.LoginControl;
import com.game.sdk.domain.NotProguard;
import com.game.sdk.log.L;
import com.game.sdk.so.SdkNative;
import com.game.sdk.ui.CkLoginActivity;

/**
 * author janecer 2014-7-23上午9:41:45
 */
@NotProguard
public class DialogUtil {
    private static final String TAG = DialogUtil.class.getSimpleName();
    private static Dialog dialog;// 显示对话框
    private static ImageView iv_pd;// 待旋转动画
    private static TextView tv_msg;// 消息
    private static View view;

    /**
     * 显示对话框
     */
    private static void init(Context context) {
        dialog = new Dialog(context, MResource.getIdByName(context, "style",
                "ck_sdk_customDialog"));
        view = LayoutInflater.from(context).inflate(
                MResource.getIdByName(context, "layout", "ck_sdk_dialog_loading"), null);
        iv_pd = (ImageView) view.findViewById(MResource.getIdByName(context,
                "id", "lyg_sdk_iv_circle"));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        tv_msg = (TextView) view.findViewById(MResource.getIdByName(context,
                "id", "lyg_sdk_tv_msg"));
        dialog.setContentView(view);
    }

    /**
     * 显示对话框
     *
     * @param context
     * @param msg
     */
    public static void showDialog(Context context, String msg) {
        try {
            init(context);
            tv_msg.setText(msg);// 显示进度信息
            if (null != dialog && !dialog.isShowing()) {
                iv_pd.startAnimation(rotaAnimation());
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialog(Context ctx, boolean cansable, String msg) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        init(ctx);
        tv_msg.setText(msg);// 显示进度信息
        if (null != dialog && !dialog.isShowing()) {
            dialog.setCancelable(cansable);
            iv_pd.startAnimation(rotaAnimation());
            dialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    public static void dismissDialog() {
        try {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                iv_pd.clearAnimation();
                dialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 旋转动画
     *
     * @return
     */
    public static Animation rotaAnimation() {
        RotateAnimation ra = new RotateAnimation(0, 355,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        ra.setInterpolator(new LinearInterpolator());
        ra.setDuration(888);
        ra.setRepeatCount(-1);
        ra.setStartOffset(0);
        ra.setRepeatMode(Animation.RESTART);
        return ra;
    }

    /**
     * 判断对话框是否是显示状态
     *
     * @return
     */
    public static boolean isShowing() {
        if (null != dialog) {
            return dialog.isShowing();
        }
        return false;
    }

    public static void showSuccessDialog(final Context context, final String accountname, final boolean isFirstLogin) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final Dialog dialog = new Dialog(context, MResource.getIdByName(context,
                            "style", "ck_sdk_dialog_bg_style"));
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    View notcieView = LayoutInflater.from(context)
                            .inflate(MResource.getIdByName(context, "layout", "ck_include_normal_success_enter"), null);
                    TextView title = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "title"));
                    TextView tip = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "tip"));
                    TextView name = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "acountname"));
                    if (isFirstLogin) {
                        title.setText(ResourceUtils.getString(context, "ck_regist_success"));

                    } else {
                        title.setText(ResourceUtils.getString(context, "ck_login_success"));
                    }
                    tip.setText(ResourceUtils.getString(context, "ck_welcome"));
                    name.setText(context.getString(ResourceUtils.getStringId("ck_loginsuccess_title"), accountname));

                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(notcieView);

                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                                ((CkLoginActivity) context).callBackFinish();
                            }
                        }
                    }, 2000);
                }
            });
        }
    }

    public static void showVisitorSuccessDialog(final Context context, final String accountname) {
        if (context instanceof Activity) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final Dialog dialog = new Dialog(context, MResource.getIdByName(context,
                            "style", "ck_sdk_dialog_bg_style"));
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    View notcieView = LayoutInflater.from(context)
                            .inflate(MResource.getIdByName(context, "layout", "ck_include_visitor_success_enter"), null);
                    TextView tip = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "tip"));
                    TextView name = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "accountname"));
                    tip.setText(ResourceUtils.getString(context, "ck_welcome"));
                    name.setText(context.getString(ResourceUtils.getStringId("ck_visitor_loginsuccess_title"), accountname));


                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(notcieView);

                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {
                                dialog.dismiss();
                                ((CkLoginActivity) context).callBackFinish();
                            }
                        }
                    }, 2000);
                }
            });
        }
    }

    public static void showVisitorUpgradeTipDialog(final Context context, final String content, final String account) {
        if (context instanceof Activity) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final Dialog dialog = new Dialog(context, MResource.getIdByName(context,
                            "style", "ck_sdk_dialog_bg_style"));
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    View notcieView = LayoutInflater.from(context)
                            .inflate(MResource.getIdByName(context, "layout", "ck_include_upgrade_tip"), null);
                    TextView tip = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "tip"));
                    TextView name = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "accountname"));
                    tip.setText(content);
                    name.setText("游客：" + account);
                    TextView ok = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "ok"));
                    TextView cancle = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "cancel"));

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null) {
                                dialog.dismiss();
                                CKGameManager.setIsUpgradeWhenPay(true);
                                CKGameManager.getInstance().showRegist();
                            }
                        }
                    });
                    cancle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null) {
                                dialog.dismiss();
                                LoginControl.setUpgradeforpay(false);
                            }
                        }
                    });

                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(notcieView);

                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }
            });
        }
    }

    //type :1 下载；2 安装 3 下载中

    public static void showGameUpdateDialog(final Context context, final int type, final ConfirmDialogListener listener) {
        if (context instanceof Activity) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final Dialog dialog = new Dialog(context, MResource.getIdByName(context,
                            "style", "ck_sdk_dialog_bg_style"));
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    View notcieView = LayoutInflater.from(context)
                            .inflate(MResource.getIdByName(context, "layout", "ck_include_update_tip"), null);
                    final TextView tip = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "tip"));
                    final TextView btn = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                            "id", "btn"));
                    if (type == 2) {
                        tip.setText(ResourceUtils.getString(context, "ck_install_tip"));
                        btn.setText(ResourceUtils.getString(context, "ck_install"));
                    } else if (type == 1) {
                        tip.setText(ResourceUtils.getString(context, "ck_update_game_tip"));
                        btn.setText(ResourceUtils.getString(context, "ck_update_game"));
                    } else if (type == 3) {
                        btn.setVisibility(View.INVISIBLE);
                        tip.setText(ResourceUtils.getString(context, "ck_downloading_tip"));
                    }

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) {
                                listener.ok(dialog);
                            }
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(notcieView);

                    if (!dialog.isShowing()) {
                        dialog.show();
                    }

                }
            });
        }
    }

    public static void showFloatTipDialog(final Context context, final ConfirmDialogListener listener) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Dialog dialog = new Dialog(context, MResource.getIdByName(context,
                                "style", "ck_sdk_dialog_bg_style"));
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        View notcieView = LayoutInflater.from(context)
                                .inflate(MResource.getIdByName(context, MResource.LAYOUT,"ck_show_float_dialog"), null);
                        TextView ok = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                                "id", "hide"));
                        TextView cancle = (TextView) notcieView.findViewById(MResource.getIdByName(context,
                                "id", "cancle"));
                        final CheckBox showagain = (CheckBox) notcieView.findViewById(MResource.getIdByName(context,
                                "id", "notip_again"));
                        ImageView gif = (ImageView) notcieView.findViewById(MResource.getIdByName(context,
                                "id", "tip_gif"));
                        showFloatTipAnimation(gif);

                        showagain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (showagain.isChecked()) {
                                    SdkNative.setFloatTipFlag(context, 1);
                                } else {
                                    SdkNative.setFloatTipFlag(context, 0);
                                }
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                if (listener != null) {
                                    listener.ok(dialog);
                                }
                            }
                        });
                        cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                if (listener != null) {
                                    listener.cancle();
                                }
                            }
                        });
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setContentView(notcieView);

                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    } catch (Exception e) {
                        L.d(TAG, e);
                    }

                }
            });
        }
    }

    public interface ConfirmDialogListener {
        void ok(Dialog dialog);

        void cancle();
    }

    private static void showFloatTipAnimation(ImageView view) {
        AnimationDrawable animationDrawable = (AnimationDrawable) view.getDrawable();
        animationDrawable.start();
    }
}
