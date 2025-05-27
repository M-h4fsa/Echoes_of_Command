package eoc.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderController {

    @FXML private Button stalinButton;
    @FXML private Button churchillButton;
    @FXML private Button deGaulleButton;
    @FXML private Button rooseveltButton;
    @FXML private Button backButton;

    @FXML private FontIcon stalinIcon;
    @FXML private FontIcon churchillIcon;
    @FXML private FontIcon deGaulleIcon;
    @FXML private FontIcon rooseveltIcon;

    @FXML private Pane stalinDescriptionPane;
    @FXML private Label stalinDescriptionLabel;

    @FXML private Pane churchillDescriptionPane;
    @FXML private Label churchillDescriptionLabel;

    @FXML private Pane deGaulleDescriptionPane;
    @FXML private Label deGaulleDescriptionLabel;

    @FXML private Pane rooseveltDescriptionPane;
    @FXML private Label rooseveltDescriptionLabel;

    private final Map<String, String> leaderBackstories = new HashMap<>();
    private String selectedMode = "SINGLE"; // default
    private String username; // Store username

    public void setSelectedMode(String mode) {
        this.selectedMode = mode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void initialize() {
        loadLeaderBackstories();

        stalinDescriptionLabel.setText(leaderBackstories.getOrDefault("Joseph Stalin", "No backstory available."));
        churchillDescriptionLabel.setText(leaderBackstories.getOrDefault("Winston Churchill", "No backstory available."));
        deGaulleDescriptionLabel.setText(leaderBackstories.getOrDefault("Charles de Gaulle", "No backstory available."));
        rooseveltDescriptionLabel.setText(leaderBackstories.getOrDefault("Franklin D. Roosevelt", "No backstory available."));

        setupButtonHoverEffect(stalinButton, "#3a4219", 1.05, 1.05);
        setupButtonHoverEffect(churchillButton, "#3a4219", 1.05, 1.05);
        setupButtonHoverEffect(deGaulleButton, "#3a4219", 1.05, 1.05);
        setupButtonHoverEffect(rooseveltButton, "#3a4219", 1.05, 1.05);
        setupButtonHoverEffect(backButton, "#3a4219", 1.05, 1.05);

        setupIconHover(stalinIcon, stalinDescriptionPane);
        setupIconHover(deGaulleIcon, deGaulleDescriptionPane);
        setupIconHoverLeft(churchillIcon, churchillDescriptionPane);
        setupIconHoverLeft(rooseveltIcon, rooseveltDescriptionPane);
    }

    private void setupButtonHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
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

    private void setupIconHover(FontIcon icon, Pane descriptionPane) {
        icon.setCursor(javafx.scene.Cursor.HAND);
        icon.setOnMouseEntered(event -> {
            icon.setIconColor(javafx.scene.paint.Color.web("#6a7b63"));
            icon.setScaleX(1.05);
            icon.setScaleY(1.05);
            descriptionPane.setVisible(true);
        });
        icon.setOnMouseExited(event -> {
            icon.setIconColor(javafx.scene.paint.Color.web("#4c5d45"));
            icon.setScaleX(1.0);
            icon.setScaleY(1.0);
            descriptionPane.setVisible(false);
        });
    }

    private void setupIconHoverLeft(FontIcon icon, Pane descriptionPane) {
        icon.setCursor(javafx.scene.Cursor.HAND);
        icon.setOnMouseEntered(event -> {
            icon.setIconColor(javafx.scene.paint.Color.web("#6a7b63"));
            icon.setScaleX(2);
            icon.setScaleY(2);

            Label label = (Label) descriptionPane.getChildren().get(0);
            label.setWrapText(true);
            label.applyCss();
            label.layout();

            double requiredHeight = label.prefHeight(label.getWidth());
            descriptionPane.setPrefHeight(requiredHeight + 20);
            descriptionPane.setVisible(true);

            double iconX = icon.getLayoutX();
            double iconY = icon.getLayoutY();
            double paneWidth = descriptionPane.getPrefWidth();
            double offsetX = 10;

            descriptionPane.setLayoutX(iconX - paneWidth - offsetX);
            descriptionPane.setLayoutY(iconY);
        });

        icon.setOnMouseExited(event -> {
            icon.setIconColor(javafx.scene.paint.Color.web("#4c5d45"));
            icon.setScaleX(1.0);
            icon.setScaleY(1.0);
            descriptionPane.setVisible(false);
        });
    }

    private void loadLeaderBackstories() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("history.json")) {
            if (input == null) {
                System.err.println("❌ history.json not found in resources.");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> leaders = mapper.readValue(input, new TypeReference<>() {});
            for (Map<String, Object> leader : leaders) {
                String name = (String) leader.get("name");
                String backstory = (String) leader.get("backstory");
                leaderBackstories.put(name, backstory);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to load leader backstories: " + e.getMessage());
        }
    }

    @FXML
    public void JStalinBC(ActionEvent event) {
        launchGame(event, "Joseph Stalin");
    }

    @FXML
    public void WChurchillBC(ActionEvent event) {
        launchGame(event, "Winston Churchill");
    }

    @FXML
    public void CGaulleBC(ActionEvent event) {
        launchGame(event, "Charles de Gaulle");
    }

    @FXML
    public void FDRooseveltBC(ActionEvent event) {
        launchGame(event, "Franklin D. Roosevelt");
    }

    private void launchGame(ActionEvent event, String leaderName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Level.fxml"));
            Parent root = loader.load();
            LevelController controller = loader.getController();
            controller.initializeGame(selectedMode, leaderName, username); // Pass username

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Failed to load Level.fxml: " + e.getMessage());
            showErrorAlert("Failed to start game. Please try again.");
        }
    }

    @FXML
    public void onBackButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Parent root = loader.load();
            PlaymodeController controller = loader.getController();
            controller.setUsername(username); // Pass username back
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode.fxml: " + e.getMessage());
            showErrorAlert("Failed to return to play mode selection. Please try again.");
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