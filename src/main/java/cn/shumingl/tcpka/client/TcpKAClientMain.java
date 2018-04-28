package cn.shumingl.tcpka.client;

import cn.shumingl.tcpka.NamedThreadFactory;
import cn.shumingl.tcpka.logger.LogConsole;
import cn.shumingl.tcpka.object.ObjectPool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpKAClientMain {
	private static final String DEFAULT_ENCODE = "UTF-8";

	private static final LogConsole logger = LogConsole.getLogger(TcpKAClientMain.class);

	public static void main(String[] args) throws IOException {

		final int threadSize = 20;
		final int objectSize = 16;
		ExecutorService service = Executors.newFixedThreadPool(threadSize, new NamedThreadFactory("CLIENT"));

		// 创建和初始化对象池
		ObjectPool<TcpKAClient> objects = new ObjectPool<>(objectSize);
		String host = "127.0.0.1";
		int port = 7254;
		try {
			for (int i = 0; i < objectSize; i++) {
				TcpKAClient client = new TcpKAClient(host, port);
				objects.put(client);
			}
		} catch (Exception e) {
			logger.error("ObjectPool.init ERROR: %s", e.getMessage());
		}
		// 发送数据
		for (int i = 0; i < 64; i++) {
			TcpKAClient client = objects.get();
			service.execute(new Thread() {
				public void run() {
					try {
						client.send(client.getName().getBytes(DEFAULT_ENCODE));
					} catch (UnsupportedEncodingException e) {
						logger.error("Send Data Error: %s", e.getMessage());
					}
				}
			});
		}
		for (TcpKAClient client : objects) {
			client.send("exit".getBytes(DEFAULT_ENCODE));
		}
		service.shutdown();
		while (!service.isTerminated()) {
		}
		objects.shutdown();

	}
}
