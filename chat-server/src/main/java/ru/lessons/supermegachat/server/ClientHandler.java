package ru.lessons.supermegachat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private String username;
    final String AUTH = "/auth ";
    final String AUTH_SUCCESS = "/authok ";
    private DataInputStream in;
    private DataOutputStream out;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String strFromClient = in.readUTF();
                        if (strFromClient.startsWith(AUTH)) {
                            username = strFromClient.split("\\s+")[1];
                            sendMessage(AUTH_SUCCESS);
                            sendMessage("Вы вошли в чат под именем " + username);
                            server.subscribe(this);
                            break;
                        } else sendMessage("SERVER: Вам необходимо авторизоваться");
                    }
                    while (true) {
                        String strFromClient = in.readUTF();
                        if (strFromClient.startsWith("/")) {
                            continue;
                        }
                        server.broadcastMessage(username + ": " + strFromClient);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                }
            }).start();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
