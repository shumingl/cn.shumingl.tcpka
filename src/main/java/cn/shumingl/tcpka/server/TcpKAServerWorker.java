package cn.shumingl.tcpka.server;

import cn.shumingl.tcpka.logger.LogConsole;
import cn.shumingl.tcpka.utils.StreamUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpKAServerWorker extends Thread {

	private static final LogConsole logger = LogConsole.getLogger(TcpKAServerWorker.class);
	private static final String DEFAULT_ENCODE = "UTF-8";
	private static final long READ_TIMEOUT = 1000;

	private Socket client = null;

	public TcpKAServerWorker(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {

		String theadName = Thread.currentThread().getName();
		String host = client.getLocalAddress().getHostAddress();
		int port = client.getLocalPort();
		String chost = client.getInetAddress().getHostAddress();
		int cport = client.getPort();

		String prefix = String.format("%s@%s:%d", theadName, host, port);
		logger.info("[%s] acpt [%s:%s]", prefix, chost, cport);
		InputStream istream = null;
		OutputStream ostream = null;
		try {
			istream = client.getInputStream();
			ostream = client.getOutputStream();
			final int MAX_WAIT = 100;
			int count = 0;
			while (true) {
				if (istream.available() <= 0) {
					logger.info("[%s] wait %s:%d", prefix, chost, cport);
					if (count >= MAX_WAIT) { // 无请求则跳出
						break;
					} else {
						count++;
						Thread.sleep(100);
						continue;
					}
				}
				int ret = -1;
				// ========读取数据========
				byte[] lenbuffer = new byte[4];// 长度
				ret = StreamUtil.ReadStream(istream, lenbuffer);
				String datarecv = "";
				if (ret > -1) {
					Integer lenrecv = Integer.parseInt(new String(lenbuffer, DEFAULT_ENCODE));
					byte[] databuffer = new byte[lenrecv];// 内容
					ret = StreamUtil.ReadStream(istream, databuffer, READ_TIMEOUT);
					if (lenrecv > 0)
						datarecv = new String(databuffer, DEFAULT_ENCODE);
					logger.info("[%s] recv [%03d]:%s", prefix, lenrecv, datarecv);
				} else {
					logger.info("[%s] recv error.", prefix);
				}
				// ========发送数据========
				String respstr = String.format("%s received: %s", prefix, datarecv);
				byte[] respdata = respstr.getBytes(DEFAULT_ENCODE);
				byte[] respbytes = new byte[respdata.length + 4];
				byte[] resplen = String.format("%04d", respdata.length).getBytes(DEFAULT_ENCODE);
				System.arraycopy(resplen, 0, respbytes, 0, 4);
				System.arraycopy(respdata, 0, respbytes, 4, respdata.length);
				logger.info("[%s] resp [%03d]:%s", prefix, respdata.length, respstr);
				ostream.write(respbytes);
				ostream.flush();
				// 检测退出
				if ("exit".equals(datarecv))
					break;
			}
		} catch (Exception e) {
			logger.error("[%s]", e, prefix);
		} finally {
			// 发生错误则关闭此连接
			logger.info("[%s] exit.", prefix);
			StreamUtil.closeQuietly(istream);
			StreamUtil.closeQuietly(ostream);
			StreamUtil.closeQuietly(client);
		}
	}
}
