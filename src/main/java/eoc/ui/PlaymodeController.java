package eoc.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;

public class PlaymodeController {
    @FXML
    private Button randomButton;

    @FXML
    private Button singleButton;

    @FXML
    private Button sequentialButton;

    @FXML
    private Button backButton;

    public void initialize() {
        setupHoverEffect(randomButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(singleButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(sequentialButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
    }

    private void setupHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
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

    public void onRandBC() {
        // Handle Randomised button click
    }

    public void onSingleBC() {
        // Handle Single Leader button click
    }

    public void onSeqBC() {
        // Handle Sequential button click
    }

    public void onBackButtonClick() {
        // Handle back button click
    }
}