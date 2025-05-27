package eoc.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class EndRoundController {

    @FXML private Label scoreLabel;
    @FXML private Label timeLabel;
    @FXML private Button quitButton;

    private Stage mainStage; // Store the main stage

    public void setScore(String score) {
        if (scoreLabel == null) {
            System.err.println("❌ Cannot set score: scoreLabel is null");
            return;
        }
        scoreLabel.setText(score);
    }

    public void setTime(String time) {
        if (timeLabel == null) {
            System.err.println("❌ Cannot set time: timeLabel is null");
            return;
        }
        timeLabel.setText(time);
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    public void onQuitButtonClick(ActionEvent event) {
        try {
            // Close the dialog
            Stage dialogStage = (Stage) quitButton.getScene().getWindow();
            dialogStage.close();

            // Navigate to main stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Scene scene = new Scene(loader.load());

            if (mainStage == null) {
                System.err.println("❌ Cannot navigate: mainStage is null");
                showError("Cannot return to play mode. Error navigating stage.");
                return;
            }
            mainStage.setScene(scene);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode: " + e.getMessage());
            showError("Failed to return to play mode. Try again.");
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            String css = "-fx-background-color: #eadcc7;";
            alert.getDialogPane().setStyle(css);
            alert.showAndWait();
        });
    }
}