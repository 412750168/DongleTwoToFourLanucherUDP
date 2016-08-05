package net.bestidear.donglelauncher.network;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.bestidear.donglelauncher.LauncherActivity;



/**
 * 
 * 基于tcp的socket监听,对接受信息的转发
 */
public class ChatManager implements Runnable {

	private Socket socket = null;
	private Handler handler;
	private InputStream iStream;
	private OutputStream oStream;
	private static final String TAG = "ChatHandler";
	private static final String SETTING_RESPONSE = "setting_wifi_response";

	public ChatManager(Socket socket, Handler handler) {
		this.socket = socket;
		this.handler = handler;
	}

	@Override
	public void run() {
		try {

			iStream = socket.getInputStream();
			oStream = socket.getOutputStream();
			byte[] buffer = new byte[1024];
			int bytes;

			while (true) {
				try {
					// Read from the InputStream
					bytes = iStream.read(buffer);
					if (bytes == -1) {
						break;
					}

					// Send the obtained bytes to the UI Activity
					handler.obtainMessage(
							LauncherActivity.SETTING_WIFI, bytes,
							-1, buffer).sendToTarget();
					
					write(SETTING_RESPONSE.getBytes());// wifi 设置应答
					
				} catch (IOException e) {
				
					Log.e(TAG, "disconnected", e);

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(iStream != null)
					iStream.close();
				if(oStream != null)
					oStream.close();
				
				if (socket != null && !socket.isClosed())
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(byte[] buffer) {
		try {
			oStream.write(buffer);
		} catch (IOException e) {
			Log.e(TAG, "Exception during write", e);
		}
	}

}

