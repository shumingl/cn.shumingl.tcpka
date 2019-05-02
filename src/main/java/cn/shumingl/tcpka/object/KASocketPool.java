package cn.shumingl.tcpka.object;

import cn.shumingl.tcpka.utils.IOUtil;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class KASocketPool implements ResourcePool<KASocket> {

    private LinkedList<KASocket> sockets;
    private int size;

    private String host;
    private int port;
    private final Semaphore available;

    public KASocketPool(String host, int port, int size) {
        this.host = host;
        this.port = port;
        this.size = size;
        available = new Semaphore(size, true);// 以公平模式创建信号量锁，资源数量为资源池大小

        this.sockets = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            this.sockets.add(new KASocket(host, port));
        }
    }

    public KASocket getSocket() {
        try {
            available.acquire();
            for (KASocket socket : sockets) {
                if (check(socket))
                    return socket;
            }
            return create(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            available.release();
        }
        return null;
    }

    private KASocket create(String host, int port) {
        return new KASocket(host, port);
    }

    private boolean check(KASocket socket) {
        return socket.isAvailable();
    }

    private void destroy(KASocket socket) {
        if (socket != null)
            IOUtil.closeQuietly(socket);
    }
}
