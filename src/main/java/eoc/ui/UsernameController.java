package eoc.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class UsernameController {

    @FXML
    private TextField usernameField;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;
    private Stage welcomeStage;  // reference to close later
    private Stage usernameStage; // popup window

    public void setWelcomeStage(Stage stage) {
        this.welcomeStage = stage;
    }

    public void setUsernameStage(Stage stage) {
        this.usernameStage = stage;
    }
    public void initialize() {
        setupHoverEffect(submitButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
    }

    private void setupHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
        String originalStyle = button.getStyle();
        DropShadow shadow = new DropShadow();

        button.setOnMouseEntered((MouseEvent event) -> {
            button.setStyle(originalStyle + "; -fx-background-color: " + hoverColor + ";");
            button.setScaleX(scaleX);
            button.setScaleY(scaleY);
            button.setEffect(shadow);
        });

        button.setOnMouseExited((MouseEvent event) -> {
            button.setStyle(originalStyle);
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            button.setEffect(null);
        });
    }

    @FXML
    private void handleSubmit(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        if (username == null || username.trim().isEmpty()) {
            showAlert("Please enter a username.");
        } else {
            // Show welcome message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Welcome");
            alert.setHeaderText(null);
            alert.setContentText("Welcome, " + username + "!");
            alert.showAndWait();

            // Load Playmode scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Playmode.fxml"));
            Scene playmodeScene = new Scene(loader.load());

            // Show in a new stage or reuse welcomeStage
            Stage playStage = new Stage();
            playStage.setTitle("Play Mode");
            playStage.setScene(playmodeScene);
            playStage.show();

            // Close both previous windows
            if (usernameStage != null) usernameStage.close();
            if (welcomeStage != null) welcomeStage.close();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (usernameStage != null) {
            usernameStage.close();
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
