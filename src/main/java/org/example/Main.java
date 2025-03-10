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
            while (true) {
                Socket socket = serverSocket.accept();
                new ChatClient(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}