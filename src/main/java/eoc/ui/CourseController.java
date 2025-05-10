package eoc.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.File;

public class CourseController {
    @FXML
    private Button downloadButton;

    @FXML
    private Button backButton;

    @FXML
    private TextArea courseTextArea;

    public void initialize() {
        setupHoverEffect(downloadButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);

        // Load content from course.txt into TextArea
        try {
            Path filePath = Paths.get("src/main/resources/course.txt");
            String content = Files.readString(filePath);
            courseTextArea.setText(content);
        } catch (IOException e) {
            courseTextArea.setText("Error loading course content: " + e.getMessage());
        }
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

    @FXML
    public void onDwCourseBC() {
        // Create a FileChooser to let the user select where to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Course Content");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("course_content.txt");

        // Show save dialog
        File file = fileChooser.showSaveDialog(downloadButton.getScene().getWindow());

        // If a file is selected, save the TextArea content to it
        if (file != null) {
            try {
                Files.writeString(file.toPath(), courseTextArea.getText());
            } catch (IOException e) {
                courseTextArea.setText("Error saving file: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onBackButtonClick() {
        // Handle back button click
    }
}