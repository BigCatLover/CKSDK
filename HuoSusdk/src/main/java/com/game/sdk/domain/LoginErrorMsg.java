package com.game.sdk.domain;

/**
 * author janecer 2014-3-29下午4:05:49
 * 
 * 用户充值成功回调消息体
 */
public class LoginErrorMsg {

	public int code;// 登录失败的状态码
	public String msg;// 登录失败的消息提示

	public LoginErrorMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
