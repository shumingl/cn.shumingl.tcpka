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
        String host = "127.0.0.1";
        int port = 7254;
        int msgSize = 100;
        if (args != null && args.length > 2) {
            String hostStr = args[0].trim();
            if (!"".equals(hostStr))
                host = hostStr;
            String portStr = args[1].trim();
            if (!"".equals(portStr))
                port = Integer.parseInt(portStr);
            String sizeStr = args[2].trim();
            if (!"".equals(sizeStr))
                msgSize = Integer.parseInt(sizeStr);
        }
        final int threadSize = 20;
        final int objectSize = 16;
        ExecutorService service = Executors.newFixedThreadPool(threadSize, new NamedThreadFactory("CLIENT"));

        // 创建和初始化对象池
        ObjectPool<TcpKAClient> objects = new ObjectPool<>(objectSize);
        try {
            for (int i = 0; i < objectSize; i++) {
                TcpKAClient client = new TcpKAClient(host, port);
                objects.put(client);
            }
        } catch (Exception e) {
            logger.error("ObjectPool.init ERROR: %s", e.getMessage());
        }
        // 发送数据
        for (int i = 0; i < msgSize; i++) {
            TcpKAClient client = objects.get();
            service.execute(new Thread(() -> {
                try {
                    client.send(client.getName().getBytes(DEFAULT_ENCODE));
                } catch (UnsupportedEncodingException e) {
                    logger.error("Send Data Error: %s", e.getMessage());
                }
            }));
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
