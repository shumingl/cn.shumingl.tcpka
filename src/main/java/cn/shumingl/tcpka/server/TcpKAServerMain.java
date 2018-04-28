package cn.shumingl.tcpka.server;

public class TcpKAServerMain {

	public static void main(String[] args) throws Exception {
		int port = 7254;
		if (args != null && args.length > 0) {
			String portStr = args[0].trim();
			if (!"".equals(portStr))
				port = Integer.parseInt(portStr);
		}
		TcpKAServer.listen(port);
		TcpKAServer.startup();
	}
}
