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
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.effect.DropShadow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class LeaderboardController {
    @FXML private Button backButton;
    @FXML private TextArea leaderboardTextArea;

    private static final String PLAYERS_JSON_PATH = "players.json";
    private static final Path PLAYERS_FILE_PATH = Paths.get("Echoes_of_Command", PLAYERS_JSON_PATH);
    private String username; // Store username for navigation

    public void setUsername(String username) {
        this.username = username;
    }

    public void initialize() {
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
        loadLeaderboardData();
    }

    private void loadLeaderboardData() {
        System.out.println("Attempting to load players from: " + PLAYERS_FILE_PATH.toAbsolutePath());

        if (!Files.exists(PLAYERS_FILE_PATH)) {
            leaderboardTextArea.setText("Leaderboard data not found at " + PLAYERS_FILE_PATH);
            System.err.println("❌ Players file not found at: " + PLAYERS_FILE_PATH);
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<PlayerData> players = mapper.readValue(Files.readAllBytes(PLAYERS_FILE_PATH), new TypeReference<List<PlayerData>>() {});
            System.out.println("✅ Loaded " + players.size() + " player entries");

            // Debug: Log each player's data
            for (PlayerData p : players) {
                System.out.println("Player: " + p.username + ", Single Score: " + p.bestScoreSingle + ", Single Time: " + p.bestTimeSingle);
            }

            StringBuilder display = new StringBuilder("🏆 Leaderboard\n");

            // Single Mode
            display.append("\n🔸 Single Mode (Sorted by Score, then Time):\n\n");
            List<PlayerData> singleFiltered = players.stream()
                    .filter(p -> p.bestScoreSingle > 0 || (p.bestTimeSingle != null && !p.bestTimeSingle.equals("00:00")))
                    .sorted(Comparator
                            .comparingInt((PlayerData p) -> -p.bestScoreSingle)
                            .thenComparingLong(p -> parseTime(p.bestTimeSingle)))
                    .toList();
            System.out.println("Single Mode entries after filter: " + singleFiltered.size());
            singleFiltered.forEach(p -> appendPlayerLine(display, p.username, p.bestScoreSingle, p.bestTimeSingle));

            // Sequential Mode
            display.append("\n🔸 Sequential Mode (Sorted by Score, then Time):\n\n");
            List<PlayerData> sequentialFiltered = players.stream()
                    .filter(p -> p.bestScoreSequential > 0 || (p.bestTimeSequential != null && !p.bestTimeSequential.equals("00:00")))
                    .sorted(Comparator
                            .comparingInt((PlayerData p) -> -p.bestScoreSequential)
                            .thenComparingLong(p -> parseTime(p.bestTimeSequential)))
                    .toList();
            System.out.println("Sequential Mode entries after filter: " + sequentialFiltered.size());
            sequentialFiltered.forEach(p -> appendPlayerLine(display, p.username, p.bestScoreSequential, p.bestTimeSequential));

            // Randomized Mode
            display.append("\n🔸 Randomized Mode (Sorted by Score, then Time):\n\n");
            List<PlayerData> randomFiltered = players.stream()
                    .filter(p -> p.bestScoreRandom > 0 || (p.bestTimeRandom != null && !p.bestTimeRandom.equals("00:00")))
                    .sorted(Comparator
                            .comparingInt((PlayerData p) -> -p.bestScoreRandom)
                            .thenComparingLong(p -> parseTime(p.bestTimeRandom)))
                    .toList();
            System.out.println("Randomized Mode entries after filter: " + randomFiltered.size());
            randomFiltered.forEach(p -> appendPlayerLine(display, p.username, p.bestScoreRandom, p.bestTimeRandom));

            leaderboardTextArea.setText(display.toString());

        } catch (IOException e) {
            System.err.println("❌ Error reading players.json: " + e.getMessage());
            e.printStackTrace();
            leaderboardTextArea.setText("Error reading leaderboard: " + e.getMessage());
        }
    }

    private void appendPlayerLine(StringBuilder sb, String username, int score, String time) {
        sb.append("User: ").append(username != null ? username : "Unknown")
                .append(" | Score: ").append(score)
                .append(" | Time: ").append(time != null ? time : "N/A").append("\n");
    }

    private long parseTime(String time) {
        if (time == null || time.equals("00:00")) return Long.MAX_VALUE;
        try {
            String[] parts = time.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (minutes * 60 + seconds) * 1000L; // Convert to milliseconds for sorting
        } catch (Exception e) {
            System.err.println("⚠️ Failed to parse time: " + time);
            return Long.MAX_VALUE;
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
        public int bestScoreSingle;
        public String bestTimeSingle;
        public int bestScoreSequential;
        public String bestTimeSequential;
        public int bestScoreRandom;
        public String bestTimeRandom;
        public List<Long> loginHistory;
        public int totalLevelsPlayed;
        public int totalCorrectChoices;
        public long totalTimeMillis;
    }
}