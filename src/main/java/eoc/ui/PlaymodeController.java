package eoc.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.effect.DropShadow;

import java.io.IOException;

public class PlaymodeController {

    @FXML private Button randomButton;
    @FXML private Button sequentialButton;
    @FXML private Button singleButton;
    @FXML private Button backButton;
    @FXML private Menu statsButton;
    @FXML private Menu archiveButton;
    @FXML private Menu courseButton;
    private String username;

    public void setUsername(String username) {
        this.username = username != null ? username.toLowerCase() : "unknown"; // Ensure lowercase
        System.out.println("PlaymodeController: Username set to: " + this.username);

        // Safe stage access for user data
        if (backButton != null && backButton.getScene() != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            if (stage != null) {
                stage.setUserData(this.username);
            }
        }
    }

    @FXML
    public void initialize() {
        // Setup hover effects for buttons
        setupHoverEffect(randomButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(sequentialButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(singleButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);

        // Bind MenuItem actions
        if (statsButton != null && !statsButton.getItems().isEmpty()) {
            statsButton.getItems().get(0).setOnAction(event -> onStatsMenuItemClick());
        } else {
            System.err.println("Warning: StatsButton or its MenuItem is not properly initialized");
        }

        if (archiveButton != null && !archiveButton.getItems().isEmpty()) {
            archiveButton.getItems().get(0).setOnAction(event -> onArchiveMenuItemClick());
        } else {
            System.err.println("Warning: ArchiveButton or its MenuItem is not properly initialized");
        }

        if (courseButton != null && !courseButton.getItems().isEmpty()) {
            courseButton.getItems().get(0).setOnAction(event -> onCourseMenuItemClick());
        } else {
            System.err.println("Warning: CourseButton or its MenuItem is not properly initialized");
        }
    }

    private void setupHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
        if (button == null) return; // Safety check
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
            controller.setUsername(username); // Pass username

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Navigated to Leader selection for SINGLE mode, username: " + username);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Leader.fxml: " + e.getMessage());
            showErrorAlert("Failed to load leader selection. Please try again.");
        }
    }

    @FXML
    public void onBackButtonClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Welcome.fxml"));
            Parent root = loader.load();
            WelcomeController controller = loader.getController();
            controller.setUsername(username); // Pass username

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Returned to Welcome screen, username: " + username);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Welcome.fxml: " + e.getMessage());
            showErrorAlert("Failed to return to welcome screen. Please try again.");
        }
    }

    public void onStatsMenuItemClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Stats.fxml"));
            Parent root = loader.load();
            StatsController controller = loader.getController();
            controller.setUsername(username); // Pass username

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Navigated to Stats screen, username: " + username);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Stats.fxml: " + e.getMessage());
            showErrorAlert("Failed to load stats. Please try again.");
        }
    }

    public void onArchiveMenuItemClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Archive.fxml"));
            Parent root = loader.load();
            ArchiveController controller = loader.getController();
            controller.setUsername(username); // Pass username

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Navigated to Archive screen, username: " + username);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Archive.fxml: " + e.getMessage());
            showErrorAlert("Failed to load archive. Please try again.");
        }
    }

    public void onCourseMenuItemClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Course.fxml"));
            Parent root = loader.load();
            CourseController controller = loader.getController();
            controller.setUsername(username); // Pass username

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            System.out.println("Navigated to Course screen, username: " + username);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Course.fxml: " + e.getMessage());
            showErrorAlert("Failed to load course. Please try again.");
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
            System.out.println("Launched game: mode=" + mode + ", leader=" + leaderName + ", username=" + username);
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
        alert.getDialogPane().setStyle("-fx-background-color: #eadcc7;");
        alert.showAndWait();
    }
}