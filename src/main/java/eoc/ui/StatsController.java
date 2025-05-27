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
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class StatsController {
    @FXML private Button backButton;
    @FXML private TextFlow avgTimeFlow;
    @FXML private TextFlow totalLevelsFlow;
    @FXML private TextFlow progressFlow;
    @FXML private TextArea historyTextArea;

    private static final String PLAYERS_JSON_PATH = "players.json";
    private static final String ARCHIVE_JSON_PATH = "archive.json";
    private static final Path PLAYERS_FILE_PATH = Paths.get("Echoes_of_Command", PLAYERS_JSON_PATH);
    private static final Path ARCHIVE_FILE_PATH = Paths.get("Echoes_of_Command", ARCHIVE_JSON_PATH);
    private String username;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerData {
        public String username;
        public int totalLevelsPlayed;
        public int totalCorrectChoices;
        public String bestTimeSingle;
        public String bestTimeSequential;
        public String bestTimeRandom;
        public int bestScoreSingle;
        public int bestScoreSequential;
        public int bestScoreRandom;
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

    private static class LeaderStats {
        int totalLevels;
        int correctLevels;
    }

    // Initialize default player first
    private PlayerData createDefaultPlayer() {
        PlayerData player = new PlayerData();
        player.username = username;
        player.totalLevelsPlayed = 0;
        player.totalCorrectChoices = 0;
        player.bestScoreSingle = 0;
        player.bestScoreSequential = 0;
        player.bestScoreRandom = 0;
        player.bestTimeSingle = "00:00";
        player.bestTimeSequential = "00:00";
        player.bestTimeRandom = "00:00";
        return player;
    }

    public void setUsername(String username) {
        this.username = username;
        loadStats();
    }

    @FXML
    public void initialize() {
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
    }

    private void loadStats() {
        try {
            if (!Files.exists(PLAYERS_FILE_PATH) || !Files.exists(ARCHIVE_FILE_PATH)) {
                historyTextArea.setText("No game data found");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            List<PlayerData> players = mapper.readValue(
                    Files.readAllBytes(PLAYERS_FILE_PATH),
                    new TypeReference<List<PlayerData>>() {}
            );

            PlayerData playerData = players.stream()
                    .filter(p -> username.equals(p.username))
                    .findFirst()
                    .orElseGet(this::createDefaultPlayer);

            Map<String, LeaderStats> leaderStats = calculateLeaderStats();
            updateUI(playerData, leaderStats);

        } catch (IOException e) {
            showError("Failed to load stats: " + e.getMessage());
        }
    }

    private Map<String, LeaderStats> calculateLeaderStats() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<ArchiveEntry> archives = mapper.readValue(
                Files.readAllBytes(ARCHIVE_FILE_PATH),
                new TypeReference<List<ArchiveEntry>>() {}
        );

        return archives.stream()
                .filter(entry -> username.equals(entry.username))
                .collect(Collectors.groupingBy(
                        entry -> entry.leader,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                entries -> {
                                    LeaderStats stats = new LeaderStats();
                                    stats.totalLevels = entries.size();
                                    stats.correctLevels = (int) entries.stream()
                                            .filter(e -> e.isCorrect)
                                            .count();
                                    return stats;
                                }
                        )
                ));
    }

    private void updateUI(PlayerData player, Map<String, LeaderStats> leaderStats) {
        Platform.runLater(() -> {
            // Update metrics
            avgTimeFlow.getChildren().setAll(makeStyledText(String.format("%.2f s", calculateAverageTime(player))));
            totalLevelsFlow.getChildren().setAll(makeStyledText(String.valueOf(player.totalLevelsPlayed)));
            progressFlow.getChildren().setAll(makeStyledText(
                    String.format("%.2f%%", player.totalLevelsPlayed > 0 ?
                            (player.totalCorrectChoices * 100.0) / player.totalLevelsPlayed : 0)
            ));

            // Build leader performance text
            StringBuilder statsText = new StringBuilder();
            statsText.append("=== LEADER PERFORMANCE ===\n\n");

            if (leaderStats.isEmpty()) {
                statsText.append("No leaders played yet\n");
            } else {
                leaderStats.forEach((leader, stats) -> {
                    statsText.append(String.format("%s:\n", leader));
                    statsText.append(String.format("  Levels Completed: %d\n", stats.totalLevels));
                    statsText.append(String.format("  Correct Choices: %d (%.1f%%)\n\n",
                            stats.correctLevels,
                            (stats.correctLevels * 100.0) / stats.totalLevels));
                });
            }

            historyTextArea.setText(statsText.toString());
        });
    }

    private double calculateAverageTime(PlayerData player) {
        int count = 0;
        long totalMillis = 0;

        if (!"00:00".equals(player.bestTimeSingle)) {
            totalMillis += parseTime(player.bestTimeSingle);
            count++;
        }
        if (!"00:00".equals(player.bestTimeSequential)) {
            totalMillis += parseTime(player.bestTimeSequential);
            count++;
        }
        if (!"00:00".equals(player.bestTimeRandom)) {
            totalMillis += parseTime(player.bestTimeRandom);
            count++;
        }

        return count > 0 ? totalMillis / (count * 1000.0) : 0;
    }

    private long parseTime(String time) {
        try {
            String[] parts = time.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (minutes * 60L + seconds) * 1000L;
        } catch (Exception e) {
            return 0;
        }
    }

    private Text makeStyledText(String value) {
        Text text = new Text(value);
        text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return text;
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

    @FXML
    private void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            PlaymodeController controller = loader.getController();
            controller.setUsername(username);
        } catch (IOException e) {
            showError("Failed to return to play mode: " + e.getMessage());
        }
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
}