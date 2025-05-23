package eoc.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArchiveController {
    @FXML
    private Button searchButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField searchField;

    @FXML
    private TextArea resultArea;

    private final File archiveFile = new File("C:\\Users\\DELL\\Desktop\\Echoes_of_Command\\Echoes_of_Command\\archive.json");
    private List<ArchiveEntry> allEntries;

    public void initialize() {
        setupHoverEffect(searchButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
        loadArchiveData();
    }

    private void loadArchiveData() {
        if (!archiveFile.exists()) {
            resultArea.setText("Archive data not found.");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            allEntries = mapper.readValue(archiveFile, new TypeReference<>() {});
            allEntries.sort(Comparator.comparingInt(entry -> entry.levelNumber));
            displayEntries(allEntries);

        } catch (IOException e) {
            resultArea.setText("Error loading archive: " + e.getMessage());
        }
    }

    private void displayEntries(List<ArchiveEntry> entries) {
        StringBuilder display = new StringBuilder();

        for (ArchiveEntry e : entries) {
            display.append("Level ").append(e.levelNumber).append(": ").append(e.description).append("\n")
                    .append("Leader: ").append(e.leader).append("\n")
                    .append("Player's Choice: ").append(e.playerChoice)
                    .append(" (").append(e.isCorrect ? "Correct" : "Incorrect").append(")\n")
                    .append("Historical Outcome: ").append(e.historicalChoice).append("\n")
                    .append("Summary: ").append(e.summary).append("\n")
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
        if (allEntries == null) return;

        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            displayEntries(allEntries);
            return;
        }

        List<ArchiveEntry> filtered = allEntries.stream()
                .filter(e ->
                        e.leader.toLowerCase().contains(keyword) ||
                                e.description.toLowerCase().contains(keyword) ||
                                e.historicalChoice.toLowerCase().contains(keyword) ||
                                e.playerChoice.toLowerCase().contains(keyword)
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
        // Handle back navigation
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

    public static class ArchiveEntry {
        public String leader;
        public int levelNumber;
        public String description;
        public String historicalChoice;
        public String summary;
        public String playerChoice;
        public boolean isCorrect;
    }
}
