package org.example.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerScoketMultiMain {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server start at: " + serverSocket.getLocalSocketAddress());
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted： " + socket.getRemoteSocketAddress());
            new Thread(new Handler(socket)).start();
        }
    }
}

class Handler implements Runnable{
    private Socket socket;
    public Handler(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        try {
            byte[] bytes = new byte[1024];

            InputStream inputStream = socket.getInputStream();
            int read = inputStream.read(bytes);

            System.out.println("Receive: " + new String(bytes,0,read));
            socket.getOutputStream().write(bytes,0,read);
            socket.close();
            System.out.println("Close: " + socket.getRemoteSocketAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
