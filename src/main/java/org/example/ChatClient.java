package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

public class ChatClient extends Thread {
    private Socket socket;
    private BufferedReader in;
    private UUID id = UUID.randomUUID();

    public ChatClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("[client][" + id + "] connected from address: " + socket.getRemoteSocketAddress());
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            String line = in.readLine();
            while (line != null) {
                System.out.println("[client][" + id + "] received: " + line);
                line = in.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
