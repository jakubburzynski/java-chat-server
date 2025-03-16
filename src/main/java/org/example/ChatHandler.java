package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private UserRepository userRepository;
    private MessageRepository messageRepository;
    public static ArrayList<ChatHandler> chatHandlers = new ArrayList<>();

    public ChatHandler(Socket socket) {
        try {
            this.socket = socket;
            userRepository = new UserRepository();
            messageRepository = new MessageRepository();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        String line = null;
        try {
            line = in.readLine();
            while (socket.isConnected() && line != null) {
                System.out.println("[client] received: " + line);
                JSONObject data = new JSONObject(line);
                System.out.println(data.toString());
                if (data.getString("action").equals("createUser")) {
                    handleCreateUser(data);
                } else if (data.getString("action").equals("checkUser")) {
                    handleCheckUser(data);
                } else if (data.getString("action").equals("connect")) {
                    handleConnect();
                } else if (data.getString("action").equals("sendMessage")) {
                    handleMessageSend(data);
                }
                line = in.readLine();

            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void groupSend(String data) {
        for (ChatHandler handler : chatHandlers) {
            try {
                handler.out.write(data);
                handler.out.newLine();
                handler.out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCreateUser(JSONObject data) throws IOException {
        JSONObject response = new JSONObject();
        response.put("action", "createUser");
        try {
            User user = userRepository.create(data.getString("username"));
            response.put("success", true);
            response.put("id", user.getId().toString());
        } catch (Exception e) {
            response.put("success", false);
        } finally {
            out.write(response.toString());
            out.newLine();
            out.flush();
        }
    }

    private void handleCheckUser(JSONObject data) throws IOException {
        JSONObject response = new JSONObject();
        response.put("action", "checkUser");
        User user = userRepository.findById(UUID.fromString(data.getString("id")));
        response.put("success", true);
        if (user == null) {
            response.put("success", false);
        }
        out.write(response.toString());
        out.newLine();
        out.flush();
    }

    private void handleConnect() throws IOException {
        chatHandlers.add(this);
        System.out.println(chatHandlers);
        JSONObject response = new JSONObject();
        response.put("action", "connect");
        response.put("success", true);
        JSONArray messagesJson = new JSONArray();
        List<Message> messages = messageRepository.findAll();
        for (Message message : messages) {
            JSONObject messageJson = new JSONObject();
            messageJson.put("senderId", message.getSender().getId().toString());
            messageJson.put("senderName", message.getSender().getName().toString());
            messageJson.put("message", message.getText());
            messageJson.put("id", message.getId().toString());
            messageJson.put("sentAt", message.getCreatedAt().toString());
            messagesJson.put(messageJson);
        }
        response.put("messages", messagesJson);
        out.write(response.toString());
        out.newLine();
        out.flush();
    }

    private void handleMessageSend(JSONObject data) throws IOException {
        UUID userId = UUID.fromString(data.getString("id"));
        String messageText = data.getString("message");
        Message message = messageRepository.create(messageText, userId);
        JSONObject response = new JSONObject();
        response.put("action", "newMessage");
        response.put("success", true);
        response.put("senderId", message.getSender().getId().toString());
        response.put("senderName", message.getSender().getName().toString());
        response.put("message", message.getText());
        response.put("id", message.getId().toString());
        response.put("sentAt", message.getCreatedAt().toString());
        groupSend(response.toString());
    }
}
