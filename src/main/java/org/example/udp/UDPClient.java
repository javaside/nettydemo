package org.example.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        try {
            // 1. 创建DatagramSocket
            DatagramSocket clientSocket = new DatagramSocket();

            // 获取服务器地址
            InetAddress serverAddress = InetAddress.getByName("localhost");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("请输入要发送的消息(输入exit退出): ");
                String message = scanner.nextLine();

                if ("exit".equalsIgnoreCase(message)) {
                    break;
                }

                // 2. 准备发送数据
                byte[] sendData = message.getBytes();

                // 3. 创建并发送数据包
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData, sendData.length, serverAddress, 9876);
                clientSocket.send(sendPacket);

                System.out.println("消息已发送到服务器");

                // 4. 创建接收数据的缓冲区
                byte[] receiveData = new byte[1024];

                // 5. 创建接收数据包
                DatagramPacket receivePacket = new DatagramPacket(
                        receiveData, receiveData.length);

                // 6. 接收服务器响应
                clientSocket.receive(receivePacket);

                // 7. 解析响应数据
                String serverResponse = new String(
                        receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("服务器响应: " + serverResponse);
            }

            // 关闭资源
            clientSocket.close();
            scanner.close();
            System.out.println("客户端已关闭");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}