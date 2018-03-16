package com.game.sdk.pay.impl;

import android.app.Activity;

import com.game.sdk.domain.NotProguard;
import com.game.sdk.domain.PayResultBean;
import com.game.sdk.pay.ILYPay;
import com.game.sdk.pay.IPayListener;

/**
 * Created by liu hong liang on 2017/2/28.
 * 现在支付的实现类
 */

public class IpaynowPayIml extends ILYPay {
    private static final String TAG = IpaynowPayIml.class.getSimpleName();
    private Activity mActivity;
    private String orderId;
    private float money;
    private IPayListener iPayListener;

    @Override
    @NotProguard
    protected void startPay(Activity activity, IPayListener listener, float money, PayResultBean payResultBean) {
        this.iPayListener = listener;
        this.money = money;
        this.mActivity = activity;
        this.orderId = payResultBean.getOrder_id();
        listener.loadWX(payResultBean.getOrder_id(), payResultBean.getReal_amount(), payResultBean.getToken());
    }
}
