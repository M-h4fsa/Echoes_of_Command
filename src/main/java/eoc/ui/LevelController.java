package eoc.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;

public class LevelController {
    @FXML
    private Button choiceOneButton;

    @FXML
    private Button choiceTwoButton;

    @FXML
    private Button backButton;

    public void initialize() {
        setupHoverEffect(choiceOneButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(choiceTwoButton, "#e6d9a8", 1.05, 1.05);
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

    public void onChoiceOneBC() {
        // Handle Choice 1 button click
    }

    public void onChoiceTwoBC() {
        // Handle Choice 2 button click
    }

    public void onBackButtonClick() {
        // Handle back button click
    }
}