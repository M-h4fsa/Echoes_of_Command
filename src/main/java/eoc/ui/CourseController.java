package eoc.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.IOException;
import java.io.File;

public class CourseController {
    @FXML
    private Button downloadButton;

    @FXML
    private Button backButton;

    @FXML
    private TextArea courseTextArea;

    private String username; // Store username

    public void setUsername(String username) {
        this.username = username;
        System.out.println("CourseController: Username set to " + username);
    }

    public void initialize() {
        setupHoverEffect(downloadButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);

        // Load content from language-specific course file into TextArea
        String courseFilePath = LanguageManager.getInstance().getCourseFilePath();
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(courseFilePath)) {
            if (input == null) {
                courseTextArea.setText("Error: " + courseFilePath + " not found in resources.");
            } else {
                String content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                courseTextArea.setText(content);
            }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Parent root = loader.load();
            PlaymodeController controller = loader.getController();
            if (username != null) {
                controller.setUsername(username); // Pass username back
            }

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode.fxml: " + e.getMessage());
        }
    }
}