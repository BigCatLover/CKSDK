package com.game.sdk.listener;

import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.NotProguard;

/**
 * author janecer 2014-3-29上午10:12:47
 */
@NotProguard
public interface OnLoginListener {
	/**
	 * 成功登录后的回调
	 * @param logincallback
	 */
	@NotProguard
	void loginSuccess(LogincallBack logincallback);

	/**
	 * 登录失败的回调 有可能是用户名与密码不正确，也有可能是服务端临时出问题
	 * @param errorMsg 登录失败时返回的提示
	 */
	@NotProguard
	void loginError(LoginErrorMsg errorMsg);

}
