package com.game.sdk.pay;

import android.app.Activity;
import android.content.Intent;

import com.game.sdk.domain.NotProguard;
import com.game.sdk.domain.PayResultBean;

/**
 * Created by Liuhongliangsdk on 2016/10/30.
 */
@NotProguard
public abstract class ILYPay {
    protected abstract void startPay(Activity activity, IPayListener listener, float money, PayResultBean payResultBean);

    /**
     * 部分支付客户端获取不到支付结果，需要在页面再次可操作的时候，查询支付结果
     */
    protected void onResume(){

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    }
    //部分支付需要在activity销毁的时候执行操作
    protected void onDestory(){

    }
}
