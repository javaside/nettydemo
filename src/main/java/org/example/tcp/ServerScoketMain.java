package org.example.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerScoketMain {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server start at: " + serverSocket.getLocalSocketAddress());
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New Connection: " + socket.getRemoteSocketAddress().toString());
            byte[] bytes = new byte[1024];

            InputStream inputStream = socket.getInputStream();
            int read = inputStream.read(bytes);

            System.out.println("Receive: " + new String(bytes,0,read));
            socket.getOutputStream().write(bytes,0,read);
            socket.close();
            System.out.println("Close: " + socket.getRemoteSocketAddress().toString());
        }
    }
}
