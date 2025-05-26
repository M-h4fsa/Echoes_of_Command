package eoc.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class EndRoundController {

    @FXML private Label scoreLabel;
    @FXML private Label timeLabel;
    @FXML private javafx.scene.control.Button quitButton;

    public void setScore(String score) {
        scoreLabel.setText(score);
    }

    @FXML
    public void onQuitButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode.fxml: " + e.getMessage());
            showErrorAlert("Failed to return to play mode selection. Please try again.");
        }
    }

    private void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}