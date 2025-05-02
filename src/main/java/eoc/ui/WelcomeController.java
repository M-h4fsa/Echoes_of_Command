package eoc.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class WelcomeController {
    @FXML
    protected void onStartButtonClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("username.fxml"));
            Scene scene = new Scene(loader.load());

            Stage popupStage = new Stage();
            popupStage.setTitle("Enter Username");
            popupStage.setScene(scene);


            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());

            popupStage.setResizable(false);
            popupStage.showAndWait(); // Show as modal pop-up

        } catch (IOException e) {
            showError("Could not load username.fxml: " + e.getMessage());
        }
    }
    @FXML
    protected void onLBButtonClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Leaderboard");
        alert.setHeaderText(null);
        alert.setContentText("Welcome to leaderboard table!");
        alert.showAndWait();
    }
    @FXML
    private void onQuitButtonClick() {
        // Create custom buttons
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType stayButton = new ButtonType("I'd rather stay");


        Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to quit?", yesButton, stayButton);

        confirmExit.setTitle("Exit Game");
        confirmExit.setHeaderText("We're sad to see you go :(");

        // Show dialog and handle result
        confirmExit.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                Platform.exit();
            }
        });
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Navigation Error");
        alert.setContentText(message);
        alert.showAndWait();
    }


}
