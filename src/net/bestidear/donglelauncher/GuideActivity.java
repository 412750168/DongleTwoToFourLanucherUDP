package net.bestidear.donglelauncher;

import net.bestidear.donglelauncher.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class GuideActivity extends Activity {

	private Button button_guide;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.guide);

		Intent launcherIntent = new Intent(GuideActivity.this,
				LauncherActivity.class);
		startActivity(launcherIntent);
		/*
		 * button_guide = (Button)findViewById(R.id.button_guide);
		 * button_guide.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub
		 * 
		 * } });
		 */
		finish();

	}
}
