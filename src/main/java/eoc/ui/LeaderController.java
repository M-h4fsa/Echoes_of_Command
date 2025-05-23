package eoc.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.kordamp.ikonli.javafx.FontIcon;

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

    public void initialize() {
        loadLeaderBackstories();

        // Assign backstories to labels
        stalinDescriptionLabel.setText(leaderBackstories.getOrDefault("Joseph Stalin", "No backstory available."));
        churchillDescriptionLabel.setText(leaderBackstories.getOrDefault("Winston Churchill", "No backstory available."));
        deGaulleDescriptionLabel.setText(leaderBackstories.getOrDefault("Charles de Gaulle", "No backstory available."));
        rooseveltDescriptionLabel.setText(leaderBackstories.getOrDefault("Franklin D. Roosevelt", "No backstory available."));

        // Set hover behavior
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
    private void setupIconHoverLeft(FontIcon icon, Pane descriptionPane) {
        icon.setCursor(javafx.scene.Cursor.HAND);

        icon.setOnMouseEntered(event -> {
            icon.setIconColor(javafx.scene.paint.Color.web("#6a7b63"));
            icon.setScaleX(2);
            icon.setScaleY(2);

            Label label = (Label) descriptionPane.getChildren().get(0); // Assuming label is the first child
            label.setWrapText(true);

            // Resize label and description pane to fit content
            label.applyCss();  // Ensures style is applied before layout
            label.layout();    // Computes the layout
            double requiredHeight = label.prefHeight(label.getWidth());

            // Set the pane to fit the label height
            descriptionPane.setPrefHeight(requiredHeight + 20); // Add some padding
            descriptionPane.setVisible(true);

            // Position to the left of the icon
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

    private void setupButtonHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
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

    @FXML public void JStalinBC() { /* Handle Stalin */ }

    @FXML public void WChurchillBC() { /* Handle Churchill */ }

    @FXML public void CGaulleBC() { /* Handle de Gaulle */ }

    @FXML public void FDRooseveltBC() { /* Handle FDR */ }

    @FXML public void onBackButtonClick() {
        // Navigate back
    }
}
