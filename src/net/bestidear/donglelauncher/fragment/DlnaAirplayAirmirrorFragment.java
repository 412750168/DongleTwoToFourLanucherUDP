package net.bestidear.donglelauncher.fragment;


import net.bestidear.donglelauncher.R;
import net.bestidear.donglelauncher.network.SettingWifiThread;
import net.bestidear.donglelauncher.tools.SettingDeviceName;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DlnaAirplayAirmirrorFragment extends Fragment {

	private View view;

	private TextView text_mode_ap;
	private TextView text_ap_ssid;
	private TextView text_ap_password;

	private TextView text_mode_router;
	private TextView text_router_ssid;
	private TextView text_router_ip;
	
	private ImageView image_dlna;
	private ImageView image_airplay;
	private ImageView image_airmirror;

	private TextView text_dlna_name;
	private TextView text_airplay_name;
	private TextView text_airmirror_name;

	private SettingDeviceName setDeviceName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(R.layout.dlna_airplay_airmirror_launcher,
				container, false);

		text_mode_ap = (TextView) view.findViewById(R.id.textview_ap_mode);
		text_ap_ssid = (TextView) view.findViewById(R.id.textview_ap_ssid);
		text_ap_password = (TextView) view
				.findViewById(R.id.textview_ap_password);

		text_mode_router = (TextView) view
				.findViewById(R.id.textview_router_mode);
		text_router_ssid = (TextView) view
				.findViewById(R.id.textview_router_ssid);
		text_router_ip = (TextView) view.findViewById(R.id.textview_router_ip);

		text_dlna_name = (TextView) view.findViewById(R.id.textview_dlna_name);
		
		text_airplay_name = (TextView) view
				.findViewById(R.id.textview_airplay_name);
		

		text_airmirror_name = (TextView) view
				.findViewById(R.id.textview_airmirror_name);	

		image_dlna = (ImageView) view.findViewById(R.id.imageview_dlna);
		image_airplay = (ImageView) view.findViewById(R.id.imageview_airplay);
		image_airmirror = (ImageView) view.findViewById(R.id.imageview_airmirror);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setDeviceName = new SettingDeviceName(getActivity());
	}

	// view
	public void setApMode(String ap, String ssid, String password) {
		if (ap.equals(Integer.toString(SettingWifiThread.AP)))
			text_mode_ap.setText("AP");

		text_ap_ssid.setText(ssid);
		text_ap_password.setText(password);
	}

	public void setRouter(String router, String ssid, String ip) {
		if (router.equals(Integer.toString(SettingWifiThread.ROUTER)))
			text_mode_router.setText("Router");
		text_router_ssid.setText(ssid);
		text_router_ip.setText(ip);
	}

	public void setDlna(String dlnaName) {
		text_dlna_name.setText(dlnaName);
	}

	public void setAirplay(String airplayName) {
		text_airplay_name.setText(airplayName);
	}

	public void setAirmirror(String airmirrorName) {
		text_airmirror_name.setText(airmirrorName);
	}

	// function

	public void changeAirplayName(String strDeviceName) {
		Intent intent = new Intent("airplay.amlogic.changeName");
		intent.putExtra("DEVNAME", strDeviceName);
		getActivity().sendBroadcast(intent);
	}

	public void changeDLnaName(String strDeviceName) {
		Intent intent = new Intent("dlna.amlogic.changeName");
		intent.putExtra("DEVNAME", strDeviceName);
		//getActivity().sendBroadcast(intent);
		getActivity().sendBroadcastAsUser(intent, UserHandle.ALL);
	}

	public void startAirplay() {
		Intent intent = new Intent("airplay.amlogic.startService");
		getActivity().sendBroadcast(intent);
		
	}
	
	public void stopAirplay(){
		Intent intent = new Intent("airplay.amlogic.stopService");
		getActivity().sendBroadcast(intent);
	}

	public void startDlna() {
		Intent intent = new Intent("dlna.amlogic.startDMR");
		getActivity().sendBroadcast(intent);

	}
	
	public void stopDlna() {
		Intent intent = new Intent("dlna.amlogic.stopDMR");
		getActivity().sendBroadcast(intent);

	}

	public String getText_mode_ap() {
		return text_mode_ap.getText().toString();
	}

	public String getText_ap_ssid() {
		return text_ap_ssid.getText().toString();
	}

	public String getText_ap_password() {
		return text_ap_password.getText().toString();
	}

	public String getText_mode_router() {
		return text_mode_router.getText().toString();
	}

	public String getText_router_ssid() {
		return text_router_ssid.getText().toString();
	}

	public String getText_router_ip() {
		return text_router_ip.getText().toString();
	}

	public String getText_dlna_name() {
		return text_dlna_name.getText().toString();
	}

	

	public String getText_airplay_name() {
		return text_airplay_name.getText().toString();
	}

	
	public String getText_airmirror_name() {
		return text_airmirror_name.getText().toString();
	}

	public void setImageViewFocus(Boolean status){
		if(status){
			image_dlna.setBackgroundResource(R.drawable.dangle_dlna_focus);
			image_airplay.setBackgroundResource(R.drawable.dangle_wifi_focus);
			image_airmirror.setBackgroundResource(R.drawable.dangle_airplay_focus);
		}else{
			image_dlna.setBackgroundResource(R.drawable.dangle_dlna_normal);
			image_airplay.setBackgroundResource(R.drawable.dangle_wifi_normal);
			image_airmirror.setBackgroundResource(R.drawable.dangle_airplay_normal);
		}
		
	}
	
}
