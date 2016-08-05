package net.bestidear.donglelauncher.fragment;


import net.bestidear.donglelauncher.R;
import net.bestidear.donglelauncher.R.id;
import net.bestidear.donglelauncher.R.layout;
import net.bestidear.donglelauncher.R.string;
import net.bestidear.donglelauncher.network.SettingWifiThread;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingFragment extends Fragment{
	
	private View view;
	private TextView textMode;
	private TextView textSSID;
	private TextView textPassOrIP;
	private TextView textApMode;
	private TextView textApSSID;
	private TextView textAPPass;
	private ImageView QRCodeImageView = null;
	private TextView QRCodeGuide = null;
	private LinearLayout linearLayout = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view  =  inflater.inflate(R.layout.setting_launcher,container,false);
		textMode = (TextView) view.findViewById(R.id.textview_mode);
		textSSID = (TextView) view.findViewById(R.id.textview_ssid);
		textPassOrIP = (TextView) view.findViewById(R.id.textview_pass_or_ip);
		
		linearLayout = (LinearLayout) view.findViewById(R.id.linerlayout_select_info);
		
		textApMode = (TextView)view.findViewById(R.id.textview_ap_mode);
		textApSSID = (TextView)view.findViewById(R.id.textview_ap_ssid);
		textAPPass = (TextView)view.findViewById(R.id.textview_ap_pass);

		QRCodeImageView = (ImageView) view.findViewById(R.id.imageview_QR);
		QRCodeGuide = (TextView) view.findViewById(R.id.textview_QR);
		QRCodeGuide.setText(R.string.DONGLE_guide);
		return view;
	}
	
	public void setLinerLayout(){
		if(linearLayout != null){
			linearLayout.setBackgroundResource(R.drawable.internet_select_info_bg);
			linearLayout.refreshDrawableState();
			linearLayout.invalidate();
		}
	}
	
	public void updateView(String mode, String ssid, String passOrIp) {
		if (mode.equals(Integer.toString(SettingWifiThread.AP)))
			textMode.setText("Mode: " + "AP");
		else
			textMode.setText("Mode: " + "ROUTER");

		textSSID.setText("SSID:" + ssid);
		textPassOrIP.setText("iP: " + passOrIp);
	}
	
	public void updateAPView(String mode, String ssid, String passOrIp) {
		if (mode.equals(Integer.toString(SettingWifiThread.AP)))
			textApMode.setText("Mode: " + "AP");
		else
			textApMode.setText("Mode: " + "ROUTER");

		textApSSID.setText("SSID:" + ssid);
		textAPPass.setText("PASSWORD: " + passOrIp);
	}
	
	public void setQRCodeImageView(Bitmap bmp){
		QRCodeImageView.setImageBitmap(bmp);
	}
	
	
}
