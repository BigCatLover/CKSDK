package com.game.sdk.domain;

public class UserInfo  {
	public String username;//
	public String mem_id;//
	public String user_token;//
	public String password;//
	public String newpassword;//
	public int isrpwd = 0;// 0已经修改过密码，1表示没有修改过密码

	public int device = 2;// 1为pc端，2为Android端 3为ios
	public String imeil;//
	public String deviceinfo;//
	public String agent;//
	public String sendcode="";
	public int  issend=0;
	public String nickname = "";
	public String img = "";
	public String email = "";

}
