package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompleteReactorMultiThread {

    private static final int WORKER_POOL_SIZE = 10;
    private final ExecutorService workerPool = Executors.newFixedThreadPool(WORKER_POOL_SIZE);

    public static void main(String[] args) throws IOException {
        new CompleteReactorMultiThread().start(8080);
    }

    public void start(int port) throws IOException {
        // 主Reactor - 处理连接建立
        Selector mainSelector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(mainSelector, SelectionKey.OP_ACCEPT);
        System.out.println("Server started on port " + port);

        // 子Reactor线程组
        SubReactor[] subReactors = new SubReactor[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < subReactors.length; i++) {
            subReactors[i] = new SubReactor();
            new Thread(subReactors[i], "SubReactor-" + i).start();
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

                    // 分配给子Reactor并初始化上下文
                    SubReactor subReactor = subReactors[nextSubReactor];
                    subReactor.register(new ConnectionContext(client));
                    nextSubReactor = (nextSubReactor + 1) % subReactors.length;

                    System.out.println(Thread.currentThread().getName() +
                            " accepted connection to " +
                            subReactor.thread.getName());
                }
            }
        }
    }

    // 连接上下文，保存连接状态和待发送数据
    private static class ConnectionContext {
        final SocketChannel channel;
        final Queue<ByteBuffer> pendingWrites = new ConcurrentLinkedQueue<>();
        ByteBuffer currentReadBuffer = ByteBuffer.allocate(1024);

        ConnectionContext(SocketChannel channel) {
            this.channel = channel;
        }
    }

    // 子Reactor实现
    private class SubReactor implements Runnable {
        final Selector selector;
        final Thread thread;

        public SubReactor() throws IOException {
            this.selector = Selector.open();
            this.thread = Thread.currentThread();
        }

        public void register(ConnectionContext context) {
            try {
                // 初始注册读事件
                context.channel.register(selector, SelectionKey.OP_READ, context);
                selector.wakeup(); // 唤醒可能阻塞的select()
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
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

                        try {
                            if (key.isValid()) {
                                if (key.isReadable()) {
                                    handleRead(key);
                                } else if (key.isWritable()) {
                                    handleWrite(key);
                                }
                            }
                        } catch (IOException e) {
                            key.cancel();
                            ((Channel) key.channel()).close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleRead(SelectionKey key) throws IOException {
            ConnectionContext context = (ConnectionContext) key.attachment();
            SocketChannel channel = context.channel;
            ByteBuffer buffer = context.currentReadBuffer;

            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
                // 连接关闭
                key.cancel();
                channel.close();
                return;
            }

            if (bytesRead > 0) {
                // 提交给工作线程池处理
                buffer.flip();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);

                // 重置读缓冲区
                buffer.clear();
                context.currentReadBuffer = buffer;

                workerPool.execute(() -> processRequest(context, bytes));
            }
        }

        private void handleWrite(SelectionKey key) throws IOException {
            ConnectionContext context = (ConnectionContext) key.attachment();
            SocketChannel channel = context.channel;

            // 处理待发送队列
            synchronized (context.pendingWrites) {
                ByteBuffer buffer;
                while ((buffer = context.pendingWrites.peek()) != null) {
                    int written = channel.write(buffer);
                    if (written == -1) {
                        // 连接错误
                        key.cancel();
                        channel.close();
                        return;
                    }

                    if (buffer.hasRemaining()) {
                        // 还有剩余数据没写完，等待下次写事件
                        return;
                    } else {
                        // 当前buffer已写完，从队列移除
                        context.pendingWrites.poll();
                    }
                }

                // 所有数据已写完，取消写事件监听
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }
        }

        private void processRequest(ConnectionContext context, byte[] requestData) {
            try {
                // 模拟业务处理耗时
                Thread.sleep(100);

                String request = new String(requestData);
                String response = "Processed[" + Thread.currentThread().getName() + "]: " + request;

                // 准备响应数据
                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());

                // 添加到待发送队列
                synchronized (context.pendingWrites) {
                    context.pendingWrites.add(responseBuffer);

                    // 注册写事件
                    context.channel.register(selector,
                            SelectionKey.OP_WRITE | SelectionKey.OP_READ,
                            context);
                    selector.wakeup();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}