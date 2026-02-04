module it.unipi.WarTrivia.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens it.unipi.WarTrivia.Client to javafx.fxml;
    exports it.unipi.WarTrivia.Client;
    requires com.google.gson;

    opens it.unipi.WarTrivia.Client.models to com.google.gson;
}
