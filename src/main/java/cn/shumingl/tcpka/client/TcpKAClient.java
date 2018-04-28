package cn.shumingl.tcpka.client;

import cn.shumingl.tcpka.logger.LogConsole;
import cn.shumingl.tcpka.object.ObjectWrapper;
import cn.shumingl.tcpka.utils.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpKAClient implements ObjectWrapper {

	private static final LogConsole logger = LogConsole.getLogger(TcpKAClient.class);
	private static final long READ_TIMEOUT = 1000;
	private static final String DEFAULT_ENCODE = "UTF-8";

	private String name;
	private String host;
	private int port;

	private boolean busy = false;
	private boolean alive = false;
	private Socket socket;
	private OutputStream ostream;
	private InputStream istream;

	public TcpKAClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public boolean isBusy() {
		return busy;
	}

	@Override
	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public void activate() {
		try {
			logger.info("Object.activate : %s", name);
			socket = new Socket(host, port);
			socket.setSoTimeout(5000);
			socket.setSoLinger(false, 0);
			istream = socket.getInputStream();
			ostream = socket.getOutputStream();
			alive = true;
		} catch (IOException e) {
			logger.error("Create TcpKAClient ERROR: %s", e.getMessage());
		}
	}

	@Override
	public void destroy() {
		logger.info("Object.destroy  : %s", name);
		StreamUtil.closeQuietly(socket);
		alive = false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public byte[] send(byte[] bytes) {
		busy = true;
		String prefix = String.format("%s@%s:%d", Thread.currentThread().getName(), host, socket.getLocalPort());
		if (bytes == null)
			return null;
		try {
			// ========发送数据========
			String sendlen = String.format("%04d", bytes.length);
			byte[] sendbytes = new byte[bytes.length + 4];
			System.arraycopy(sendlen.getBytes(DEFAULT_ENCODE), 0, sendbytes, 0, 4);
			System.arraycopy(bytes, 0, sendbytes, 4, bytes.length);
			logger.info("[%s] send %s bytes: %s", prefix, bytes.length, new String(bytes, DEFAULT_ENCODE));
			ostream.write(sendbytes);
			ostream.flush();

			// ========读取数据========
			byte[] lenbuffer = new byte[4];// 长度，前4字节
			StreamUtil.ReadStream(istream, lenbuffer, READ_TIMEOUT);
			Integer lenrecv = Integer.parseInt(new String(lenbuffer, DEFAULT_ENCODE));
			byte[] databuffer = new byte[lenrecv]; // 内容
			StreamUtil.ReadStream(istream, databuffer, READ_TIMEOUT);
			String datarecv = "";
			if (lenrecv > 0)
				datarecv = new String(databuffer, DEFAULT_ENCODE);
			logger.info("[%s] recv %d bytes: %s", prefix, lenrecv, datarecv);
			return databuffer;
		} catch (Exception e) {
			logger.error("[%s][%s]", e, prefix, name);
			return null;
		} finally {
			busy = false;
		}
	}

}
