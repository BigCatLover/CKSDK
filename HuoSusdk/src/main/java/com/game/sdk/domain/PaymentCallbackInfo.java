package com.game.sdk.domain;

/**
 * author janecer 2014-3-29下午4:10:11 用户充值成功回调消息体
 */
public class PaymentCallbackInfo {
	public String msg;// 回调消息提示

	public float money;// 充值金额数

	public PaymentCallbackInfo(String msg, float money) {
		this.msg = msg;
		this.money = money;
	}

	public PaymentCallbackInfo() {

	}
}
