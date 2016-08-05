package net.bestidear.donglelauncher.network;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 创建纯种池，提供客户多次连接
 */
public class GroupOwnerSocketHandler extends Thread {

	ServerSocket socket = null;
	private final int THREAD_COUNT = 10;
	private Handler handler;

	public GroupOwnerSocketHandler(Handler handler,int port) throws IOException {
		try {

			socket = new ServerSocket();
			socket.setReuseAddress(true); // 设置 ServerSocket 的选项
			socket.bind(new InetSocketAddress(port));
			this.handler = handler;
			
		} catch (IOException e) {
			e.printStackTrace();
			pool.shutdownNow();
			
			throw e;
		}

	}

	/**
	 * A ThreadPool for client sockets.
	 */
	private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
			THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());

	@Override
	public void run() {
		while (true) {
			try {
				// A blocking operation. Initiate a ChatManager instance when
				// there is a new connection
				pool.execute(new ChatManager(socket.accept(), handler));

			} catch (IOException e) {
				try {
					if (socket != null && !socket.isClosed())
						socket.close();

				} catch (IOException ioe) {

				}

				e.printStackTrace();
				pool.shutdownNow();
				break;
			}
		}
	}

}
