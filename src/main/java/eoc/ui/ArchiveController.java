package eoc.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArchiveController {
    @FXML private Button searchButton;
    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private TextArea resultArea;

    private static final String ARCHIVE_JSON_PATH = "archive.json";
    private static final Path ARCHIVE_FILE_PATH = Paths.get("Echoes_of_Command", ARCHIVE_JSON_PATH);
    private List<ArchiveEntry> archiveEntries;
    private String username; // Store username for navigation

    public void setUsername(String username) {
        this.username = username;
    }

    public void initialize() {
        if (username == null) {
            System.err.println("⚠️ Username not set in ArchiveController");
            resultArea.setText("Error: Username not set.");
            return;
        }
        setupHoverEffects();
        loadArchiveData();
    }

    private void loadArchiveData() {
        if (!Files.exists(ARCHIVE_FILE_PATH)) {
            resultArea.setText("Archive data not found at " + ARCHIVE_FILE_PATH);
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            archiveEntries = mapper.readValue(Files.readAllBytes(ARCHIVE_FILE_PATH), new TypeReference<List<ArchiveEntry>>() {});
            archiveEntries.sort(Comparator.comparingInt(entry -> entry.levelNumber));
            displayPlayerEntries();

        } catch (IOException e) {
            System.err.println("❌ Error loading archive.json: " + e.getMessage());
            resultArea.setText("Error loading archive: " + e.getMessage());
        }
    }

    private void displayPlayerEntries() {
        if (archiveEntries == null) return;

        List<ArchiveEntry> playerEntries = archiveEntries.stream()
                .filter(entry -> entry.username != null && entry.username.equals(username))
                .collect(Collectors.toList());

        if (playerEntries.isEmpty()) {
            resultArea.setText("No archive entries found for " + username);
        } else {
            displayEntries(playerEntries);
        }
    }

    private void displayEntries(List<ArchiveEntry> entries) {
        StringBuilder display = new StringBuilder();
        for (ArchiveEntry entry : entries) {
            display.append("Level ").append(entry.levelNumber).append(": ").append(entry.description).append("\n")
                    .append("Leader: ").append(entry.leader).append("\n")
                    .append("Player's Choice: ").append(entry.playerChoice)
                    .append(" (").append(entry.isCorrect ? "Correct" : "Incorrect").append(")\n")
                    .append("Historical Outcome: ").append(entry.historicalChoice).append("\n")
                    .append("Summary: ").append(entry.summary).append("\n")
                    .append("--------------------------------------------------\n");
        }
        resultArea.setText(display.toString());
    }

    @FXML
    public void onSearchButtonClick() {
        filterAndDisplay();
    }

    @FXML
    public void onSearchKeyReleased(KeyEvent event) {
        filterAndDisplay();
    }

    private void filterAndDisplay() {
        if (archiveEntries == null) return;

        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            displayPlayerEntries();
            return;
        }

        List<ArchiveEntry> filtered = archiveEntries.stream()
                .filter(entry -> entry.username != null && entry.username.equals(username))
                .filter(entry ->
                        entry.leader.toLowerCase().contains(keyword) ||
                                entry.description.toLowerCase().contains(keyword) ||
                                entry.historicalChoice.toLowerCase().contains(keyword) ||
                                entry.playerChoice.toLowerCase().contains(keyword)
                )
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            resultArea.setText("No results for: " + keyword);
        } else {
            displayEntries(filtered);
        }
    }

    @FXML
    public void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Scene scene = new Scene(loader.load());
            PlaymodeController controller = loader.getController();
            if (username != null) {
                controller.setUsername(username); // Pass username back
            }

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode.fxml: " + e.getMessage());
            showErrorAlert("Failed to return to play mode. Try again.");
        }
    }

    private void setupHoverEffects() {
        setupHoverEffect(searchButton, "#e6d9a8", 1.05, 1.05);
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

    private void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            String css = "-fx-background-color: #eadcc7;";
            alert.getDialogPane().setStyle(css);
            alert.showAndWait();
        });
    }

    public static class ArchiveEntry {
        public String username;
        public String leader;
        public int levelNumber;
        public String description;
        public String historicalChoice;
        public String summary;
        public String playerChoice;
        public boolean isCorrect;
    }
}