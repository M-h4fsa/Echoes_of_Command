package eoc.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.effect.DropShadow;

public class LeaderController {
    @FXML
    private Button stalinButton;

    @FXML
    private Button churchillButton;

    @FXML
    private Button deGaulleButton;

    @FXML
    private Button rooseveltButton;

    @FXML
    private Button backButton;

    @FXML
    private FontIcon stalinIcon;

    @FXML
    private FontIcon churchillIcon;

    @FXML
    private FontIcon deGaulleIcon;

    @FXML
    private FontIcon rooseveltIcon;

    public void initialize() {
        // Hover effects for buttons
        setupButtonHoverEffect(stalinButton, "#3a4219", 1.05, 1.05);
        setupButtonHoverEffect(churchillButton, "#3a4219", 1.05, 1.05);
        setupButtonHoverEffect(deGaulleButton, "#3a4219", 1.05, 1.05);
        setupButtonHoverEffect(rooseveltButton, "#3a4219", 1.05, 1.05);
        setupButtonHoverEffect(backButton, "#3a4219", 1.05, 1.05);

        // Hover and click effects for icons
        setupIconHoverAndClick(stalinIcon, "Joseph Stalin");
        setupIconHoverAndClick(churchillIcon, "Winston Churchill");
        setupIconHoverAndClick(deGaulleIcon, "Charles de Gaulle");
        setupIconHoverAndClick(rooseveltIcon, "Franklin D. Roosevelt");
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

    private void setupIconHoverAndClick(FontIcon icon, String leaderName) {
        // Set cursor to hand to indicate clickability
        icon.setCursor(javafx.scene.Cursor.HAND);

        // Hover effect
        icon.setOnMouseEntered((MouseEvent event) -> {
            icon.setIconColor(javafx.scene.paint.Color.web("#6a7b63")); // Lighter shade on hover
            icon.setScaleX(1.05);
            icon.setScaleY(1.05);
        });

        icon.setOnMouseExited((MouseEvent event) -> {
            icon.setIconColor(javafx.scene.paint.Color.web("#4c5d45")); // Original color
            icon.setScaleX(1.0);
            icon.setScaleY(1.0);
        });

        // Click handler (placeholder for now)
        icon.setOnMouseClicked((MouseEvent event) -> {
            System.out.println("Clicked on " + leaderName + " icon. Show leader details here.");
            // You can replace this with a dialog or UI update to show leader details
        });
    }

    public void JStalinBC() {
        // Handle Joseph Stalin button click
    }

    public void WChurchillBC() {
        // Handle Winston Churchill button click
    }

    public void CGaulleBC() {
        // Handle Charles de Gaulle button click
    }

    public void FDRooseveltBC() {
        // Handle Franklin D. Roosevelt button click
    }

    public void onBackButtonClick() {
        // Handle back button click
    }
}