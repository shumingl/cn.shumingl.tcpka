package cn.shumingl.tcpka.object;

import cn.shumingl.tcpka.utils.IOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class KASocketPool implements ResourcePool<KASocket> {

    private LinkedList<Socket> sockets;
    private int size;

    private boolean available;
    private String host;
    private int port;
    private Semaphore locks;

    public KASocketPool(String host, int port, int size) {
        this.host = host;
        this.port = port;
        this.size = size;
        locks = new Semaphore(size, true);// 以公平模式创建信号量锁，资源数量为资源池大小

        this.sockets = new LinkedList<>();
        for (int i = 0; i < size; i++) {
        }
    }

    public Socket getSocket() {
        for (Socket socket : sockets) {
            if (isAvailable(socket))
                return socket;
        }
        return create(host, port);
    }

    private Socket create(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.setKeepAlive(false);
            socket.setOOBInline(false);
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            //TODO 异常处理
            e.printStackTrace();
        }
        sockets.add(socket);
        return socket;
    }

    private boolean isAvailable(Socket socket) {
        if (!available) return false;
        available = socket.isBound() && socket.isConnected() &&
                !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown();
        return available;
    }

    private void destroy(Socket socket) {
        available = false;
        if (socket != null) {
            IOUtil.closeQuietly(socket);
        }
    }
}
