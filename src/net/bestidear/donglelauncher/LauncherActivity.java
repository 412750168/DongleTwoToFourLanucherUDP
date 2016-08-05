package net.bestidear.donglelauncher;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import net.bestidear.donglelauncher.R;
import net.bestidear.donglelauncher.fragment.DlnaAirplayAirmirrorFragment;
import net.bestidear.donglelauncher.fragment.MainFragment;
import net.bestidear.donglelauncher.fragment.SettingFragment;
import net.bestidear.donglelauncher.network.GroupOwnerSocketHandler;
import net.bestidear.donglelauncher.network.SettingWifiThread;
import net.bestidear.donglelauncher.tools.LoginSharedPreferences;
import net.bestidear.donglelauncher.tools.SettingDeviceName;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class LauncherActivity extends Activity implements Handler.Callback , ChannelListener
		 {

	public static final String SEPERATE = ":";
//	public static final String DONGLEQRName = "DONGLELANUCHER.jpg";
//	public static final String DONGLEQRUrl = "http://yekertech.com/biapi/api/qrcode/getQRCode/DONGLEAPP/400";
	public static final String DOWNLOAD_QR = "https://github.com/miricy/release/blob/master/temp/ContralDongle.apk?raw=true";
	public static final String FLITER_FLAG = "BestIdear";
	public static final String STATE_SERVICE_AIRPLAY = "airplay.amlogic.state";
	public static final String OPEN_P2P_GROUP = "miracast_closed_p2p_grop_open";
//	public static final String ENABLE_STATE = "ENABLE";
//	public static final String SHELL_CMD_SYNC = "sync";
//	public static final String SHELL_CMD_CACHE = "echo \"1\" > /proc/sys/vm/drop_caches";
//	public static final String SHELL_CACHE_PATH = "/proc/sys/vm/drop_caches";

//	public static final String DEFAULT_AP_NAME = "DongleAp";
//	public static final String DEFAULT_AP_PASS = "12345678";
	public static final int DEFAULT_TCP_RECEIVE_PORT = 9001;

	public static final int UPDATE_UI = 0X400 + 1;
	public static final int CREATE_SOCKET = 0X400 + 2;
	public static final int SETTING_WIFI = 0X400 + 3;
	public static final int SHOW_QR = 0X400 + 4;
	public static final int ERROR = 0X400 + 5;
	public static final int UPDATE_WIFI_DIRECT_AP = 0X400 + 6;
	public static final int UPDATE_DLNA_AIRPLAY_FRAGMENT = 0X400 + 7;
	public static final int START_AIRPLAY_DLNA_AIRMIRROR = 0X400 + 8;
	public static final int DLNA_AIRPLAY_AIRMIRROR_FOCUS = 0X400 + 9;
	public static final int DLNA_AIRPLAY_AIRMIRROR_NORMAL = 0X400 + 10;
	public static final int REMOVE_ANDROID_AP = 0X400 + 11;
	public static final int REFRESH_SETTING_BG = 0X400 + 12;
	
	public static final int DLNAAIRPLAYAIRMIRROR_FRAGMENT = 0;
	public static final int MIRACAST_FRAGMENT = 1;
	public static final int SETTING_FRAGMENT = 2;
	public static final int MAIN_FRAGMENT = 3;
	public static boolean miracast_flag = false;
	
	private static int currentFragment = DLNAAIRPLAYAIRMIRROR_FRAGMENT;

//	public static int count = 0;
	public static boolean ISSETTING = false;

	public static final int DEFAULT_PORT = 8988;

	private Bitmap QRCodeBitmap = null;

	private Handler handler = new Handler(this);
	private IntentFilter filter = new IntentFilter();
	private NetWorkStateBroadCast broadcast;
//	private Thread udp = null;
	private Thread Tcp = null;
	Timer detecttimer;

	private SettingFragment settingFragement;
	private MainFragment mainFragment;
	private DlnaAirplayAirmirrorFragment dlnaAirplayAirmirrorFragment;
	
	private static ProgressDialog progressDialog ;
	private static boolean VISIBLE = true;
	
	private WifiP2pManager manager;
	private WifiManager wifiManager;
	private Channel channel;
	private boolean retryChannel = false;
	private LoginSharedPreferences loginSharedPreferences;
	
	private WifiConfiguration apConfig;
//	private WifiP2pGroup currentPersisentGroup;
	public static int groupNetworkId = -1;
	
	private String devName ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_main);
		
		IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
	    try{
	            mWindowManager.setAnimationScale(1, 0.0f);
	        } catch (RemoteException e) {
	        }
		
		//第一次系统登录
		loginSharedPreferences = new LoginSharedPreferences(this);
		// 解决内存不足，造成null 的问题
		if(savedInstanceState != null){
		//	loginSharedPreferences.deleteLogin();
			finish();
		}
		
		initFragment();
		//防止udp被杀
		Intent serviceIntent = new Intent(LauncherActivity.this, UdpReceiveService.class);
			startService(serviceIntent);

		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		filter.addAction(LauncherActivity.STATE_SERVICE_AIRPLAY);
		filter.addAction(LauncherActivity.OPEN_P2P_GROUP);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		//filter.addAction(WifiP2pManager.WIFI_P2P_PERSISTENT_GROUPS_CHANGED_ACTION);
		
		//创建wifi 的控制通道
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		channel = manager.initialize(this, getMainLooper(), this);
		devName = new SettingDeviceName(this).getDevicename();
		/*if(devName.equals("Android"))//防止出现0000
			devName = new SettingDeviceName(this).getDevicename();

		manager.setDeviceName(channel, devName, null);
		*/
		broadcast = new NetWorkStateBroadCast(manager,channel,this);
		registerReceiver(broadcast, filter);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				createGo();
				Log.d("zzl:::","this is Dongle_launcher delay 5s to open Softap ");
				handler.obtainMessage(UPDATE_DLNA_AIRPLAY_FRAGMENT).sendToTarget();// 防止过早设置为空
				
				TimerTask task = new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//创建用户组	
						//manager.createGroup(channel, null);
					//	if(wifiManager.isWifiEnabled()){
						if(manager.getWifiP2PApState() != WifiP2pManager.WIFI_P2P_AP_STATE_ENABLED){
							
							createGo();
							Log.d("zzl:::","this is Dongle_launcher detectd the  Softap is closed ,now to open again ");
							handler.obtainMessage(UPDATE_DLNA_AIRPLAY_FRAGMENT).sendToTarget();// 防止过早设置为空
						}
					}
				};
				Timer timer = new Timer();
				timer.schedule(task, 3000);//加大秒数，防止第一次开机airmirror不能使用
						
			}
		}, 5000);

		if(loginSharedPreferences.isFirstLogin()){
			loginSharedPreferences.putLogin();
			
			getFragmentManager().beginTransaction().hide(dlnaAirplayAirmirrorFragment).commitAllowingStateLoss();
			currentFragment = MIRACAST_FRAGMENT;
			changeFragement();
			
			TimerTask task2 = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					//创建用户组			
					//manager.createGroup(channel, null);
					initAirplayDlnaAirmirror();			
				}
			};
			Timer timer2 = new Timer();
			timer2.schedule(task2, 20000);
