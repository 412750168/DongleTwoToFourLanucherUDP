package net.bestidear.donglelauncher.network;

import java.lang.reflect.Method;
import java.util.List;

import net.bestidear.donglelauncher.LauncherActivity;
import net.bestidear.donglelauncher.network.WifiConfig.WifiCipherType;
import net.bestidear.donglelauncher.tools.LoginSharedPreferences;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
/**
 * wifi ap / router 模式切换
 * @author think
 *
 */
public class SettingWifiThread extends Thread {

	private static final String SEPERATE = ":";
	String ssid;
	String password;
	Activity activity;
	int networkid;
	WifiConfiguration config;
	WifiManager mWifiManager;
	int mode;
	LoginSharedPreferences loginSharedPreferences;
	WifiConfiguration goodOldConfig;

	public static final int AP = 1;
	public static final int ROUTER = 2;

	public SettingWifiThread(String ssid, String password, final int Mode,
			Activity activity) {
		super();
		this.ssid = ssid;
		this.password = password;
		this.activity = activity;
		this.mode = Mode;
		mWifiManager = (WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE);
		loginSharedPreferences = new LoginSharedPreferences(activity);
	}

	@Override
	public void run() {

		// TODO Auto-generated method stub
		try {
		if (mode == ROUTER)
			setRouter();
		else {
			setWifiApEnabled(false);
			
			Thread.sleep(2000);
			
			boolean statue = setWifiApEnabled(true);
			if (statue == true) {
				//等待ap成功设置后才显示				
				while(getWifiApState(mWifiManager) != WIFI_AP_STATE_ENABLED )
					Thread.sleep(1000);
				
				StringBuilder builder = new StringBuilder();
				builder.append(AP);
				builder.append(SEPERATE);
				builder.append(ssid);
				builder.append(SEPERATE);
				builder.append(password);

				((LauncherActivity) activity)
						.getHandler()
						.obtainMessage(LauncherActivity.UPDATE_UI, // 在ui 更新的时候 创建接收
								builder.toString()).sendToTarget();

			}
		}
		
		/*	Thread.sleep(4000);
			((LauncherActivity) activity).getHandler()
					.obtainMessage(LauncherActivity.CREATE_SOCKET,mode)
					.sendToTarget(); */
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setRouter() {
		//try {
		/*setWifiApEnabled(false);
		
		Thread.sleep(4000);
		mWifiManager.setWifiEnabled(true);	
		Thread.sleep(8000);
		*/
		
		WifiInfo currentInfo = null;
		synchronized (this) {

			currentInfo = mWifiManager.getConnectionInfo();
			if (currentInfo != null) {
				if (currentInfo.getSSID().equals("\"" + ssid + "\""))
					return;
				mWifiManager.disableNetwork(currentInfo.getNetworkId());// 先关闭已连的wifi
				//密码输入错误用来重新连接以前的ssid
				loginSharedPreferences.putOldNetworkId(currentInfo.getNetworkId());
				
				currentInfo = null;
			}
	
			WifiConfiguration oldConfig = IsExsits(ssid);
			if (oldConfig != null
					&& (oldConfig.preSharedKey == null || oldConfig.preSharedKey
							.equals("\"" + password + "\""))) {
				config = oldConfig;
				networkid = config.networkId;
			} else {
				if (oldConfig != null)
					mWifiManager.removeNetwork(networkid);
				
				WifiConfig configmanager = new WifiConfig();
				WifiCipherType wifiType = getSecurityType(ssid);
				config = configmanager.CreateWifiInfo(ssid, password, wifiType);						
				networkid = mWifiManager.addNetwork(config);
			}
			if (config != null) {
			
			    mWifiManager.enableNetwork(networkid, true);
				//if(!state)
				//	((LauncherActivity)activity).getHandler().obtainMessage(LauncherActivity.ERROR).sendToTarget();
				mWifiManager.saveConfiguration();
				loginSharedPreferences.putNewNetworkId(networkid);
								
			}

		}
	/*	} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	}


	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		if (existingConfigs == null)
			return null;
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiCipherType getSecurityType(String ssid) {
		WifiCipherType security_type = WifiCipherType.WIFICIPHER_INVALID;
		List<ScanResult> results = mWifiManager.getScanResults();
		if (results != null) {
			for (ScanResult result : results) {
				// Ignore hidden and ad-hoc networks.
				if (result.SSID == null || result.SSID.length() == 0
						|| result.capabilities.contains("[IBSS]")) {
					continue;
				}
				if (result.SSID.equals(ssid)) {
					if (result.capabilities.contains("WEP")) {
						security_type = WifiCipherType.WIFICIPHER_WEP;
					} else if (result.capabilities.contains("PSK")) {
						security_type = WifiCipherType.WIFICIPHER_WPA;
					} else if (result.capabilities.contains("EAP")) {
						security_type = WifiCipherType.WIFICIPHER_WPA;
					} else {
						security_type = WifiCipherType.WIFICIPHER_NOPASS;
					}
					break;
				}
			}
		}
		return security_type;
	}

	public static final int WIFI_AP_STATE_DISABLING = 10;
	public static final int WIFI_AP_STATE_DISABLED = 11;
	public static final int WIFI_AP_STATE_ENABLING = 12;
	public static final int WIFI_AP_STATE_ENABLED = 13;
	public static final int WIFI_AP_STATE_FAILED = 14;

	private int getWifiApState(WifiManager wifiManager) {
		try {
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			int i = (Integer) method.invoke(wifiManager);
			// Log.i("ljl" , "wifi state:  " + i);
			return i;
		} catch (Exception e) {
			Log.i("ljl", "Cannot get WiFi AP state" + e);
			return WIFI_AP_STATE_FAILED;
		}
	}

	private boolean setWifiApEnabled(boolean enabled) {

		synchronized (this) {
		try {
			Method method = mWifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			if (enabled) { // disable WiFi in any case
				// wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
				mWifiManager.setWifiEnabled(false);
				// 热点的配置类
				WifiConfiguration apConfig = new WifiConfiguration();
				// 配置热点的名称(可以在名字后面加点随机数什么的)
				apConfig.SSID = ssid;
				// 配置热点的密码
				apConfig.allowedGroupCiphers
						.set(WifiConfiguration.GroupCipher.TKIP);
				apConfig.allowedKeyManagement
						.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				apConfig.allowedPairwiseCiphers
						.set(WifiConfiguration.PairwiseCipher.TKIP);
				apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
				apConfig.preSharedKey = password;
				// 通过反射调用设置热点

				// 返回热点打开状态
				return (Boolean) method.invoke(mWifiManager, apConfig, enabled);

			} else {
				return (Boolean) method.invoke(mWifiManager, null, enabled);

			}

		} catch (Exception e) {
			return false;
		}
		}
		
	}

}
