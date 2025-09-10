package org.example.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPMulticastReceiver {
    public static void main(String[] args) {
        try {
            // 1. 创建多播socket并绑定端口
            MulticastSocket socket = new MulticastSocket(9876);

            // 2. 定义多播地址 (224.0.0.0 到 239.255.255.255)
            InetAddress group = InetAddress.getByName("230.0.0.1");

            // 3. 加入多播组
            socket.joinGroup(group);

            System.out.println("多播接收器已启动，等待消息...");

            while (true) {
                // 4. 准备接收数据
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // 5. 接收数据
                socket.receive(packet);

                // 6. 处理接收到的数据
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("收到多播消息: " + message);

                // 可以添加退出条件
                if ("exit".equals(message)) {
                    break;
                }
            }

            // 7. 离开多播组并关闭socket
            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
