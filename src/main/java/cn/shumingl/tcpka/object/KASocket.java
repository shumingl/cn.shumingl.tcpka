package cn.shumingl.tcpka.object;

import cn.shumingl.tcpka.utils.IOUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class KASocket implements IResource<Socket>, Closeable {

    private Socket socket;

    private boolean available;
    private String host;
    private int port;

    public KASocket(Socket socket) {
        if (socket == null)
            throw new RuntimeException();

        this.host = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.socket = socket;
    }

    public KASocket(String host, int port) {
        this.host = host;
        this.port = port;
        this.socket = new Socket();
    }

    @Override
    public Socket take() {
        try {
            socket.setKeepAlive(false);
            socket.setOOBInline(false);
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            //TODO 异常处理
            e.printStackTrace();
        }
        return socket;
    }

    @Override
    public boolean isAvailable() {
        if (!available) return false;
        available = socket.isConnected() &&
                !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown();
        return available;
    }

    @Override
    public void destroy() {
        IOUtil.closeQuietly(this);
    }

    @Override
    public void close() throws IOException {
        available = false;
        if (socket != null)
            socket.close();
    }
}
