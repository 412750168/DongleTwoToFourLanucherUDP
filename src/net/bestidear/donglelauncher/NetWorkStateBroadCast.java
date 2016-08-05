package net.bestidear.donglelauncher;



import net.bestidear.donglelauncher.network.SettingWifiThread;
import net.bestidear.donglelauncher.tools.LoginSharedPreferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.util.Log;
/*
 * wifi 网络状态更新
 */
public class NetWorkStateBroadCast extends BroadcastReceiver {

	private LauncherActivity activity;
	private WifiManager mWifiManager;
	private WifiP2pManager manager;
	private Channel channel;
    private static int count = 0;
    private LoginSharedPreferences loginSharedPreferences;

	public NetWorkStateBroadCast(WifiP2pManager manager ,Channel channel ,LauncherActivity activity) {
		super();
		this.manager = manager;
		this.channel = channel;
		this.activity = activity;
		mWifiManager = (WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE);
		loginSharedPreferences = new LoginSharedPreferences(activity);
	}
	
	private String IntToStringIpAddress(int ipAddress) {
		return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
				+ (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			// network state
			ConnectivityManager conMan = (ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifi = conMan
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifi.isConnected()) {

				StringBuilder builder = new StringBuilder();
				builder.append(SettingWifiThread.ROUTER);
				builder.append(":");
				builder.append(mWifiManager.getConnectionInfo().getSSID());
				builder.append(":");
				WifiInfo info = mWifiManager.getConnectionInfo();
				String ip = IntToStringIpAddress(info.getIpAddress());
				builder.append(ip);

				((LauncherActivity) activity)
						.getHandler()
						.obtainMessage(LauncherActivity.UPDATE_UI,
								builder.toString()).sendToTarget();
			}else{
				((LauncherActivity) activity)
				.getHandler()
				.obtainMessage(LauncherActivity.UPDATE_UI,
						"NULL:NULL:NULL").sendToTarget();
		
				setAPNULL();
				
			}
		}else if (intent.getAction().equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)) {
			// this is get ap : ssid and password
			if (manager == null) {
				return;
			}
				
			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			
			if (networkInfo.isConnected()) {
				
				//wifi-direct 成功连接上，会产生ap		
				((LauncherActivity) activity)
				.getHandler()
				.obtainMessage(LauncherActivity.DLNA_AIRPLAY_AIRMIRROR_FOCUS).sendToTarget();
				//不允许手动关闭wifi-p2p功能，不然就造成airplay界面显示为关闭
				manager.requestGroupInfo(channel, new GroupInfoListener() {

					@Override
					public void onGroupInfoAvailable(WifiP2pGroup group) {
						// TODO Auto-generated method stub
						//获取ap ：ssid and password
						if(group != null ){
							//miracast 会产生 一个AndroidAP的softAP,造成显示的时候不一致
							if(group.getNetworkName().contains("AndroidAP_")&&group.getPassphrase().contains("8")){
								StringBuilder builder = new StringBuilder();
								builder.append(SettingWifiThread.AP);
								builder.append("#");
								builder.append(group.getNetworkName());
								builder.append("#");
								builder.append(group.getPassphrase());
							Log.d("zzl:::","this is Dongle_launcher receive softap::"+"ssid::"+group.getNetworkName()+"::pass::"+group.getPassphrase());									
								((LauncherActivity) activity)
										.getHandler()
										.obtainMessage(LauncherActivity.UPDATE_WIFI_DIRECT_AP,
												builder.toString()).sendToTarget();						
							}
							activity.groupNetworkId = group.getNetworkId(); //remove perssient group
						}
					}
				});

			}else{
				
			}
		}else if(intent.getAction().equals(LauncherActivity.STATE_SERVICE_AIRPLAY)){
			
		}else if(intent.getAction().equals(LauncherActivity.OPEN_P2P_GROUP)){ //miracast发过来的页面切换广播
		//	manager.createGroup(channel, null);
			//产生 两个广播，造成页面跳格
			String mode = intent.getStringExtra("mode");
			if(mode != null)
			{
				if(mode.equals("TOUCH")){
					count ++;
					if(count == 1){
						((LauncherActivity) activity)
						.getHandler()
						.obtainMessage(LauncherActivity.START_AIRPLAY_DLNA_AIRMIRROR).sendToTarget();	
					}
					if(count > 1){
						count = 0;
					}
				}else{
					((LauncherActivity) activity)
					.getHandler()
					.obtainMessage(LauncherActivity.START_AIRPLAY_DLNA_AIRMIRROR).sendToTarget();	
				}
			}
			
		}else if(intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
			// 密码输入错误,重新连接以前的ssid
			int authState = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
			if(authState == WifiManager.ERROR_AUTHENTICATING){
				
				int newNetworkId = loginSharedPreferences.getNewNetworkId();
				mWifiManager.removeNetwork(newNetworkId);
				
				int oldNetworkId = loginSharedPreferences.getOldNetworkId();
				if(oldNetworkId != -1)
					mWifiManager.enableNetwork(oldNetworkId, true);
				
				((LauncherActivity) activity)
				.getHandler()
				.obtainMessage(LauncherActivity.ERROR).sendToTarget();
			}
		
		}
	}
	
	private void setAPNULL(){
		if(!mWifiManager.isWifiEnabled()){	// wifi 关闭的时候androidAP 显示为空
			
			StringBuilder builder = new StringBuilder();
			builder.append(SettingWifiThread.AP);
			builder.append("#");
			builder.append("NULL");
			builder.append("#");
			builder.append("NULL");
			((LauncherActivity) activity)
					.getHandler()
					.obtainMessage(LauncherActivity.UPDATE_WIFI_DIRECT_AP,
							builder.toString()).sendToTarget();
			
			((LauncherActivity) activity)
			.getHandler()
			.obtainMessage(LauncherActivity.DLNA_AIRPLAY_AIRMIRROR_NORMAL).sendToTarget();
		}
	}
}
