package org.example.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerScoketPollMain {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server start at: " + serverSocket.getLocalSocketAddress());
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Accepted： " + socket.getRemoteSocketAddress());
            executorService.submit(new PoolHandler(socket));
        }
    }
}

class PoolHandler implements Runnable{
    private Socket socket;
    public PoolHandler(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        try {
            byte[] bytes = new byte[1024];
            int read = socket.getInputStream().read(bytes);
            System.out.println("Receive: " + new String(bytes, 0, read));
            socket.getOutputStream().write(bytes,0, read);
            socket.close();
            System.out.println("Close: " + socket.getRemoteSocketAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
