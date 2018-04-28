package cn.shumingl.tcpka.server;

import cn.shumingl.tcpka.NamedThreadFactory;
import cn.shumingl.tcpka.logger.LogConsole;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpKAServer {

	private static final LogConsole logger = LogConsole.getLogger(TcpKAServer.class);
	private static ExecutorService POOL;
	private static final int DEFAULT_PORT = 7254;
	private static ServerSocket server;
	private static int port;
	private static boolean stop = false;

	public static void listen(int port) {
		TcpKAServer.port = port;
	}

	public static void startup() throws IOException {
		if (port == 0)
			port = DEFAULT_PORT;
		POOL = Executors.newFixedThreadPool(20, new NamedThreadFactory("SERVER"));
		try {
			server = new ServerSocket(port);
			logger.info("TcpServerMain.startup successfully. listening [%d]", port);
			while (!stop) {
				POOL.execute(new TcpKAServerWorker(server.accept()));
			}
		} catch (Exception e) {
			logger.error("TcpServerMain.startup error.", e);
		}
	}

	public static void shutdown() {
		try {
			POOL.shutdown();
			while (!POOL.isTerminated()) {
				Thread.sleep(100);
			}
			if (server != null) {
				server.close();
				server = null;
			}
		} catch (Exception e) {
			logger.error("TcpServerMain.shutdown error.", e);
		}
	}
}
