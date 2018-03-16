package com.game.sdk.domain;

/**
 * author janecer 2014-3-29下午4:08:05 成功登录后 信息回调信息
 */
public class LogincallBack {

	public String user_token;
	public String mem_id;
	public Notice notice;

	public LogincallBack() {
	}

	public LogincallBack(String mem_id,String user_token) {
		this.user_token = user_token;
		this.mem_id = mem_id;
	}

	public LogincallBack(String mem_id,String user_token,Notice notice) {
		this.user_token = user_token;
		this.mem_id = mem_id;
		this.notice = notice;
	}
}
