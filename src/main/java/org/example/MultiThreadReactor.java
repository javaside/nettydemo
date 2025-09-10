package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadReactor {

    private static final int WORKER_POOL_SIZE = 10;
    private final ExecutorService workerPool = Executors.newFixedThreadPool(WORKER_POOL_SIZE);

    public static void main(String[] args) throws IOException {
        new MultiThreadReactor().start(8080);
    }

    public void start(int port) throws IOException {
        // 主Reactor - 处理连接建立
        Selector mainSelector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(mainSelector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port " + port);

        // 子Reactor线程组 - 处理I/O读写
        Reactor[] subReactors = new Reactor[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < subReactors.length; i++) {
            subReactors[i] = new Reactor();
            new Thread(subReactors[i]).start();
        }
        int nextSubReactor = 0;

        // 主事件循环
        while (true) {
            mainSelector.select();
            Set<SelectionKey> keys = mainSelector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);

                    // 轮询分配给子Reactor
                    Reactor subReactor = subReactors[nextSubReactor];
                    subReactor.register(client);
                    nextSubReactor = (nextSubReactor + 1) % subReactors.length;

                    System.out.println("New client connected to subReactor-" + nextSubReactor);
                }
            }
        }
    }

    // 子Reactor实现
    private class Reactor implements Runnable {
        private final Selector selector;

        public Reactor() throws IOException {
            this.selector = Selector.open();
        }

        public void register(SocketChannel channel) throws ClosedChannelException {
            channel.register(selector, SelectionKey.OP_READ);
            selector.wakeup(); // 唤醒可能阻塞的select()
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();

                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();

                        if (key.isReadable()) {
                            // 读取数据后提交给工作线程池处理
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int bytesRead = client.read(buffer);

                            if (bytesRead > 0) {
                                buffer.flip();
                                workerPool.execute(() -> processRequest(client, buffer));
                            } else if (bytesRead < 0) {
                                client.close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processRequest(SocketChannel client, ByteBuffer buffer) {
            try {
                // 模拟业务处理耗时
                Thread.sleep(100);

                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                String request = new String(bytes);
                String response = "Processed: " + request;

                // 注册写事件
                client.register(selector, SelectionKey.OP_WRITE, ByteBuffer.wrap(response.getBytes()));
                selector.wakeup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}