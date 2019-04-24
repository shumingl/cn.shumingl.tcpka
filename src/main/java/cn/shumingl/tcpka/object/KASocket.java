package cn.shumingl.tcpka.object;

import cn.shumingl.tcpka.utils.IOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class KASocket implements IResource<Socket> {

    private Socket socket;
    private int size;

    private boolean available;
    private boolean used;
    private String host;
    private int port;

    private String id; // 对象ID

    public KASocket(Socket socket, int size) {
        if (socket == null)
            throw new RuntimeException();

        this.host = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.size = size;
        this.socket = socket;
    }

    public KASocket(String host, int port, int size) {
        this.host = host;
        this.port = port;
        this.size = size;
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
    public void release() {
        used = false;
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    public boolean isAvailable() {
        if (!available) return false;
        available = socket.isBound() && socket.isConnected() &&
                !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown();
        return available;
    }

    @Override
    public void destroy() {
        available = false;
        if (socket != null) {
            IOUtil.closeQuietly(socket);
        }
    }
}
