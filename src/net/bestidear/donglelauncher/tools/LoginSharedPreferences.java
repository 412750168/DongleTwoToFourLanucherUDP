package net.bestidear.donglelauncher.tools;

import android.app.Activity;
import android.content.SharedPreferences;

public class LoginSharedPreferences {
	
	private static final String NAME = "Bestidear_dongle";
	private static final String LOGIN = "Login";
	public static final String 	FIRST = "first_login";
	
	public static final String OLD_NETWORK_ID = "old_network_id";
	public static final String NEW_NETWORK_ID = "new_network_id";
	
	public static final String OLD_ROUTER = "old_router";
	public static final String NEW_ROUTER = "new_router";
	
	private Activity activity;
	
	SharedPreferences mySharedPreferences;
	SharedPreferences.Editor editor;
	
	public LoginSharedPreferences(Activity activity) {
		super();
		this.activity = activity;
		
	    mySharedPreferences = this.activity.getSharedPreferences (NAME,Activity.MODE_PRIVATE);
		editor=mySharedPreferences.edit();
	}

	// 第一次登录
	public void putLogin(){

		editor.putString(LOGIN,FIRST);
		editor.commit();
	}
	
	public void deleteLogin(){
		
		editor.putString(LOGIN, "");
		editor.commit();
	}
	
	public boolean isFirstLogin(){
		
		String loginStatus = mySharedPreferences.getString(LOGIN, "");
		if(loginStatus.equals(""))
			return true;
		else if(loginStatus.equals(FIRST))
			return false;
		return false;
	}
	
	//wifi 密码设置错误，进行重连
	public void putOldNetworkId(int id){
		
		editor.putInt(OLD_NETWORK_ID, id);
		editor.commit();
	}
	
	public int getOldNetworkId(){
		
		int id = mySharedPreferences.getInt(OLD_NETWORK_ID, -1);
		return id;
	}
	
	public void putNewNetworkId(int id){
		
		editor.putInt(NEW_NETWORK_ID, id);
		editor.commit();
	}
	
	public int getNewNetworkId(){
		
		int id = mySharedPreferences.getInt(NEW_NETWORK_ID, -1);
		return id;
	}
	
	
	public void putOldRouter(String router){
		
		editor.putString(OLD_ROUTER, router);
		editor.commit();
	}
	
	public String getOldRouter(){
		
		String router = mySharedPreferences.getString(OLD_ROUTER, "");
		return router;
	}
	
	public void putNewRouter(String router){
		
		editor.putString(NEW_ROUTER, router);
		editor.commit();
	}
	
	public String getNewRouter(){
		
		String router = mySharedPreferences.getString(NEW_ROUTER, "");
		return router;
	}
	
}
