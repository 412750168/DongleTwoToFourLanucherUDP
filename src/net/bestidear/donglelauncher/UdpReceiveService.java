package net.bestidear.donglelauncher;

import net.bestidear.donglelauncher.network.UDPReceiveCommandThread;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UdpReceiveService extends Service{

	public static final int ROUTER = 2;
	private Thread udp = null;

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	// this is udp :receive cmd
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		udp = new UDPReceiveCommandThread(this); //Ö¸ÊÜ ËÑË÷Ö¸Áî
		udp.start();
	}

	
	
}
