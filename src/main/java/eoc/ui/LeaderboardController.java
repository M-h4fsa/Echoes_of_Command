package eoc.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardController {
    @FXML
    private Button backButton;

    @FXML
    private TextArea leaderboardTextArea;

    private final File jsonFile = new File("C:\\Users\\DELL\\Desktop\\Echoes_of_Command\\Echoes_of_Command\\players.json");

    public void initialize() {
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
        loadLeaderboardData();
    }

    private void loadLeaderboardData() {
        if (!jsonFile.exists()) {
            leaderboardTextArea.setText("Leaderboard data not found.");
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<PlayerData> players = mapper.readValue(jsonFile, new TypeReference<>() {});

            StringBuilder display = new StringBuilder("🏆 Leaderboard\n");

            // --- Single Mode ---
            display.append("\n🔸 Single Mode (Sorted by Time, then Score):\n\n");
            players.stream()
                    .sorted(Comparator
                            .comparingLong((PlayerData p) -> p.bestSingleTimeMillis == 0 ? Long.MAX_VALUE : p.bestSingleTimeMillis)
                            .thenComparingInt((PlayerData p) -> -p.bestSingleScore))
                    .forEach(p -> appendPlayerLine(display, p.username, p.bestSingleScore, p.bestSingleTimeMillis));

            // --- Sequential Mode ---
            display.append("\n🔸 Sequential Mode (Sorted by Time, then Score):\n\n");
            players.stream()
                    .sorted(Comparator
                            .comparingLong((PlayerData p) -> p.bestSequentialTimeMillis == 0 ? Long.MAX_VALUE : p.bestSequentialTimeMillis)
                            .thenComparingInt((PlayerData p) -> -p.bestSequentialScore))
                    .forEach(p -> appendPlayerLine(display, p.username, p.bestSequentialScore, p.bestSequentialTimeMillis));

            // --- Randomized Mode ---
            display.append("\n🔸 Randomized Mode (Sorted by Time, then Score):\n\n");
            players.stream()
                    .sorted(Comparator
                            .comparingLong((PlayerData p) -> p.bestRandomizedTimeMillis == 0 ? Long.MAX_VALUE : p.bestRandomizedTimeMillis)
                            .thenComparingInt((PlayerData p) -> -p.bestRandomizedScore))
                    .forEach(p -> appendPlayerLine(display, p.username, p.bestRandomizedScore, p.bestRandomizedTimeMillis));

            leaderboardTextArea.setText(display.toString());

        } catch (IOException e) {
            leaderboardTextArea.setText("Error reading leaderboard: " + e.getMessage());
        }
    }

    private void appendPlayerLine(StringBuilder sb, String username, int score, long time) {
        if (time == 0) return; // Skip unplayed
        sb.append("User: ").append(username)
                .append(" | Score: ").append(score)
                .append(" | Time: ").append(time).append(" ms\n");
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
        // Handle back navigation
    }

    public static class PlayerData {
        public String username;
        public int bestSingleScore;
        public long bestSingleTimeMillis;
        public int bestSequentialScore;
        public long bestSequentialTimeMillis;
        public int bestRandomizedScore;
        public long bestRandomizedTimeMillis;
        public List<Long> loginHistory;
        public int totalLevelsPlayed;
        public int totalCorrectChoices;
        public long totalTimeMillis;
    }
}
