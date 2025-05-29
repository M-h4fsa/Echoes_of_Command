package eoc.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    @FXML private Button startButton;
    @FXML private Button leaderboardButton;
    @FXML private Button quitButton;
    @FXML private MenuItem englishMenuItem;
    @FXML private MenuItem arabicMenuItem;
    @FXML private MenuItem aboutMenuItem;

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
    protected void onStartButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Username.fxml"));
            Scene scene = new Scene(loader.load());

            // Create the popup stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Enter Username");
            popupStage.setScene(scene);
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(startButton.getScene().getWindow());
            popupStage.setResizable(false);

            // Inject the parent stage into the UsernameController
            UsernameController controller = loader.getController();
            controller.setWelcomeStage((Stage) startButton.getScene().getWindow());
            controller.setUsernameStage(popupStage);
            controller.setWelcomeStageUsername(username); // Pass existing username if any

            popupStage.showAndWait();
        } catch (IOException e) {
            showError("Could not load Username.fxml: " + e.getMessage());
        }
    }

    @FXML
    protected void onLBButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Leaderboard.fxml"));
            Scene scene = new Scene(loader.load());
            LeaderboardController controller = loader.getController();
            controller.setUsername(username); // Pass username
            Stage stage = new Stage();
            stage.setTitle("Leaderboard");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initOwner(leaderboardButton.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            showError("Could not load Leaderboard.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void onQuitButtonClick() {
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType stayButton = new ButtonType("I'd rather stay");

        Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to quit?", yesButton, stayButton);
        confirmExit.setTitle("Exit Game");
        confirmExit.setHeaderText("We're sad to see you go :(");
        confirmExit.getDialogPane().setStyle("-fx-background-color: #eadcc7;");
        confirmExit.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                Platform.exit();
            }
        });
    }
    @FXML
    private void onAboutMenuItemClick() {
        String aboutMessage = "Quick Reference Guide:\n\n" +
                "Start ▶ Enter your Username.\n\n" +
                "Menu : Choose:\n\n" +
                "Play – start a new game.\n" +
                "Stats – view your overall performance.\n" +
                "Archive – browse past levels.\n" +
                "Leaderboard – see top players.\n" +
                "Download – export course.\n" +
                "Logout – end session.\n\n" +
                "During Play 🎮 Finish a round → your score is saved, added to your archive, and updates the leaderboard.\n\n" +
                "Next Time ⏭ Re-enter your Username to view stats/archive, or keep playing.";
        showInfo("About Echoes of Command", aboutMessage);
    }

    @FXML
    private void onEnglishMenuItemClick() {
        LanguageManager.getInstance().setLanguage("English");
        showInfo("Language Changed", "Selected language: English");
    }

    @FXML
    private void onArabicMenuItemClick() {
        LanguageManager.getInstance().setLanguage("Arabic");
        showInfo("Language Changed", "Selected language: Arabic");
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Navigation Error");
            alert.setContentText(message);
            alert.getDialogPane().setStyle("-fx-background-color: #eadcc7;");
            alert.showAndWait();
        });
    }

    private void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getDialogPane().setStyle("-fx-background-color: #eadcc7;");
            alert.showAndWait();
        });
    }
}