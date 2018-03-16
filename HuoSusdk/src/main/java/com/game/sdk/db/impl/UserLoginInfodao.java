package com.game.sdk.db.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;
import java.util.List;

import com.game.sdk.db.DBHelper;
import com.game.sdk.domain.UserInfo;
import com.game.sdk.util.des.Des3;


public class UserLoginInfodao {

	public static final String TABLENAME = "userlogin";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	// public static final String ISREPWD="isrepwd";//0已经修改过密码，1表示没有修改过密码
	private static final String TAG = "UserLoginInfodao";
	private static final String LOGIN_FLAG = "loginFlag";

	private DBHelper dbHelper = null;
	private static UserLoginInfodao userlogininfodao;

	private UserLoginInfodao(Context context) {
		
		
		dbHelper = new DBHelper(context, null, 2);
	}

	public static UserLoginInfodao getInstance(Context context) {
		if (null == userlogininfodao) {
			userlogininfodao = new UserLoginInfodao(context);
		}
		return userlogininfodao;
	}

	public static int getBindFlag(Context context, String user) {
		SharedPreferences sf = context.getSharedPreferences(user,
				Context.MODE_PRIVATE);
		return sf.getInt(LOGIN_FLAG, 0);
	}

	public static void saveBindFlag(Context context, String user, int flag) {
		SharedPreferences sf = context.getSharedPreferences(user,
				Context.MODE_PRIVATE);
		sf.edit().putInt(LOGIN_FLAG, flag).commit();
	}

	
	
	/**
	 * @return
	 */
	public List<UserInfo> getUserLoginInfo() {
		List<UserInfo> userLogininfos = null;
		SQLiteDatabase r_db = dbHelper.getReadableDatabase();
		if (r_db.isOpen()) {
			Cursor cursor = r_db.rawQuery("select * from " + TABLENAME, null);
			userLogininfos = new ArrayList<UserInfo>();
			UserInfo ulinfo = null;

			String username;
			String pwd;
			int isrepwd;
			try {
				if (cursor.moveToLast()) {
					ulinfo = new UserInfo();
					username = cursor
							.getString(cursor.getColumnIndex(USERNAME));
					pwd = cursor.getString(cursor.getColumnIndex(PASSWORD));
				
					// isrepwd=cursor.getInt(cursor.getColumnIndex(ISREPWD));
					ulinfo.username = username;
					ulinfo.password = pwd;
					ulinfo.password = ulinfo.password.substring(1,
							ulinfo.password.length());// "0000123 变成123问题"
					// ulinfo.isrpwd=isrepwd;
					ulinfo.username= Des3.decode(ulinfo.username);
					ulinfo.password= Des3.decode(ulinfo.password);
					userLogininfos.add(ulinfo);
				}
			} catch (Exception e) {

			}

			while (cursor.moveToPrevious()) {
				ulinfo = new UserInfo();
				username = cursor.getString(cursor.getColumnIndex(USERNAME));
				pwd = cursor.getString(cursor.getColumnIndex(PASSWORD));
				// isrepwd=cursor.getInt(cursor.getColumnIndex(ISREPWD));
				ulinfo.username = username;
				ulinfo.password = pwd;
				ulinfo.password = ulinfo.password.substring(1,
						ulinfo.password.length());// "0000123 变成123问题"
				// ulinfo.isrpwd=isrepwd;
				ulinfo.username=Des3.decode(ulinfo.username);
				ulinfo.password=Des3.decode(ulinfo.password);
				userLogininfos.add(ulinfo);
				ulinfo = null;
			}
			cursor.close();
		}
		r_db.close();
		r_db = null;
		return userLogininfos;
	}

	/**
	 * @param username
	 * @param pwd
	 */
	public void saveUserLoginInfo(String username, String pwd) {
		
		username= Des3.encode(username);
		pwd= Des3.encode(pwd);
		deleteUserLoginByName(username);
		SQLiteDatabase w_db = dbHelper.getWritableDatabase();
		if (w_db.isOpen()) {
			w_db.execSQL("insert into " + TABLENAME + "(" + USERNAME + ","
					+ PASSWORD + ") values(?,?)", new Object[] { username,
					"@" + pwd });
		}
		w_db.close();
		w_db = null;
	}

