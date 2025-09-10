package org.example.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBroadcaster {
    public static void main(String[] args) {
        try {
            // 1. 创建DatagramSocket
            DatagramSocket socket = new DatagramSocket();

            // 2. 设置广播模式
            socket.setBroadcast(true);

            // 3. 定义广播地址
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");

            // 4. 准备广播消息
            String message = "这是一条广播消息!";
            byte[] buffer = message.getBytes();

            // 5. 创建并发送广播包
            DatagramPacket packet = new DatagramPacket(
                    buffer, buffer.length, broadcastAddress, 9876);
            socket.send(packet);

            System.out.println("广播消息已发送");

            // 关闭socket
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}