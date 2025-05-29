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
        this.username = username != null ? username.toLowerCase() : "unknown";
        System.out.println("PlaymodeController: Username set to: " + this.username);
    }

    @FXML
    public void initialize() {
        setupHoverEffect(randomButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(sequentialButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(singleButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);

        // Bind MenuItem actions
        bindMenuItem(statsButton, this::onStatsMenuItemClick);
        bindMenuItem(archiveButton, this::onArchiveMenuItemClick);
        bindMenuItem(courseButton, this::onCourseMenuItemClick);
    }

    private void bindMenuItem(Menu menu, Runnable action) {
        if (menu != null && !menu.getItems().isEmpty()) {
            menu.getItems().get(0).setOnAction(event -> action.run());
        } else {
            System.err.println("⚠️ Menu or its MenuItem is not initialized: " + (menu != null ? menu.getText() : "null"));
        }
    }

    private void setupHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
        if (button == null) return;
        String originalStyle = button.getStyle() != null ? button.getStyle() : "";
        DropShadow shadow = new DropShadow();

        button.setOnMouseEntered(e -> {
            button.setStyle(originalStyle + "; -fx-background-color: " + hoverColor + ";");
            button.setScaleX(scaleX);
            button.setScaleY(scaleY);
            button.setEffect(shadow);
        });

        button.setOnMouseExited(e -> {
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
        navigateTo(event, "/eoc/ui/Leader.fxml", controller -> ((LeaderController) controller).setUsername(username));
        System.out.println("Navigated to Leader selection for SINGLE mode, username=" + username);
    }

    @FXML
    public void onBackButtonClick(MouseEvent event) {
        navigateTo(event, "/eoc/ui/Welcome.fxml", controller -> ((WelcomeController) controller).setUsername(username));
        System.out.println("Returned to Welcome screen, username=" + username);
    }

    public void onStatsMenuItemClick() {
        navigateTo(backButton, "/eoc/ui/Stats.fxml", controller -> ((StatsController) controller).setUsername(username));
        System.out.println("Navigated to Stats screen, username=" + username);
    }

    public void onArchiveMenuItemClick() {
        navigateTo(backButton, "/eoc/ui/Archive.fxml", controller -> ((ArchiveController) controller).setUsername(username));
        System.out.println("Navigated to Archive screen, username=" + username);
    }

    public void onCourseMenuItemClick() {
        navigateTo(backButton, "/eoc/ui/Course.fxml", controller -> ((CourseController) controller).setUsername(username));
        System.out.println("Navigated to Course screen, username=" + username);
    }

    private void launchGame(MouseEvent event, String mode, String leaderName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Level.fxml"));
            Parent root = loader.load();
            LevelController controller = loader.getController();
            controller.initializeGame(mode, leaderName, username);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            stage.setResizable(false);
            System.out.println("Launched game: mode=" + mode + ", leader=" + leaderName + ", username=" + username);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Level.fxml: " + e.getMessage());
            showErrorAlert("Failed to start game.");
        }
    }

    private void navigateTo(MouseEvent event, String fxmlPath, java.util.function.Consumer<Object> setUsername) {
        try {
            Node source = (Node) event.getSource();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            setUsername.accept(loader.getController());

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Failed to load " + fxmlPath + ": " + e.getMessage());
            showErrorAlert("Failed to navigate to " + fxmlPath);
        }
    }

    private void navigateTo(Node node, String fxmlPath, java.util.function.Consumer<Object> setUsername) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            setUsername.accept(loader.getController());

            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);
        } catch (IOException e) {
            System.err.println("❌ Failed to load " + fxmlPath + ": " + e.getMessage());
            showErrorAlert("Failed to navigate to " + fxmlPath);
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