	/**
	 * @param username
	 * @return true
	 */
	public boolean findUserLoginInfoByName(String username) {
		username= Des3.encode(username);
		boolean flag = false;
		;
		SQLiteDatabase r_db = dbHelper.getReadableDatabase();
		if (r_db.isOpen()) {
			Cursor cursor = r_db.rawQuery("select * from " + TABLENAME
					+ " where " + USERNAME + "=?", new String[] { username });
			if (cursor.moveToNext()) {
				flag = true;
			}
			cursor.close();
			cursor = null;
		}
		r_db.close();
		r_db = null;
		return flag;
	}

	/**
	 * @param username
	 */
	public void deleteUserLoginByName(String username) {
		username= Des3.encode(username);
		SQLiteDatabase w_db = dbHelper.getWritableDatabase();
		if (w_db.isOpen()) {
			w_db.execSQL("delete from " + TABLENAME + " where " + USERNAME
					+ "=?", new String[] { username });
		}
		w_db.close();
		w_db = null;
	}

	/**
	 * 根据用户用去更新密码
	 * 
	 * @param username
	 * @param pwd
	 */
	/*
	 * public void updateUserLoginByName(String username,String pwd,int
	 * isrepwd){ SQLiteDatabase w_db=dbHelper.getWritableDatabase();
	 * if(w_db.isOpen()){
	 * w_db.execSQL("update "+TABLENAME+" set "+ISREPWD+"=?,"+
	 * PASSWORD+"=? where "+USERNAME+"=?",new Object[]{isrepwd,pwd,username});
	 * Logger.msg(
	 * "update "+TABLENAME+" set "+ISREPWD+"=? ,"+PASSWORD+"=? where "
	 * +USERNAME+"=?"); } w_db.close(); w_db=null; }
	 */

	/**
	 * 根据用户名获取密码
	 * 
	 * @param username
	 * @return
	 */
	public String getPwdByUsername(String username) {
		username= Des3.encode(username);
		SQLiteDatabase r_db = dbHelper.getReadableDatabase();
		String pwd = "";
		if (r_db.isOpen()) {
			Cursor cursor = r_db.rawQuery("select * from " + TABLENAME
					+ " where " + USERNAME + " =?", new String[] { username });
			if (cursor.moveToNext()) {
				pwd = cursor.getString(cursor.getColumnIndex(PASSWORD));
				pwd = pwd.substring(1, pwd.length());// "0000123 变成123问题"
			}
			cursor.close();
			cursor = null;
		}
		r_db.close();
		r_db = null;

		return Des3.decode(pwd);
	}

	/**
	 * 获取上次登录成功的用户信息
	 * 
	 * @return
	 */
	public UserInfo getUserInfoLast() {
		UserInfo ulinfo = new UserInfo();
		SQLiteDatabase r_db = dbHelper.getReadableDatabase();
		if (r_db.isOpen()) {
			Cursor cursor = r_db.rawQuery("select * from  " + TABLENAME, null);
			if (cursor.moveToLast()) {
				String username = cursor.getString(cursor
						.getColumnIndex(USERNAME));
				String pwd = cursor.getString(cursor.getColumnIndex(PASSWORD));
				// int isrepwd=cursor.getInt(cursor.getColumnIndex(ISREPWD));
		
				
				ulinfo.username = username;
				ulinfo.password = pwd;
				ulinfo.password = ulinfo.password.substring(1,
						ulinfo.password.length());// "0000123 变成123问题"
				// ulinfo.isrpwd=isrepwd;
				ulinfo.username=Des3.decode(ulinfo.username);
				ulinfo.password=Des3.decode(ulinfo.password);
			}
			cursor.close();
			cursor = null;
		}
		r_db.close();
		r_db = null;
		return ulinfo;
	}
}
