package eoc.ui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ArchiveController {
    @FXML private Button searchButton;
    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private TextArea resultArea;

    private static final String ARCHIVE_JSON_PATH = "archive.json";
    private static final Path ARCHIVE_FILE_PATH = Paths.get("Echoes_of_Command", ARCHIVE_JSON_PATH);
    private List<ArchiveEntry> archiveEntries;
    private String username;

    public void setUsername(String username) {
        this.username = username;
        loadArchiveData();
    }

    @FXML
    public void initialize() {
        setupHoverEffects();
    }

    private void loadArchiveData() {
        try {
            if (!Files.exists(ARCHIVE_FILE_PATH)) {
                resultArea.setText("No archive data found");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            archiveEntries = mapper.readValue(
                    Files.readAllBytes(ARCHIVE_FILE_PATH),
                    new TypeReference<List<ArchiveEntry>>() {}
            );

            displayAllEntries();
        } catch (IOException e) {
            showError("Failed to load archive: " + e.getMessage());
        }
    }

    private void displayAllEntries() {
        List<ArchiveEntry> playerEntries = archiveEntries.stream()
                .filter(entry -> username.equals(entry.username))
                .sorted(Comparator.comparingInt(entry -> entry.levelNumber))
                .collect(Collectors.toList());

        StringBuilder archiveText = new StringBuilder();
        archiveText.append("=== COMPLETE GAME HISTORY ===\n\n");

        if (playerEntries.isEmpty()) {
            archiveText.append("No entries found for ").append(username).append("\n");
        } else {
            String currentLeader = null;
            for (ArchiveEntry entry : playerEntries) {
                if (!entry.leader.equals(currentLeader)) {
                    currentLeader = entry.leader;
                    archiveText.append("\n=== ").append(currentLeader.toUpperCase()).append(" ===\n\n");
                }

                archiveText.append(String.format("Level %d\n", entry.levelNumber));
                archiveText.append(String.format("Scenario: %s\n", entry.description));
                archiveText.append(String.format("Your Choice: %s (%s)\n",
                        entry.playerChoice, entry.isCorrect ? "✓ Correct" : "✗ Incorrect"));
                archiveText.append(String.format("Historical Outcome: %s\n", entry.historicalChoice));
                archiveText.append(String.format("Summary: %s\n", entry.summary));
                archiveText.append("----------------------------------------\n");
            }
        }

        resultArea.setText(archiveText.toString());
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
        if (archiveEntries == null || archiveEntries.isEmpty()) {
            return;
        }

        String keyword = searchField.getText().trim().toLowerCase();
        StringBuilder filteredText = new StringBuilder();
        filteredText.append("=== SEARCH RESULTS ===\n\n");

        if (keyword.isEmpty()) {
            displayAllEntries();
            return;
        }

        List<ArchiveEntry> filtered = archiveEntries.stream()
                .filter(entry -> username.equals(entry.username))
                .filter(entry -> matchesKeyword(entry, keyword))
                .sorted(Comparator.comparingInt(entry -> entry.levelNumber))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            filteredText.append("No results found for: '").append(keyword).append("'\n");
        } else {
            filtered.forEach(entry -> {
                filteredText.append(String.format("Level %d\n", entry.levelNumber));
                filteredText.append(String.format("Leader: %s\n", entry.leader));
                filteredText.append(String.format("Scenario: %s\n", entry.description));
                filteredText.append(String.format("Your Choice: %s (%s)\n",
                        entry.playerChoice, entry.isCorrect ? "Correct" : "Incorrect"));
                filteredText.append(String.format("Historical Outcome: %s\n", entry.historicalChoice));
                filteredText.append(String.format("Summary: %s\n", entry.summary));
                filteredText.append("--------------------------------------------------\n\n");
            });
        }

        resultArea.setText(filteredText.toString());
    }

    private boolean matchesKeyword(ArchiveEntry entry, String keyword) {
        return (entry.leader != null && entry.leader.toLowerCase().contains(keyword)) ||
                (entry.description != null && entry.description.toLowerCase().contains(keyword)) ||
                (entry.historicalChoice != null && entry.historicalChoice.toLowerCase().contains(keyword)) ||
                (entry.playerChoice != null && entry.playerChoice.toLowerCase().contains(keyword)) ||
                (entry.summary != null && entry.summary.toLowerCase().contains(keyword));
    }

    @FXML
    public void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Scene scene = new Scene(loader.load());
            PlaymodeController controller = loader.getController();
            controller.setUsername(username);

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Failed to return to play mode: " + e.getMessage());
        }
    }

    private void setupHoverEffects() {
        setupHoverEffect(searchButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
    }

    private void setupHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
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

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getDialogPane().setStyle("-fx-background-color: #eadcc7;");
            alert.showAndWait();
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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
