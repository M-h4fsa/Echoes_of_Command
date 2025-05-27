package eoc.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private Button startButton;

    @FXML
    private Button leaderboardButton;

    @FXML
    private Button quitButton;

    private String username; // Store username

    public void setUsername(String username) {
        this.username = username;
        System.out.println("WelcomeController: Username set to " + username);
    }

    public void initialize() {
        setupHoverEffect(startButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(leaderboardButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(quitButton, "#3a4219", 1.05, 1.05);
    }

    private void setupHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
        String originalStyle = button.getStyle() != null ? button.getStyle() : "";
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
    protected void onStartButtonClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Username.fxml"));
            Scene scene = new Scene(loader.load());

            // Create the popup stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Enter Username");
            popupStage.setScene(scene);
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.setResizable(false);

            // Inject the parent stage into the UsernameController
            UsernameController controller = loader.getController();
            controller.setWelcomeStage((Stage) ((Node) event.getSource()).getScene().getWindow());
            controller.setUsernameStage(popupStage);  // to close it later

            popupStage.showAndWait();

        } catch (IOException e) {
            showError("Could not load Username.fxml: " + e.getMessage());
        }
    }

    @FXML
    protected void onLBButtonClick(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Leaderboard.fxml"));
            Scene scene = new Scene(loader.load());
            LeaderboardController controller = loader.getController();
            // Username not set at this stage, but can be added later if needed
            Stage stage = new Stage();
            stage.setTitle("Leaderboard");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            showError("Could not load Leaderboard.fxml: " + e.getMessage());
        }
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
        String css = "-fx-background-color: #eadcc7;";
        confirmExit.getDialogPane().setStyle(css);

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
        String css = "-fx-background-color: #eadcc7;";
        alert.getDialogPane().setStyle(css);
        alert.showAndWait();
    }
}