//			twoClickedEvent();
		}else{	
			    	//startYLT();			
		}		
	//	startDLNA();
	}

	private void initFragment(){
		
		dlnaAirplayAirmirrorFragment = new DlnaAirplayAirmirrorFragment();
		settingFragement = new SettingFragment();
		mainFragment = new MainFragment();

		getFragmentManager().beginTransaction().add(R.id.main, dlnaAirplayAirmirrorFragment,"Display").commitAllowingStateLoss();
		getFragmentManager().beginTransaction().add(R.id.main, settingFragement, "Display").commitAllowingStateLoss();
		getFragmentManager().beginTransaction().add(R.id.main,mainFragment,"Display").commitAllowingStateLoss();
		
		getFragmentManager().beginTransaction().hide(settingFragement).commitAllowingStateLoss();
		getFragmentManager().beginTransaction().hide(mainFragment).commitAllowingStateLoss();
		
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		progressDialog = new ProgressDialog(LauncherActivity.this);
		progressDialog.setTitle("Setting NetWork!");
		progressDialog.setMessage("Please wait!");
		progressDialog.setCancelable(true);
		
		//initAirplayDlnaAirmirror();
		handler.obtainMessage(UPDATE_DLNA_AIRPLAY_FRAGMENT).sendToTarget();
		
		if(wifiManager == null)
			wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if(!wifiManager.isWifiEnabled()){	// wifi 关闭的时候androidAP 显示为空
			
			StringBuilder builder = new StringBuilder();
			builder.append(SettingWifiThread.AP);
			builder.append("#");
			builder.append("NULL");
			builder.append("#");
			builder.append("NULL");
			
			handler.obtainMessage(LauncherActivity.UPDATE_WIFI_DIRECT_AP,
							builder.toString()).sendToTarget();
			
			handler.obtainMessage(LauncherActivity.DLNA_AIRPLAY_AIRMIRROR_NORMAL).sendToTarget();
		}

		VISIBLE = true;

		try {
			QRCodeBitmap = encodeAsBitmap(DOWNLOAD_QR);
			if(QRCodeBitmap != null)
				handler.sendEmptyMessage(SHOW_QR);

		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		VISIBLE  = false;
		progressDialog = null;
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(broadcast);
		super.onDestroy();
	}


	/**
	 * 创建二维码
	 * @param contentsToEncode 要创建二维码的数据
	 * @return
	 * @throws WriterException
	 */
	private Bitmap encodeAsBitmap(String contentsToEncode) throws WriterException {
        if (contentsToEncode == null) {
          return null;
        }
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
	    Display display = manager.getDefaultDisplay();
	    int mwidth = display.getWidth();
	    int mheight = display.getHeight();
	    int smallerDimension = mwidth < mheight ? mwidth : mheight;
	    smallerDimension = smallerDimension * 3 / 8;
	    Map<EncodeHintType,Object> hints = null;
	    String encoding = "UTF-8";//guessAppropriateEncoding(contentsToEncode);
	    if (encoding != null) {
	      hints = new EnumMap<EncodeHintType,Object>(EncodeHintType.class);
	      hints.put(EncodeHintType.CHARACTER_SET, encoding);
	    }
        BitMatrix result;
        try {
          result = new MultiFormatWriter().encode(contentsToEncode, BarcodeFormat.QR_CODE, smallerDimension, smallerDimension, hints);
        } catch (IllegalArgumentException iae) {
          // Unsupported format
          return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
          int offset = y * width;
          for (int x = 0; x < width; x++) {
            pixels[offset + x] = result.get(x, y) ?  0XFF000000 : 0XFFFFFFFF;
          }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
      }

	
	public Handler getHandler() {
		return handler;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub

		switch (msg.what) {
		case UPDATE_UI://界面更新
			
			String obj = (String) msg.obj;
			String value[] = obj.split(SEPERATE);
			
			if(progressDialog != null && progressDialog.isShowing() && VISIBLE && !value[1].equals("NULL"))
				progressDialog.dismiss();
			
			settingFragement.updateView(value[0], value[1], value[2]);
			dlnaAirplayAirmirrorFragment.setRouter(value[0], value[1], value[2]);
			
			if(!value[1].equals("NULL")&&!value[2].equals("NULL"))
				initAirplayDlnaAirmirror(); // 网络变化，更新一下名字
			
			if(!value[1].equals("NULL") && !value[2].equals("NULL"))
				handler.obtainMessage(LauncherActivity.CREATE_SOCKET).sendToTarget();
			
			break;
			
		case UPDATE_WIFI_DIRECT_AP://更新androidAp的界面 
			String obj2 = (String) msg.obj;
			String value2[] = obj2.split("#");
			settingFragement.updateAPView(value2[0], value2[1], value2[2]);
			dlnaAirplayAirmirrorFragment.setApMode(value2[0], value2[1], value2[2]);
			
			if(!value2[1].equals("NULL")&&!value2[2].equals("NULL"))
				initAirplayDlnaAirmirror(); // 网络变化，更新一下名字
			
			try {
				//这个接收网络改变数据
			Tcp= new GroupOwnerSocketHandler(handler, DEFAULT_TCP_RECEIVE_PORT);
			Tcp.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;

		case CREATE_SOCKET://监听线程
			
			//test : recreate the group
			//创建用户组
			
			if(ISSETTING){
				//manager.createGroup(channel, null);	
				if(wifiManager.isWifiEnabled()){
					if(manager.getWifiP2PApState() == WifiP2pManager.WIFI_P2P_AP_STATE_ENABLED){
						removeGo();
					}
					createGo();
				}
			}
			ISSETTING = false;
			if(detecttimer != null)
				detecttimer.cancel();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					//recreate wifi-direct group
					/**
					 * 创建tcp的监听线程
					 */
					try {
					Tcp = 	new GroupOwnerSocketHandler(handler, DEFAULT_TCP_RECEIVE_PORT);
					Tcp.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 2000);	
			break;

		case SETTING_WIFI://wifi模式类型设置 
			
			byte[] readBuf = (byte[]) msg.obj;
			String readMessage = new String(readBuf, 0, msg.arg1);
			String str[] = readMessage.split(SEPERATE);
			
			if(progressDialog != null && !progressDialog.isShowing() && VISIBLE){
				progressDialog.show();
			}
			//manager.removeGroup(channel, null);
			removeGo();
			ISSETTING = true;
			setingWifiNetwork(str);
			break;

		case SHOW_QR://显示二维码

			if(QRCodeBitmap != null)
				settingFragement.setQRCodeImageView(QRCodeBitmap);
			break;
			
		case UPDATE_DLNA_AIRPLAY_FRAGMENT://更新dlna界面
			Log.d("zzl:::","wifimanager.isWifiEnabled::"+wifiManager.isWifiEnabled());
			if(wifiManager.isWifiEnabled()){
				devName = new SettingDeviceName(this).getDevicename();
				updateDlnaAirPlayAirmirrorUI(devName);
			}
			break;
			
		case DLNA_AIRPLAY_AIRMIRROR_FOCUS:
			
			dlnaAirplayAirmirrorFragment.setImageViewFocus(true);
			break;
			
		case DLNA_AIRPLAY_AIRMIRROR_NORMAL:
			
			dlnaAirplayAirmirrorFragment.setImageViewFocus(false);
			break;
		case REFRESH_SETTING_BG:
			
			settingFragement.setLinerLayout();
			break;
			
		case START_AIRPLAY_DLNA_AIRMIRROR: // miracast 切换回来的时候，页面切换
			
		//	startAirplayDlnaAirmirror();
		//	removeGo();// 防止 miracast时产生的softap,以免出现AndroidAP 
			changeFragement();
			break;
			
		case ERROR://错误
			
			if(progressDialog != null && progressDialog.isShowing() && VISIBLE)
				progressDialog.dismiss();
			Toast.makeText(LauncherActivity.this, "Password is error/Ssid is invalid", Toast.LENGTH_LONG).show();
			//manager.createGroup(channel, null);
			if(wifiManager.isWifiEnabled()){
				if(manager.getWifiP2PApState() == WifiP2pManager.WIFI_P2P_AP_STATE_ENABLED){
					removeGo();
				}
				createGo(); //一开始的时候 ，wifi 密码输入出错，重新设置androidap
			}
			break;
		}

		return true;
	}
	
	private void updateDlnaAirPlayAirmirrorUI(String name){
		
		dlnaAirplayAirmirrorFragment.setAirplay(name);
		dlnaAirplayAirmirrorFragment.setDlna(name);
		dlnaAirplayAirmirrorFragment.setAirmirror(name);
	}

	private void setingWifiNetwork(final String str[]){
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				SettingWifiThread thread = new SettingWifiThread(str[1], str[2],
						Integer.parseInt(str[0]), LauncherActivity.this);
				thread.start();
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 5000);
		
		// 超时取消 对话框,ssid 无效 进行取消network setting
		TimerTask detecttask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub			
				if(ISSETTING)
				{
					ISSETTING = false;		
					setWifi(false);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					setWifi(true);
					handler.sendMessageDelayed(handler.obtainMessage(LauncherActivity.ERROR), 5000);
					//handler.obtainMessage(LauncherActivity.ERROR).sendToTarget();					
				}
			}
		};
		detecttimer = new Timer();
		detecttimer.schedule(detecttask, 60000);
		
	}
	
	//this is double clicked to change fragement
	/*
	
	private static String MODE_FUNCTION = "function";
	private static String MODE_SETTING = "setting";
	private String current_mode = MODE_FUNCTION;
	
	private boolean is_function_first_fragment = false;
	private boolean is_setting_first_fragment = true;
	private boolean is_function_to_setting = false;

	private static long first = 0;
	private static long second = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.d("zzl:::","key down count :"+ event.getRepeatCount()+event.isLongPress()+event.getDownTime());

		//模拟双击事件
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 return true;
		 }
		if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) 
			keyOneTwoEvent();
				
		return super.onKeyDown(keyCode, event);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if(event.getAction() == MotionEvent.ACTION_DOWN)
			keyOneTwoEvent();
		
		return super.onTouchEvent(event);
	}

	

	private void keyOneTwoEvent(){
		if(first == 0)
		{
			first = System.currentTimeMillis();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if((second -first) < 0 ){
						
						oneClickedEvent();
						
					}else{
						
						twoClickedEvent();
					}
					
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 500);
		}else{
			second = System.currentTimeMillis();
		}
		
	
	}
	
	
	private void oneClickedEvent(){
		
		Log.d("zzl:::", "this is one click");
		if(current_mode.equals(MODE_SETTING))
			if(is_setting_first_fragment == true)
				is_setting_first_fragment = false;
			else is_setting_first_fragment = true;
		
		first = 0;
		second = 0;
		changeFragment(current_mode);
	}
	
	private void twoClickedEvent(){
		
		Log.d("zzl:::","this is double click");
		if(current_mode.equals(MODE_FUNCTION)){
			current_mode = MODE_SETTING;
			is_function_to_setting = true;
		}else { current_mode = MODE_FUNCTION; is_function_first_fragment = true; }
		
		first = 0;
		second = 0;
		changeFragment(current_mode);
	}

	private void changeFragment(String mode){
		
		if(mode.equals(MODE_FUNCTION)){
			
			if(is_setting_first_fragment == true){
				getFragmentManager().beginTransaction().hide(settingFragement)
				.show(dlnaAirplayAirmirrorFragment).commitAllowingStateLoss();
				manager.createGroup(channel, null);
			}else {
				getFragmentManager().beginTransaction().hide(mainFragment)
				.show(dlnaAirplayAirmirrorFragment).commitAllowingStateLoss();
				manager.createGroup(channel, null);
			}
			
			if(is_function_first_fragment == false){
				manager.removeGroup(channel, null);//to test the miracast
				
				Intent intent = new Intent(Intent.ACTION_MAIN);
				ComponentName componentName = new ComponentName(
					"com.amlogic.miracast",
					"com.amlogic.miracast.WiFiDirectMainActivity");
				intent.setComponent(componentName);
				startActivity(intent);
			}else{
				is_function_first_fragment = false;
			}
		}else{
			
			if(is_function_to_setting)
				is_setting_first_fragment = true;
			
			if(is_setting_first_fragment == true && is_function_to_setting == true){
				getFragmentManager().beginTransaction().hide(dlnaAirplayAirmirrorFragment)
				.show(settingFragement).commitAllowingStateLoss();
				is_function_to_setting = false;
			}
			if(is_setting_first_fragment == false)

				getFragmentManager().beginTransaction().hide(settingFragement)
				.show(mainFragment).commitAllowingStateLoss();

			
			if(is_setting_first_fragment == true && is_function_to_setting == false)
				getFragmentManager().beginTransaction().hide(mainFragment)
				.show(settingFragement).commitAllowingStateLoss();
				manager.createGroup(channel, null);
			
		}
		
	}
	
	*/
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
	
		//模拟双击事件
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 return true;
		 }
		if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) 
			changeFragement();
		return super.onKeyDown(keyCode, event);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
	
		if(event.getAction() == MotionEvent.ACTION_DOWN)
			changeFragement();
		return super.onTouchEvent(event);
	}

	
	private void changeFragement(){
				
	//	if(currentFragment == DLNAAIRPLAYAIRMIRROR_FRAGMENT && manager.getWifiP2PApState() == WifiP2pManager.WIFI_P2P_AP_STATE_ENABLED){ //dlna 界面
		if(currentFragment == DLNAAIRPLAYAIRMIRROR_FRAGMENT){ //dlna 界面
		
			//manager.removeGroup(channel, null);
		//	removeGo();
			
			final ProgressDialog dialog2 = new ProgressDialog(LauncherActivity.this);
			dialog2.setTitle("Go to Miracast!");
			dialog2.setMessage("Please wait!");
			dialog2.setCancelable(true);
			dialog2.show();
			
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					currentFragment = MIRACAST_FRAGMENT;
					
					if(dialog2 != null)
						dialog2.dismiss();
					
					Intent intent = new Intent(Intent.ACTION_MAIN);
					ComponentName componentName = new ComponentName(
						"com.amlogic.miracast",
						"com.amlogic.miracast.WiFiDirectMainActivity");
					intent.setComponent(componentName);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);//不进行组件有无判断，防止进不了一页
					
					getFragmentManager().beginTransaction().hide(dlnaAirplayAirmirrorFragment).commitAllowingStateLoss();
					Log.d("zzl:::","this is in miracast ");
					}
			};
			Timer timer = new Timer();
			timer.schedule(task, 2500);	
			
		}else if(currentFragment == MIRACAST_FRAGMENT){ // miracast 界面
			//manager.createGroup(channel, null);
			currentFragment = SETTING_FRAGMENT;
			getFragmentManager().beginTransaction().show(settingFragement).commitAllowingStateLoss();
			Log.d("zzl:::","this is in setting ");

			handler.obtainMessage(LauncherActivity.REFRESH_SETTING_BG).sendToTarget();
				
			TimerTask task2 = new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(wifiManager.isWifiEnabled()){
						//	if(manager.getWifiP2PApState() != WifiP2pManager.WIFI_P2P_AP_STATE_ENABLED){
								createGo();
						//   }
						}
					}
				};
				Timer timer2 = new Timer();
				timer2.schedule(task2, 2000);
				
		}else if(currentFragment == SETTING_FRAGMENT){ // 设置 界面
			getFragmentManager().beginTransaction().hide(settingFragement)
			.show(mainFragment).commitAllowingStateLoss();
			Log.d("zzl:::","this is in main ");

			currentFragment = MAIN_FRAGMENT;
			
		}else if(currentFragment == MAIN_FRAGMENT){ // 主界面
			getFragmentManager().beginTransaction().hide(mainFragment)
			.show(dlnaAirplayAirmirrorFragment).commitAllowingStateLoss();
			Log.d("zzl:::","this is in dlna ");

			currentFragment = DLNAAIRPLAYAIRMIRROR_FRAGMENT;
			
			Log.d("zzl:::","softAP_status"+manager.getWifiP2PApState());	
			
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(wifiManager.isWifiEnabled()){
						if(manager.getWifiP2PApState() == WifiP2pManager.WIFI_P2P_AP_STATE_DISABLED || 
								manager.getWifiP2PApState() == WifiP2pManager.WIFI_P2P_AP_STATE_DISABLING ||
									manager.getWifiP2PApState() == WifiP2pManager.WIFI_P2P_AP_STATE_FAILED){	
							createGo(); //wifi 关闭后，dlna 界面不显示			
						}
						//manager.createGroup(channel, null);
					}
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 3000);	
			
	        startYLT();
	       
		}
		
	}
	
	private void startYLT(){
		
		if(!isServiceRunning("com.hpplay.happyplay.daemonService")) {
		    Intent intent = new Intent();
			intent.setComponent(new ComponentName("com.hpplay.happyplay.aw", "com.hpplay.happyplay.daemonService"));
			//            intent .setComponent(new ComponentName("com.hpplay.happyplay.aw", "com.hpplay.happyplay.mainServer"));
			startService(intent);
	     }
		    Intent intent2 = new Intent("airplay.amlogic.startService");
			sendBroadcast(intent2);
		
	}
	
	private void startDLNA(){
		
		if(dlnaAirplayAirmirrorFragment != null){
			dlnaAirplayAirmirrorFragment.startDlna();
		}
	}
	
	private void initAirplayDlnaAirmirror(){
		
		//启动airplay
		if(wifiManager.isWifiEnabled()){
			devName = new SettingDeviceName(this).getDevicename();	
			dlnaAirplayAirmirrorFragment.changeAirplayName(devName);
			dlnaAirplayAirmirrorFragment.changeDLnaName(devName);
			Log.d("zzl:::","dlna and airmirror change name broadcast is sent");
			handler.obtainMessage(UPDATE_DLNA_AIRPLAY_FRAGMENT).sendToTarget();
		}
	}


	@Override
	public void onChannelDisconnected() {
		// TODO Auto-generated method stub
		if (manager != null && !retryChannel) {
			retryChannel = true;
			manager.initialize(this, getMainLooper(), this);
		} else {
			retryChannel = false;
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(arg0);
		
		String ap = arg0.getString("AP");
		String router = arg0.getString("ROUTER");
				
		handler.obtainMessage(LauncherActivity.UPDATE_WIFI_DIRECT_AP,
				ap).sendToTarget();
		
		handler.obtainMessage(LauncherActivity.UPDATE_UI,
				router).sendToTarget();
		
		devName = new SettingDeviceName(this).getDevicename();
		handler.obtainMessage(UPDATE_DLNA_AIRPLAY_FRAGMENT).sendToTarget();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
		outState.putString("AP", SettingWifiThread.AP+"#"+dlnaAirplayAirmirrorFragment.getText_ap_ssid()+"#"+dlnaAirplayAirmirrorFragment.getText_ap_ssid());
		outState.putString("ROUTER", SettingWifiThread.ROUTER+":"+dlnaAirplayAirmirrorFragment.getText_router_ssid()+":"+dlnaAirplayAirmirrorFragment.getText_router_ip());

		super.onSaveInstanceState(outState);
	}
	
	/**
	 * 解决语言改变造成null 的问题
	 * @param newConfig
	 */

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		currentFragment = DLNAAIRPLAYAIRMIRROR_FRAGMENT;
		super.onConfigurationChanged(newConfig);
		finish();
	}

	private void createGo(){
		//WifiConfiguration apConfig = manager.getWifiP2PApConfiguration();
		apConfig = new WifiConfiguration();
		//设置热点的名字与密码
		String Mac = new SettingDeviceName(this).getMacAfterSix();
		
		apConfig.SSID = "AndroidAP"+Mac;   
        apConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
        apConfig.preSharedKey = "12345678";
              
		if(manager != null && currentFragment != MIRACAST_FRAGMENT){
			manager.startSoftAP(channel, apConfig);	
			Log.d("zzl:::","THIS IS softap is opend and createGo");
		}
	}
	
	private void removeGo(){
		
		if(manager != null && groupNetworkId != -1){
			if(wifiManager.isWifiEnabled()){
					if(manager.getWifiP2PApState() == WifiP2pManager.WIFI_P2P_AP_STATE_ENABLED){
						manager.stopSoftAP(channel);
						manager.deletePersistentGroup(channel, groupNetworkId, null); //底层固定了
						Log.d("zzl:::","remove the softap");
				   }
				}
		}
		
	}
	
	public void setWifi(boolean isEnable) {  
		  
        if (wifiManager == null) {  
            wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);  
           // return;  
        }  
          
        if (isEnable) {// 开启wifi  
            if (!wifiManager.isWifiEnabled()) {  
            	wifiManager.setWifiEnabled(true);  
            }  
        } else {  
            // 关闭 wifi  
            if (wifiManager.isWifiEnabled()) {  
            	wifiManager.setWifiEnabled(false);  
            }  
        }  
	}
	
	public boolean isServiceRunning(String className) {
		        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE); 
		        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
		
		        if (serviceList.size() <= 0) {
		            return false;
		        }
		        for (int i=0; i<serviceList.size(); i++) {
		            //Log.d(TAG, "*** servicelist: "+serviceList.get(i).service.getClassName());
		            if (serviceList.get(i).service.getClassName().equals(className) == true) {
		                //Log.d(TAG, "*** airplay service list exist");
		                return true;
	              }
		        }
		        return false;
		    }

}
