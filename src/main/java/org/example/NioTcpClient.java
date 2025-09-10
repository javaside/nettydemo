package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NioTcpClient {

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 8080;

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            // 配置为非阻塞模式（可选）
            // socketChannel.configureBlocking(false);

            // 连接服务器
            socketChannel.connect(new InetSocketAddress(hostname, port));

            System.out.println("Connected to server " + hostname + ":" + port);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (true) {
                System.out.print("Enter message (type 'exit' to quit): ");
                String userInput = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }

                // 发送消息到服务器
                buffer.clear();
                buffer.put(userInput.getBytes(StandardCharsets.UTF_8));
                buffer.flip();
                while (buffer.hasRemaining()) {
                    socketChannel.write(buffer);
                }

                // 接收服务器响应
                buffer.clear();
                int bytesRead = socketChannel.read(buffer);
                if (bytesRead > 0) {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String serverResponse = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("Server response: " + serverResponse);
                }
            }

        } catch (IOException e) {
            System.err.println("Error in NIO client: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Client disconnected");
    }
}
