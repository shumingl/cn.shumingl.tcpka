package cn.shumingl.tcpka.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

	/**
	 * 接收数据
	 * 
	 * @param is
	 * @param buffer
	 * @throws IOException
	 */
	public static int ReadBytesFromSteam(InputStream is, byte[] buffer) throws IOException {
		if (buffer == null)
			return -1;
		int total = buffer.length;
		int finished = 0;
		int ret = 0;
		do {
			ret = is.read(buffer);
			if (ret > -1)
				finished += ret;
		} while (finished == total);
		return finished;
	}

	/**
	 * 接收数据
	 * 
	 * @param is
	 * @param buffer
	 * @throws IOException
	 */
	public static int ReadStream(InputStream is, byte[] buffer) throws IOException {
		if (buffer == null)
			return -1;
		int total = buffer.length;
		int finished = 0;
		int ret = 0;
		while (finished < total) {
			ret = is.read(buffer, finished, total - finished);
			if (ret > -1)
				finished += ret;
		}
		return finished;
	}

	/**
	 * 接收数据
	 * 
	 * @param is
	 * @param bufferSize
	 * @param timeout
	 * @throws IOException
	 */
	public static int ReadStream(InputStream is, byte[] buffer, long timeout) throws IOException {
		long start = System.currentTimeMillis();
		int total = buffer.length;
		int finished = 0;
		int ret = 0;
		while (finished < total) {
			ret = is.read(buffer, finished, total - finished);
			if (ret > -1)
				finished += ret;
			else {
				if (System.currentTimeMillis() - start > timeout) {
					return -1;
				}
			}
		}
		return finished;
	}

	/**
	 * 发送数据
	 * 
	 * @param os
	 * @param buffer
	 * @throws IOException
	 */
	public static void WriteBytesToStream(OutputStream os, byte[] buffer) throws IOException {
		os.write(buffer);
		os.flush();
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (Exception e) {
		}
	}
}
