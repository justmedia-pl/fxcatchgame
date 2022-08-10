module com.example.catchgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.catchgame to javafx.fxml;
    exports com.example.catchgame;
}