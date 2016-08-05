package net.bestidear.donglelauncher.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class SettingDeviceName {

	private static final String DEVICE_PRE = "FAVAS";
	private static final String MAC_PATH = "/sys/class/net/wlan0/address";
	private Activity activity;

	public SettingDeviceName(Activity activity) {
		super();
		this.activity = activity;
	}

	public String getDevicename() {

		String mac = getMacAddress();
		if (mac == null || mac.equals(""))
			return "Android";
		String macs[] = mac.split(":");
		String Name = DEVICE_PRE + "_" + macs[3] + macs[4] + macs[5];
		return Name;
	}

	public String getMacAfterSix() {

		String mac = getMacAddress();
		if (mac == null || mac.equals(""))
			return "_123456";
		String macs[] = mac.split(":");
		String Name = "_" + macs[3] + macs[4] + macs[5];
		return Name;
	}

	private String loadFileAsString(String filePath) throws java.io.IOException {
		if (new File(filePath).exists()) {
			StringBuffer fileData = new StringBuffer(1000);
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
			reader.close();
			if(numRead != 0)
				return fileData.toString();
			else return "";
		}
		return "";

	}

	/*
	 * Get the STB MacAddress
	 */
	public String getMacAddress() {
		try {
			String mac = loadFileAsString(MAC_PATH).toUpperCase();
			if (mac.equals(""))
				return "";
			else
				return mac.substring(0, 17);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getLocalIP() {
		int IP_Int = ((WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo()
				.getIpAddress();
		return IP_IntToStr(IP_Int);
	}

	private String IP_IntToStr(int IP_Int) {
		String IP_Str = (IP_Int & 0xff) + "." + ((IP_Int >> 8) & 0xff) + "."
				+ ((IP_Int >> 16) & 0xff) + "." + ((IP_Int >> 24) & 0xff);
		return IP_Str;
	}

	/*
	 * 获取ap的广播地址
	 */
	public String getGatewayAddr() {
		int gateway = ((WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE)).getDhcpInfo().gateway;
		String addr = IP_IntToStr((gateway));
		return addr;
	}

}
