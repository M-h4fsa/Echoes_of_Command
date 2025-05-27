package eoc.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class PlaymodeController {

    @FXML private Button randomButton;
    @FXML private Button sequentialButton;
    @FXML private Button singleButton;
    @FXML private Button backButton;
    private String username; // Store username

    public void setUsername(String username) {
        this.username = username;
    }

    public void initialize() {
        setupHoverEffect(randomButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(sequentialButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(singleButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
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
    public void onRandomButtonClick(MouseEvent event) {
        launchGame(event, "RANDOM", null);
    }

    @FXML
    public void onSequentialButtonClick(MouseEvent event) {
        launchGame(event, "SEQUENTIAL", null);
    }

    @FXML
    public void onSingleButtonClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Leader.fxml"));
            Parent root = loader.load();
            LeaderController controller = loader.getController();
            controller.setSelectedMode("SINGLE");
            controller.setUsername(username); // Pass username

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Failed to load Leader.fxml: " + e.getMessage());
            showErrorAlert("Failed to load leader selection screen. Please try again.");
        }
    }

    @FXML
    public void onBackButtonClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Failed to load MainMenu.fxml: " + e.getMessage());
            showErrorAlert("Failed to return to main menu. Please try again.");
        }
    }

    private void launchGame(MouseEvent event, String mode, String leaderName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Level.fxml"));
            Parent root = loader.load();
            LevelController controller = loader.getController();
            controller.initializeGame(mode, leaderName, username); // Pass username

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Failed to load Level.fxml: " + e.getMessage());
            showErrorAlert("Failed to start game. Please try again.");
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        String css = "-fx-background-color: #eadcc7;";
        alert.getDialogPane().setStyle(css);
        alert.showAndWait();
    }
}