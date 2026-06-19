package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {

    @FXML private TabPane tabPane;
    @FXML private BorderPane root;

    @FXML
    public void initialize() { }

    @FXML
    private void handleQuitter() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous quitter ?");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) System.exit(0);
        });
    }

    @FXML
    private void handleAPropos() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Gestion de Bibliothèque");
        alert.setContentText("Application JavaFX - GI3 ENSAO\n2025/2026");
        alert.show();
    }
}