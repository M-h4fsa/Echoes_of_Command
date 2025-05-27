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
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
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
    private static final String STATS_JSON_PATH = "stats.json";
    private static final Path PLAYERS_FILE_PATH = Paths.get("Echoes_of_Command", PLAYERS_JSON_PATH);
    private static final Path ARCHIVE_FILE_PATH = Paths.get("Echoes_of_Command", ARCHIVE_JSON_PATH);
    private static final Path STATS_FILE_PATH = Paths.get("Echoes_of_Command", STATS_JSON_PATH);
    private String username; // Store username for the current player

    public void setUsername(String username) {
        this.username = username;
    }

    public void initialize() {
        if (username == null) {
            System.err.println("⚠️ Username not set in StatsController");
            historyTextArea.setText("Error: Username not set.");
            return;
        }
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
        loadStats();
    }

    private void loadStats() {
        System.out.println("Attempting to load players from: " + PLAYERS_FILE_PATH.toAbsolutePath());
        System.out.println("Attempting to load archives from: " + ARCHIVE_FILE_PATH.toAbsolutePath());

        if (!Files.exists(PLAYERS_FILE_PATH)) {
            historyTextArea.setText("Player data not found at " + PLAYERS_FILE_PATH);
            System.err.println("❌ Players file not found at: " + PLAYERS_FILE_PATH);
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<PlayerData> players = mapper.readValue(Files.readAllBytes(PLAYERS_FILE_PATH), new TypeReference<List<PlayerData>>() {});
            System.out.println("✅ Loaded " + players.size() + " player entries");

            PlayerData currentPlayer = players.stream()
                    .filter(p -> p.username != null && p.username.equals(username))
                    .findFirst()
                    .orElseGet(() -> {
                        PlayerData newPlayer = new PlayerData();
                        newPlayer.username = username;
                        players.add(newPlayer);
                        return newPlayer;
                    });

            // Initialize missing fields with defaults if not present
            if (currentPlayer.totalLevelsPlayed == 0 && currentPlayer.totalCorrectChoices == 0) {
                // Infer from best scores if possible (temporary workaround)
                currentPlayer.totalLevelsPlayed = (currentPlayer.bestScoreSingle > 0 ? 1 : 0) +
                        (currentPlayer.bestScoreSequential > 0 ? 1 : 0) +
                        (currentPlayer.bestScoreRandom > 0 ? 1 : 0);
                currentPlayer.totalCorrectChoices = Math.min(currentPlayer.totalLevelsPlayed, currentPlayer.bestScoreSingle); // Rough estimate
            }

            // Calculate average time
            double avgTimeMillis = calculateAvgTime(currentPlayer);
            String avgTime = String.format("%.2f s", avgTimeMillis / 1000.0);

            // Set total levels played
            int totalLevels = currentPlayer.totalLevelsPlayed;

            // Calculate progress (accuracy percentage)
            double progress = (totalLevels > 0) ? (currentPlayer.totalCorrectChoices / (double) totalLevels) * 100.0 : 0.0;
            String progressStr = String.format("%.2f%%", progress);

            // Load leaders played from archive.json
            Set<String> leadersPlayed = loadLeadersPlayed();

            // Update display
            avgTimeFlow.getChildren().setAll(makeStyledText(avgTime));
            totalLevelsFlow.getChildren().setAll(makeStyledText(String.valueOf(totalLevels)));
            progressFlow.getChildren().setAll(makeStyledText(progressStr));
            historyTextArea.setText("Leaders played:\n" + (leadersPlayed.isEmpty() ? "None" : String.join(", ", leadersPlayed)));

            // Save updated stats to stats.json
            saveStats(currentPlayer, leadersPlayed);

        } catch (IOException e) {
            System.err.println("❌ Error reading player data: " + e.getMessage());
            e.printStackTrace();
            historyTextArea.setText("Error loading stats: " + e.getMessage());
        }
    }

    private double calculateAvgTime(PlayerData player) {
        long totalMillis = 0;
        int timeCount = 0;

        if (player.bestTimeSingle != null && !player.bestTimeSingle.equals("00:00")) {
            totalMillis += parseTime(player.bestTimeSingle);
            timeCount++;
        }
        if (player.bestTimeSequential != null && !player.bestTimeSequential.equals("00:00")) {
            totalMillis += parseTime(player.bestTimeSequential);
            timeCount++;
        }
        if (player.bestTimeRandom != null && !player.bestTimeRandom.equals("00:00")) {
            totalMillis += parseTime(player.bestTimeRandom);
            timeCount++;
        }

        return (timeCount > 0) ? totalMillis / (double) timeCount : 0.0;
    }

    private long parseTime(String time) {
        if (time == null || time.equals("00:00")) return 0;
        try {
            String[] parts = time.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (minutes * 60 + seconds) * 1000L;
        } catch (Exception e) {
            System.err.println("⚠️ Failed to parse time: " + time);
            return 0;
        }
    }

    private Set<String> loadLeadersPlayed() throws IOException {
        Set<String> leadersPlayed = new HashSet<>();
        if (Files.exists(ARCHIVE_FILE_PATH)) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> archives = mapper.readValue(Files.readAllBytes(ARCHIVE_FILE_PATH), new TypeReference<List<Map<String, Object>>>() {});
            leadersPlayed = archives.stream()
                    .filter(entry -> entry.get("username") != null && entry.get("username").equals(username))
                    .map(entry -> (String) entry.get("leader"))
                    .filter(leader -> leader != null)
                    .collect(Collectors.toSet());
        }
        return leadersPlayed;
    }

    private void saveStats(PlayerData player, Set<String> leadersPlayed) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<StatsData> allStats = new ArrayList<>();

        if (Files.exists(STATS_FILE_PATH)) {
            try {
                allStats = mapper.readValue(Files.readAllBytes(STATS_FILE_PATH), new TypeReference<List<StatsData>>() {});
            } catch (IOException e) {
                System.err.println("❌ Failed to load stats.json: " + e.getMessage());
            }
        }

        // Update or add the current player's stats
        List<StatsData> finalAllStats = allStats;
        StatsData statsEntry = allStats.stream()
                .filter(stat -> stat.username != null && stat.username.equals(player.username))
                .findFirst()
                .orElseGet(() -> {
                    StatsData newStat = new StatsData();
                    newStat.username = player.username;
                    finalAllStats.add(newStat);
                    return newStat;
                });

        statsEntry.totalLevelsPlayed = player.totalLevelsPlayed;
        statsEntry.totalCorrectChoices = player.totalCorrectChoices;
        statsEntry.bestTimeSingle = player.bestTimeSingle;
        statsEntry.bestTimeSequential = player.bestTimeSequential;
        statsEntry.bestTimeRandom = player.bestTimeRandom;
        statsEntry.bestScoreSingle = player.bestScoreSingle;
        statsEntry.bestScoreSequential = player.bestScoreSequential;
        statsEntry.bestScoreRandom = player.bestScoreRandom;
        statsEntry.leadersPlayed = leadersPlayed;

        Path dir = STATS_FILE_PATH.getParent();
        if (dir != null) {
            Files.createDirectories(dir);
        }
        try (OutputStream output = Files.newOutputStream(STATS_FILE_PATH)) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(output, allStats);
            System.out.println("Saved stats to " + STATS_FILE_PATH + " for user " + username);
        }
    }

    private Text makeStyledText(String value) {
        Text text = new Text(value);
        text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return text;
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
    public void onBackButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Scene scene = new Scene(loader.load());
            PlaymodeController controller = loader.getController();
            if (username != null) {
                controller.setUsername(username);
            }

            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode.fxml: " + e.getMessage());
            showErrorAlert("Failed to return to play mode. Try again.");
        }
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatsData {
        public String username;
        public int totalLevelsPlayed;
        public int totalCorrectChoices;
        public String bestTimeSingle;
        public String bestTimeSequential;
        public String bestTimeRandom;
        public int bestScoreSingle;
        public int bestScoreSequential;
        public int bestScoreRandom;
        public Set<String> leadersPlayed = new HashSet<>();
    }
}