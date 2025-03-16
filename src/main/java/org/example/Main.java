package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int PORT = 17777;
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("server listening on port: " + PORT);
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
//                new ChatClient(socket).start();
                ChatHandler chatHandler = new ChatHandler(socket);
                new Thread(chatHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}