package net.bestidear.donglelauncher.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.bestidear.donglelauncher.LauncherActivity;



import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
/**
 * 应答发现命令的处理udp线程
 * @author think
 *
 */
public class UDPReceiveCommandThread extends Thread{
	
	public static final String SEPERATE = ":";
	
	public static final int DEFAULT_RECEIVE_PORT = 8989; //接受端口
	public static final int DEFAULT_SEND_PORT = 9000; //应答端口
	public static final int DEFAULT_TCP_RECEIVE_PORT = 9001;

	public static final String DISCOVERY_CMD = "discovery_device_bestidear";
	public static final String DEFAULT_AP_NAME = "DongleAp";

	
	private String DISCOVERY_CMD_AP = DISCOVERY_CMD + "_AP";
	private String DISCOVERY_CDM_router = DISCOVERY_CMD + "_router";
	
	private Service service;
	byte[] buffer = new byte[1024];
	DatagramSocket udpServerSocket;
	DatagramPacket udpReceivePacket;
	private int count_receice_cmd = 0;
	private int mode; //AP/ROUTER

	public UDPReceiveCommandThread(Service service) {
		super();
		this.service = service;
		
		try {
			udpServerSocket = new DatagramSocket(DEFAULT_RECEIVE_PORT);
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block

			if(udpServerSocket != null){
				udpServerSocket.close();
				udpServerSocket = null;
			}
			e.printStackTrace();
		}
		udpReceivePacket = new DatagramPacket(buffer, 0,buffer.length);
		
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				count_receice_cmd = 0;
			}
		};

		Timer time = new Timer(true);
		time.schedule(task, 5000, 5000);
	}
	
	private  String getLocalIP(Service service)
	{
		int IP_Int = ((WifiManager) service.getSystemService(Context.WIFI_SERVICE))
				.getConnectionInfo().getIpAddress();
		return IP_IntToStr(IP_Int);
	}
	
	private String IP_IntToStr(int IP_Int)
	{
		String IP_Str = 
			(IP_Int & 0xff) + "." +
			((IP_Int >> 8) & 0xff) + "." +
			((IP_Int >> 16) & 0xff) + "." +
			((IP_Int >> 24) & 0xff);
		return IP_Str;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				
				udpServerSocket.receive(udpReceivePacket); //接收指令
				count_receice_cmd += 1; //记录接受指令个数
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
			/*	if(udpServerSocket != null){
					udpReceivePacket = null;
					udpServerSocket.close();
					udpServerSocket = null;
					break;
				}*/
				e.printStackTrace();
			}
			//InetAddress clientAddress = udpReceivePacket.getAddress();
			//int clientPort = udpReceivePacket.getPort();	
			if(count_receice_cmd > 3){
				count_receice_cmd = 0;
				
				InetAddress clientAddress = udpReceivePacket.getAddress();
				byte[] msgBytes = udpReceivePacket.getData();		
				String receivecommand = new String(msgBytes,0,udpReceivePacket.getLength());
				if(receivecommand.contains("AP"))
					mode = SettingWifiThread.AP;
				else mode = SettingWifiThread.ROUTER;
				if(receivecommand.equals(DISCOVERY_CDM_router) || receivecommand.equals(DISCOVERY_CMD_AP))
				{	
					String response = createResponseCmd(mode, getLocalIP(service), DEFAULT_TCP_RECEIVE_PORT,DEFAULT_AP_NAME,getWifiListToString());
					sendResponse(response, clientAddress);
				}
			}
		}
	}
	
	
	/**
	 * 告诉对方这边的 具体信息
	 * @param mode ：ap/router
	 * @param ip :本机ip
	 * @param port :本机TCP 监听端口
	 * @param apSsid : ap的名字
	 * @param wifiList : wifi 列表    以#号分隔
	 * @return
	 */
	private String createResponseCmd(int mode,String ip, int port ,String apSsid,String wifiList){
		
		StringBuilder builder = new StringBuilder();
		builder.append(mode);
		builder.append(SEPERATE);
		builder.append(ip);
		builder.append(SEPERATE);
		builder.append(port);
		builder.append(SEPERATE);
		builder.append(apSsid);
		builder.append(SEPERATE);
		builder.append(wifiList);
		return builder.toString();
	}
	/**
	 * 区分 5G wifi ，2.4 wifi
	 * @return
	 */
	
	private String getWifiListToString(){
		String str="";
		
		List<ScanResult> scanlist = getWifiList();
		for (int i = 0; i < scanlist.size(); i++) {
			if(scanlist.get(i).frequency > 5000 && scanlist.get(i).frequency < 6000){// 5G 
				str = str + "5G_"+scanlist.get(i).SSID+"#"; // 是5G就加个5G的前缀
				
			}else{//2.4G
				str = str + scanlist.get(i).SSID + "#";
			}
		}
		return str;
	}
	
	private List<ScanResult> getWifiList() {

		List<ScanResult> list = new ArrayList<ScanResult>();

		WifiManager mWifiManager = (WifiManager) service
				.getSystemService(Context.WIFI_SERVICE);

		if (mWifiManager != null){
			mWifiManager.startScan();//这个是异步的
			list = mWifiManager.getScanResults();
		}
		return list;
	}
	
	
	private void sendResponse(String data ,InetAddress ip){
		try
		{
			InetAddress  remoteIP = ip;
			DatagramPacket dp = new DatagramPacket(data.getBytes(), data.getBytes().length, remoteIP, DEFAULT_SEND_PORT);

			for(int i= 0 ;i<5 ;i++){
				udpServerSocket.send(dp);
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
		/*	if(udpSendSocket != null){
				udpSendSocket.close();
				udpSendSocket = null;
			}*/
			e.printStackTrace();
		}
	}
}
