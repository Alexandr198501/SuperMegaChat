package ru.lessons.chatclient;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController {
    @FXML
    HBox msgPanel, authPanel;
    @FXML
    TextArea chatArea;
    @FXML
    TextField messageField, usernameField;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    final String AUTH = "/auth ";
    final String AUTH_SUCCESS = "/authok ";


    public void sendMessage(){
        try {
            out.writeUTF(messageField.getText());
            messageField.clear();
            messageField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToAuth(){
        connect();
        try {
            out.writeUTF(AUTH + usernameField.getText());
        } catch (IOException e) {
            showError("Невоможно отправить запрос авторизации на сервер");
        }
    }

    public void connect(){
        if (socket != null && !socket.isClosed())
            return;
        try {
            socket = new Socket("localhost", 8189);
            out = new DataOutputStream(socket.getOutputStream());
            in =new DataInputStream(socket.getInputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String strFromServer = in.readUTF();
                        if (strFromServer.equals(AUTH_SUCCESS)){
                            msgPanel.setVisible(true);
                            msgPanel.setManaged(true);
                            authPanel.setVisible(false);
                            authPanel.setManaged(false);
                            break;
                        }
                        chatArea.appendText(strFromServer + "\n");
                    }
                    while (true) {
                        String strFromServer = in.readUTF();
                        chatArea.appendText(strFromServer + '\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            showError("Не удалось подключиться к серверу");
        }
    }

    public void showError(String message){
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }
}