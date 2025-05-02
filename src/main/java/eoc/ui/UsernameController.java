package eoc.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class UsernameController {

    @FXML
    private TextField usernameField;

    @FXML
    private void handleSubmit() {
        String username = usernameField.getText();
        if (username == null || username.trim().isEmpty()) {
            showAlert("Please enter a username.");
        } else {
            showAlert("Welcome, " + username + "!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Username Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

