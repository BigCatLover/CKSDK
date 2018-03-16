package com.game.sdk.pay.impl;

import android.app.Activity;

import com.game.sdk.domain.PayResultBean;
import com.game.sdk.pay.ILYPay;
import com.game.sdk.pay.IPayListener;

/**
 * Created by zhanglei on 2018/3/5.
 */
public class PayPalIml extends ILYPay {
    private Activity mActivity;
    private String orderId;
    private float money;
    private IPayListener iPayListener;
    @Override
    protected void startPay(Activity activity, IPayListener listener, float money, PayResultBean payResultBean) {

    }
}
