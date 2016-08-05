package net.bestidear.donglelauncher.fragment;

import java.util.List;

import org.w3c.dom.Text;

import net.bestidear.donglelauncher.InstructionsActivity;
import net.bestidear.donglelauncher.R;
import net.bestidear.donglemain.activity.allAppActivity;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFragment extends Fragment {

	private View view;

	private ImageView imageApp;
	private ImageView imageInternet;
	private ImageView imageGuid;
	private ImageView imageSetting;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.main_launcher, container, false);
		imageApp = (ImageView) view.findViewById(R.id.imageview_app);
		imageInternet = (ImageView) view
				.findViewById(R.id.imageview_internet_setting);
		imageSetting = (ImageView) view.findViewById(R.id.imageview_setting);
		imageGuid = (ImageView) view.findViewById(R.id.imageview_guid);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		imageApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * Intent intent = new Intent(getActivity(),
				 * allAppActivity.class); startActivity(intent);
				 */
				Intent intent = new Intent();
				ComponentName componentName = new ComponentName(
						"net.bestidear.donglelauncherapp",
						"net.bestidear.donglelauncherapp.activity.allAppActivity");
				intent.setComponent(componentName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (isIntentAvailable(getActivity(), intent))
					startActivity(intent);
			}
		});

		imageGuid.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						InstructionsActivity.class);
				startActivity(intent);
			}
		});

		imageInternet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				ComponentName componentName = new ComponentName(
						"com.android.mbox_settings",
						"com.android.mbox_settings.NetworkActivity");
				intent.setComponent(componentName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (isIntentAvailable(getActivity(), intent))
					startActivity(intent);
			}
		});

		imageSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				ComponentName componentName = new ComponentName(
						"com.android.mbox_settings",
						"com.android.mbox_settings.SystemSettingActivity");
				intent.setComponent(componentName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				if (isIntentAvailable(getActivity(), intent))
					startActivity(intent);
			}
		});

	}

	public boolean isIntentAvailable(Context context, Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

}
