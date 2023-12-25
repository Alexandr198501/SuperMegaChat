module ru.lessons.chatclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.lessons.chatclient to javafx.fxml;
    exports ru.lessons.chatclient;
}