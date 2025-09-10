package org.example.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) {
        try {
            // 1. 创建DatagramSocket，指定端口号
            DatagramSocket serverSocket = new DatagramSocket(9876);

            System.out.println("UDP服务器已启动，等待客户端连接...");

            while (true) {
                // 2. 创建接收数据的缓冲区
                byte[] receiveData = new byte[1024];

                // 3. 创建接收数据包
                DatagramPacket receivePacket = new DatagramPacket(
                        receiveData, receiveData.length);

                // 4. 接收客户端发送的数据
                serverSocket.receive(receivePacket);

                // 5. 解析接收到的数据
                String clientMessage = new String(
                        receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("收到客户端消息: " + clientMessage);

                // 获取客户端地址和端口
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // 6. 准备响应数据
                String responseMessage = "服务器已收到你的消息: " + clientMessage;
                byte[] sendData = responseMessage.getBytes();

                // 7. 创建并发送响应数据包
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);

                System.out.println("已向客户端发送响应");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
