package net.bestidear.donglelauncher;

import net.bestidear.donglelauncher.tools.LoginSharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity{

	private LoginSharedPreferences loginSharedPreferences;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		loginSharedPreferences = new LoginSharedPreferences(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// first login : select to guide or main launcher
		if(loginSharedPreferences.isFirstLogin()){
			Intent guideIntent = new Intent(MainActivity.this, GuideActivity.class);
			startActivity(guideIntent);
		}else {
			Intent launcherIntent = new Intent(MainActivity.this, LauncherActivity.class);
			startActivity(launcherIntent);
		}
		
	}
	
	

}
