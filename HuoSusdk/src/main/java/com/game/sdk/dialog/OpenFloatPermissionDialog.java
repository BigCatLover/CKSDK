package com.game.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.game.sdk.util.DimensionUtil;
import com.game.sdk.util.MResource;

/**
 * Created by 刘红亮 on 2015/10/30 14:31.
 */
public class OpenFloatPermissionDialog {
    private static final String TAG = OpenFloatPermissionDialog.class.getSimpleName();
    private Dialog updateDialog;
    private ConfirmDialogListener mlistener;
    private static final long MAX_HINT_DELAY_TIME=60*1000;//每隔一分钟提示一次
    private static long beforeOpenTime=-1;//上次打开的时间
    private static int openPermissionCount=0;
    public void  showDialog(Context context, boolean showCancel, String content, ConfirmDialogListener listener){
        synchronized(OpenFloatPermissionDialog.class){
            if(openPermissionCount>0){//每次打开应用只提示一次
                return;
            }else{
                openPermissionCount++;
            }
            dismiss();
            this.mlistener=listener;
            View dialogview = LayoutInflater.from(context).inflate(MResource.getIdByName(context,MResource.LAYOUT,"ck_sdk_dialog_open_float"), null);
            updateDialog = new Dialog(context,MResource.getIdByName(context,"R.style.ck_sdk_dialog_bg_style"));
            //设置view
            updateDialog.setContentView(dialogview);
            updateDialog.setCanceledOnTouchOutside(false);
            //dialog默认是环绕内容的
            //通过window来设置位置、高宽
            Window window = updateDialog.getWindow();
            WindowManager.LayoutParams windowparams = window.getAttributes();
            int margin=(int)(context.getResources().getDimension(MResource.getIdByName(context, "R.dimen.ckgame_activity_horizontal_margin")));
            windowparams.width = DimensionUtil.getWidth(context)-2* margin;
            TextView btok = (TextView) dialogview.findViewById(MResource.getIdByName(context,"R.id.lyg_sdk_confirm_tv"));
            TextView btcancel = (TextView) dialogview.findViewById(MResource.getIdByName(context,"R.id.lyg_sdk_cancel_tv"));
            TextView text= (TextView) dialogview.findViewById(MResource.getIdByName(context,"R.id.lyg_sdk_content_text"));
            if(!TextUtils.isEmpty(content)){
                text.setText(content);
            }
            if(showCancel){
                btcancel.setVisibility(View.VISIBLE);
            }else{
                btcancel.setVisibility(View.GONE);
            }
            btok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mlistener!=null){
                        mlistener.ok();
                    }

                    dismiss();
                }
            });
            btcancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(mlistener!=null){
                                                    mlistener.cancel();
                                                }
                                                dismiss();
                                            }
                                        }
            );
            updateDialog.show();
        }
    }
    public void dismiss(){
        if(updateDialog !=null){
            updateDialog.dismiss();
            mlistener=null;
        }
    }
    public interface ConfirmDialogListener{
        void ok();
        void cancel();

    }
}
