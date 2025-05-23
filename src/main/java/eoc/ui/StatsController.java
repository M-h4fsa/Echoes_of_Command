package eoc.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StatsController {
    @FXML
    private Button backButton;

    @FXML
    private TextFlow avgTimeFlow;

    @FXML
    private TextFlow totalLevelsFlow;

    @FXML
    private TextFlow progressFlow;

    @FXML
    private TextArea historyTextArea;

    private final File statsFile = new File("C:\\Users\\DELL\\Desktop\\Echoes_of_Command\\Echoes_of_Command\\stats.json");

    public void initialize() {
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
        loadStats();
    }

    private void loadStats() {
        try {
            StatsData stats;

            if (!statsFile.exists()) {
                stats = new StatsData();
                saveStats(stats);
            } else {
                ObjectMapper mapper = new ObjectMapper();
                stats = mapper.readValue(statsFile, StatsData.class);
            }

            // 🎯 Styled bold, larger text
            avgTimeFlow.getChildren().setAll(makeStyledText(formatAvgTime(stats)));
            totalLevelsFlow.getChildren().setAll(makeStyledText(String.valueOf(stats.totalLevelsPlayed)));
            progressFlow.getChildren().setAll(makeStyledText(formatProgress(stats)));

            // 📝 History display
            if (stats.leadersPlayed.isEmpty()) {
                historyTextArea.setText("No leaders played yet.");
            } else {
                historyTextArea.setText("Leaders played:\n" + String.join(", ", stats.leadersPlayed));
            }

        } catch (IOException e) {
            historyTextArea.setText("Error loading stats: " + e.getMessage());
        }
    }

    private Text makeStyledText(String value) {
        Text text = new Text(value);
        text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return text;
    }


    private void saveStats(StatsData stats) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(statsFile, stats);
    }

    private String formatAvgTime(StatsData stats) {
        if (stats.totalLevelsPlayed == 0 || stats.totalTimeMillis == 0) return "0.00 s";
        double avg = stats.totalTimeMillis / (double) stats.totalLevelsPlayed / 1000.0;
        return String.format("%.2f s", avg);
    }

    private String formatProgress(StatsData stats) {
        if (stats.totalLevelsPlayed == 0) return "0.00%";
        double accuracy = (stats.totalCorrectChoices / (double) stats.totalLevelsPlayed) * 100.0;
        return String.format("%.2f%%", accuracy);
    }

    @FXML
    public void onBackButtonClick() {
        // Handle navigation back
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

    // === Inner class for JSON binding ===
    public static class StatsData {
        public String username = "user";
        public int totalLevelsPlayed = 0;
        public int totalCorrectChoices = 0;
        public long totalTimeMillis = 0;
        public Set<String> leadersPlayed = new HashSet<>();
    }
}
