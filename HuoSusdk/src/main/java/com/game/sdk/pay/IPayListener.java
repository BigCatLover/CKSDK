package com.game.sdk.pay;

/**
 * Created by Liuhongliangsdk on 2016/10/30.
 */

public interface IPayListener {
    void paySuccess(String orderId, float money);
    void payFail(String orderId, float money,boolean queryOrder, String msg);
    void loadWX(String orderId, float money,String url);
}
