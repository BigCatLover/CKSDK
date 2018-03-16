package com.game.sdk.db.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.game.sdk.db.DBHelper;

public class AgentNamedao {

	public static final String TABLENAME = "agentname";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String AGENTNAME = "agentname";
	// public static final String ISREPWD="isrepwd";//0已经修改过密码，1表示没有修改过密码
	private static final String TAG = "UserLoginInfodao";

	private DBHelper dbHelper = null;
	private static AgentNamedao userlogininfodao;
	private Context aContext;

	private AgentNamedao(Context context) {
		// dbHelper = new DBHelper(context, "userlogin.db", null, 1);
		this.aContext = context;
	}

	public static AgentNamedao getInstance(Context context) {
		if (null == userlogininfodao) {
			userlogininfodao = new AgentNamedao(context);
		}
		return userlogininfodao;
	}

	public void saveAgentname(String agentname) {
		if (!TextUtils.isEmpty(agentname)) {
			SharedPreferences sp = aContext.getSharedPreferences("appinfo",
					Context.MODE_PRIVATE);
			sp.edit().putString(AGENTNAME, agentname).commit();
		}
	}

	public String getAgentname() {
		SharedPreferences sp = aContext.getSharedPreferences("appinfo",
				Context.MODE_PRIVATE);
		String agent = sp.getString(AGENTNAME, "");
		return agent;
	}
}
