package eoc.ui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eoc.ui.model.Leader;
import eoc.ui.model.Choice;
import eoc.ui.model.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.fxml.FXMLLoader;
import javafx.animation.PauseTransition;
import javafx.scene.media.AudioClip;
import java.time.Duration;
import java.time.Instant;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class LevelController {

    @FXML private TextArea descriptionArea;
    @FXML private Button choiceOneButton;
    @FXML private Button choiceTwoButton;
    @FXML private ImageView leaderPortrait;
    @FXML private ProgressBar progressBar;

    private List<Leader> allLeaders;
    private List<Level> currentLevels;
    private Leader currentLeader;
    private int currentIndex = 0;
    private int correctCount = 0;
    private String mode;
    private String username;
    private Instant startTime;
    private List<Map<String, Object>> archives;
    private static final String ARCHIVE_JSON_PATH = "archive.json";
    private static final Path ARCHIVE_FILE_PATH = Paths.get("Echoes_of_Command", ARCHIVE_JSON_PATH);
    private static final Path PLAYERS_FILE_PATH = Paths.get("Echoes_of_Command", "players.json");
    private static final Path STATS_FILE_PATH = Paths.get("Echoes_of_Command", "stats.json");

    public void initializeGame(String mode, String leaderName, String username) {
        this.mode = mode;
        this.username = username != null ? username.toLowerCase() : "unknown"; // Ensure lowercase
        this.startTime = Instant.now();
        this.archives = new ArrayList<>();
        this.correctCount = 0; // Reset correct count
        this.currentIndex = 0; // Reset index
        loadHistory();

        System.out.println("Initializing game: mode=" + mode + ", leader=" + leaderName + ", username=" + this.username);

        if (allLeaders == null || allLeaders.isEmpty()) {
            showErrorAlert("No leaders loaded. Cannot start game.");
            return;
        }

        if (mode.equals("SINGLE")) {
            this.currentLeader = findLeaderByName(leaderName);
            if (this.currentLeader == null) {
                showErrorAlert("Selected leader not found: " + leaderName);
                return;
            }
            this.currentLevels = new ArrayList<>(currentLeader.getLevels());
        } else if (mode.equals("SEQUENTIAL")) {
            this.currentLevels = new ArrayList<>();
            for (Leader l : allLeaders) currentLevels.addAll(l.getLevels());
        } else if (mode.equals("RANDOM")) {
            this.currentLevels = new ArrayList<>();
            for (Leader l : allLeaders) currentLevels.addAll(l.getLevels());
            Collections.shuffle(currentLevels);
        } else {
            showErrorAlert("Invalid mode: " + mode);
            return;
        }

        if (currentLevels.isEmpty()) {
            showErrorAlert("No levels available for the selected mode.");
            return;
        }

        progressBar.setProgress(0.0);
        System.out.println("Initialized game with " + currentLevels.size() + " levels, username: " + this.username);

        showLevel();
    }

    private void loadHistory() {
        ObjectMapper mapper = new ObjectMapper();
        String historyFilePath = LanguageManager.getInstance().getHistoryFilePath();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(historyFilePath)) {
            if (input == null) {
                System.err.println("❌ " + historyFilePath + " not found in resources.");
                showErrorAlert("Failed to load game data.");
                return;
            }
            allLeaders = mapper.readValue(input, new TypeReference<List<Leader>>() {});
            System.out.println("Loaded " + allLeaders.size() + " leaders from " + historyFilePath);
        } catch (IOException e) {
            System.err.println("❌ Failed to load " + historyFilePath + ": " + e.getMessage());
            showErrorAlert("Failed to load game data.");
        }
    }

    private Leader findLeaderByName(String name) {
        return allLeaders.stream().filter(l -> l.getName().equals(name)).findFirst().orElse(null);
    }

    private void showLevel() {
        if (currentIndex >= currentLevels.size()) {
            try {
                saveArchives();
                updatePlayerStats();
                updateStats();
                System.out.println("Game ended: mode=" + mode + ", username=" + username + ", score=" + correctCount);
            } catch (Exception e) {
                System.err.println("❌ Failed to save data: " + e.getMessage());
                showErrorAlert("Error saving game data. Proceeding to end screen.");
            }
            goToEndScreen();
            return;
        }

        Level level = currentLevels.get(currentIndex);
        if (level == null || level.getDescription() == null) {
            System.err.println("❌ Level or description is null at index " + currentIndex);
            showErrorAlert("Failed to load level data.");
            return;
        }

        descriptionArea.setText(wrapText(level.getDescription(), 50));
        System.out.println("Displaying level " + currentIndex + " for mode=" + mode + ", username=" + username);

        Choice choice1 = level.getChoices().get(0);
        Choice choice2 = level.getChoices().get(1);

        choiceOneButton.setText(choice1.getText());
        choiceTwoButton.setText(choice2.getText());

        resetButton(choiceOneButton);
        resetButton(choiceTwoButton);

        updatePortrait();
    }

    private String wrapText(String text, int maxLineLength) {
        if (text == null) return "";
        StringBuilder wrapped = new StringBuilder();
        String[] words = text.split(" ");
        int currentLineLength = 0;

        for (String word : words) {
            if (currentLineLength + word.length() + 1 > maxLineLength) {
                wrapped.append("\n");
                currentLineLength = 0;
            }
            wrapped.append(word).append(" ");
            currentLineLength += word.length() + 1;
        }
        return wrapped.toString().trim();
    }

    private void resetButton(Button button) {
        button.setDisable(false);
        button.setStyle("-fx-background-color: #fff3c6;");
    }

    private void updatePortrait() {
        Leader effectiveLeader = (mode.equals("SINGLE")) ? currentLeader : findLeaderOf(currentLevels.get(currentIndex));
        if (effectiveLeader == null) {
            System.err.println("❌ No leader found for current level");
            return;
        }
        String path = switch (effectiveLeader.getName()) {
            case "Joseph Stalin" -> "/eoc/ui/joe.png";
            case "Winston Churchill" -> "/eoc/ui/winston.png";
            case "Charles de Gaulle" -> "/eoc/ui/charles.png";
            case "Franklin D. Roosevelt" -> "/eoc/ui/frank.png";
            default -> null;
        };
        if (path != null) {
            try {
                leaderPortrait.setImage(new Image(getClass().getResource(path).toExternalForm()));
            } catch (Exception e) {
                System.err.println("❌ Failed to load image: " + path);
            }
        }
    }

    private Leader findLeaderOf(Level level) {
        for (Leader l : allLeaders) {
            if (l.getLevels().contains(level)) return l;
        }
        return null;
    }

    private void handleChoice(int choiceIndex, Button clickedButton) {
        Level level = currentLevels.get(currentIndex);
        Choice choice = level.getChoices().get(choiceIndex);
        boolean correct = choice.isHistorical();

        System.out.println("Choice " + choiceIndex + " selected: " + choice.getText() + ", isHistorical: " + correct + ", username: " + username + ", mode: " + mode);

        Leader effectiveLeader = (mode.equals("SINGLE")) ? currentLeader : findLeaderOf(level);
        String leaderName = effectiveLeader != null ? effectiveLeader.getName() : "Unknown";
        Map<String, Object> archive = new HashMap<>();
        archive.put("username", username);
        archive.put("leader", leaderName);
        archive.put("levelNumber", level.getNumber());
        archive.put("description", level.getDescription());
        archive.put("historicalChoice", level.getChoices().stream().filter(Choice::isHistorical).findFirst().map(Choice::getText).orElse(""));
        archive.put("summary", level.getSummary());
        archive.put("playerChoice", choice.getText());
        archive.put("isCorrect", correct);
        archives.add(archive);

        if (correct) {
            clickedButton.setStyle("-fx-background-color: green;");
            correctCount++;
        } else {
            clickedButton.setStyle("-fx-background-color: red;");
        }

        choiceOneButton.setDisable(true);
        choiceTwoButton.setDisable(true);

        double progress = (double) correctCount / currentLevels.size();
        progressBar.setProgress(progress);
        System.out.println("Progress: correctCount=" + correctCount + "/" + currentLevels.size() + ", mode=" + mode);

        currentIndex++;
        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(1));
        pause.setOnFinished(event -> showLevel());
        pause.play();
    }

    @FXML
    private void onChoiceOneBC(ActionEvent event) {
        handleChoice(0, choiceOneButton);
    }

    @FXML
    private void onChoiceTwoBC(ActionEvent event) {
        handleChoice(1, choiceTwoButton);
    }

    private String getElapsedTime() {
        Duration duration = Duration.between(startTime, Instant.now());
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void saveArchives() {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> allArchives = new ArrayList<>();

        System.out.println("Saving archives: username=" + username + ", mode=" + mode + ", new entries=" + archives.size());

        // Load existing archives
        try {
            if (Files.exists(ARCHIVE_FILE_PATH)) {
                try (InputStream input = Files.newInputStream(ARCHIVE_FILE_PATH)) {
                    allArchives = mapper.readValue(input, new TypeReference<List<Map<String, Object>>>() {});
                    System.out.println("Loaded " + allArchives.size() + " existing archive entries");
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load archive.json: " + e.getMessage());
        }

        // Add new archives, avoiding duplicates
        Set<String> existingKeys = allArchives.stream()
                .filter(entry -> entry.get("username") != null && entry.get("leader") != null && entry.get("levelNumber") != null)
                .map(entry -> String.format("%s:%s:%d",
                        entry.get("username").toString().toLowerCase(),
                        entry.get("leader").toString(),
                        ((Number) entry.get("levelNumber")).intValue()))
                .collect(Collectors.toSet());

        for (Map<String, Object> archive : archives) {
            if (archive.get("username") == null || archive.get("leader") == null || archive.get("levelNumber") == null) {
                System.err.println("⚠️ Skipping invalid archive entry: " + archive);
                continue;
            }
            String key = String.format("%s:%s:%d",
                    archive.get("username").toString().toLowerCase(),
                    archive.get("leader").toString(),
                    ((Number) archive.get("levelNumber")).intValue());
            if (!existingKeys.contains(key)) {
                allArchives.add(archive);
                existingKeys.add(key);
                System.out.println("Added archive entry: " + key);
            } else {
                System.out.println("Skipped duplicate archive entry: " + key);
            }
        }

        // Save updated archives
        try {
            Path dir = ARCHIVE_FILE_PATH.getParent();
            if (dir != null) Files.createDirectories(dir);
            try (OutputStream output = Files.newOutputStream(ARCHIVE_FILE_PATH)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(output, allArchives);
                System.out.println("Saved " + allArchives.size() + " total archive entries");
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save archive.json: " + e.getMessage());
        }
    }

    private void updatePlayerStats() {
        ObjectMapper mapper = new ObjectMapper();
        List<PlayerStats> players = loadPlayers();
        String elapsedTime = getElapsedTime();
        int score = correctCount;

        System.out.println("Updating player stats: username=" + username + ", mode=" + mode + ", score=" + score + ", time=" + elapsedTime);

        PlayerStats currentPlayer = players.stream()
                .filter(p -> p.username != null && p.username.toLowerCase().equals(username))
                .findFirst()
                .orElseGet(() -> {
                    PlayerStats newPlayer = new PlayerStats();
                    newPlayer.username = username;
                    players.add(newPlayer);
                    System.out.println("Created new player entry for username=" + username);
                    return newPlayer;
                });

        String scoreKey = switch (mode) {
            case "RANDOM" -> "bestScoreRandom";
            case "SEQUENTIAL" -> "bestScoreSequential";
            case "SINGLE" -> "bestScoreSingle";
            default -> {
                System.err.println("❌ Invalid mode: " + mode);
                yield null;
            }
        };
        String timeKey = switch (mode) {
            case "RANDOM" -> "bestTimeRandom";
            case "SEQUENTIAL" -> "bestTimeSequential";
            case "SINGLE" -> "bestTimeSingle";
            default -> {
                System.err.println("❌ Invalid mode: " + mode);
                yield null;
            }
        };

        if (scoreKey == null || timeKey == null) {
            System.err.println("❌ Skipping player stats update due to invalid mode: " + mode);
            return;
        }

        // Update best score and time
        int currentBestScore = switch (scoreKey) {
            case "bestScoreSingle" -> currentPlayer.bestScoreSingle;
            case "bestScoreSequential" -> currentPlayer.bestScoreSequential;
            case "bestScoreRandom" -> currentPlayer.bestScoreRandom;
            default -> 0;
        };
        String currentBestTime = switch (timeKey) {
            case "bestTimeSingle" -> currentPlayer.bestTimeSingle != null ? currentPlayer.bestTimeSingle : "99:99";
            case "bestTimeSequential" -> currentPlayer.bestTimeSequential != null ? currentPlayer.bestTimeSequential : "99:99";
            case "bestTimeRandom" -> currentPlayer.bestTimeRandom != null ? currentPlayer.bestTimeRandom : "99:99";
            default -> "99:99";
        };

        boolean updated = false;
        if (score > currentBestScore) {
            switch (scoreKey) {
                case "bestScoreSingle" -> currentPlayer.bestScoreSingle = score;
                case "bestScoreSequential" -> currentPlayer.bestScoreSequential = score;
                case "bestScoreRandom" -> currentPlayer.bestScoreRandom = score;
            }
            switch (timeKey) {
                case "bestTimeSingle" -> currentPlayer.bestTimeSingle = elapsedTime;
                case "bestTimeSequential" -> currentPlayer.bestTimeSequential = elapsedTime;
                case "bestTimeRandom" -> currentPlayer.bestTimeRandom = elapsedTime;
            }
            updated = true;
            System.out.println("Updated " + scoreKey + "=" + score + ", " + timeKey + "=" + elapsedTime);
        } else if (score == currentBestScore && compareTimes(elapsedTime, currentBestTime) < 0) {
            switch (timeKey) {
                case "bestTimeSingle" -> currentPlayer.bestTimeSingle = elapsedTime;
                case "bestTimeSequential" -> currentPlayer.bestTimeSequential = elapsedTime;
                case "bestTimeRandom" -> currentPlayer.bestTimeRandom = elapsedTime;
            }
            updated = true;
            System.out.println("Updated " + timeKey + "=" + elapsedTime + " (same score, better time)");
        }

        if (updated) {
            try {
                Path dir = PLAYERS_FILE_PATH.getParent();
                if (dir != null) Files.createDirectories(dir);
                try (OutputStream output = Files.newOutputStream(PLAYERS_FILE_PATH)) {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(output, players);
                    System.out.println("Saved player stats for username=" + username + ", mode=" + mode);
                }
            } catch (IOException e) {
                System.err.println("❌ Failed to save players.json: " + e.getMessage());
            }
        } else {
            System.out.println("No update needed for username=" + username + ", mode=" + mode);
        }
    }

    private void updateStats() {
        ObjectMapper mapper = new ObjectMapper();
        List<StatsData> stats = loadStats();
        Duration duration = Duration.between(startTime, Instant.now());
        double elapsedTimeSeconds = duration.toMillis() / 1000.0;

        System.out.println("Updating stats: username=" + username + ", mode=" + mode + ", levels=" + currentLevels.size() + ", correct=" + correctCount);

        StatsData currentStats = stats.stream()
                .filter(s -> s.username != null && s.username.toLowerCase().equals(username))
                .findFirst()
                .orElseGet(() -> {
                    StatsData newStats = new StatsData();
                    newStats.username = username;
                    stats.add(newStats);
                    return newStats;
                });

        int previousLevels = currentStats.totalLevelsPlayed;
        double previousTotalTime = currentStats.averageTime * previousLevels;
        currentStats.totalLevelsPlayed += currentLevels.size();
        currentStats.totalCorrectChoices += correctCount;
        currentStats.averageTime = previousLevels > 0 ?
                (previousTotalTime + elapsedTimeSeconds) / currentStats.totalLevelsPlayed :
                elapsedTimeSeconds / currentStats.totalLevelsPlayed;

        try {
            Path dir = STATS_FILE_PATH.getParent();
            if (dir != null) Files.createDirectories(dir);
            try (OutputStream output = Files.newOutputStream(STATS_FILE_PATH)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(output, stats);
                System.out.println("Saved stats for username=" + username);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save stats.json: " + e.getMessage());
        }
    }

    private List<StatsData> loadStats() {
        ObjectMapper mapper = new ObjectMapper();
        List<StatsData> stats = new ArrayList<>();
        try {
            if (Files.exists(STATS_FILE_PATH)) {
                try (InputStream input = Files.newInputStream(STATS_FILE_PATH)) {
                    stats = mapper.readValue(input, new TypeReference<List<StatsData>>() {});
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load stats.json: " + e.getMessage());
        }
        return stats;
    }

    private List<PlayerStats> loadPlayers() {
        ObjectMapper mapper = new ObjectMapper();
        List<PlayerStats> players = new ArrayList<>();
        try {
            if (Files.exists(PLAYERS_FILE_PATH)) {
                try (InputStream input = Files.newInputStream(PLAYERS_FILE_PATH)) {
                    players = mapper.readValue(input, new TypeReference<List<PlayerStats>>() {});
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load players.json: " + e.getMessage());
        }
        return players;
    }

    private int compareTimes(String time1, String time2) {
        try {
            String[] parts1 = time1.split(":");
            String[] parts2 = time2.split(":");
            int minutes1 = Integer.parseInt(parts1[0]);
            int seconds1 = Integer.parseInt(parts1[1]);
            int minutes2 = Integer.parseInt(parts2[0]);
            int seconds2 = Integer.parseInt(parts2[1]);
            return (minutes1 * 60 + seconds1) - (minutes2 * 60 + seconds2);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to compare times: " + time1 + " vs " + time2);
            return 0;
        }
    }

    private void goToEndScreen() {
        try {
            String soundPath = getClass().getResource("/eoc/ui/endround.wav").toExternalForm();
            if (soundPath != null) {
                AudioClip endSound = new AudioClip(soundPath);
                endSound.play();
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to play end round sound");
        }

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/EndRound.fxml"));
                Scene scene = new Scene(loader.load());
                EndRoundController controller = loader.getController();
                controller.setScore("Score: " + correctCount + " / " + currentLevels.size());
                controller.setTime("Time: " + getElapsedTime());

                Stage mainStage = (Stage) descriptionArea.getScene().getWindow();
                controller.setMainStage(mainStage);

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initOwner(mainStage);
                dialogStage.setTitle("Round Ended");
                dialogStage.setScene(scene);
                dialogStage.setResizable(false);
                dialogStage.showAndWait();
            } catch (IOException e) {
                System.err.println("❌ Failed to load EndRound.fxml: " + e.getMessage());
                showErrorAlert("Failed to load end screen.");
            }
        });
    }

    @FXML
    private void onBackButtonClick(ActionEvent event) {
        try {
            saveArchives();
            updatePlayerStats();
            updateStats();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Scene scene = new Scene(loader.load());
            PlaymodeController controller = loader.getController();
            controller.setUsername(username);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            System.out.println("Returned to Playmode, username=" + username);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode.fxml: " + e.getMessage());
            showErrorAlert("Failed to return to play mode.");
        }
    }

    private void showErrorAlert(String message) {
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
    public static class PlayerStats {
        public String username;
        public String lastLogin;
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
        public double averageTime;
    }
}