package eoc.ui;

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
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    public void initializeGame(String mode, String leaderName) {
        this.mode = mode;
        loadHistory();

        if (allLeaders == null || allLeaders.isEmpty()) {
            showErrorAlert("No leaders loaded. Cannot start game.");
            return;
        }

        if (mode.equals("SINGLE")) {
            this.currentLeader = findLeaderByName(leaderName);
            if (currentLeader == null) {
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
        }

        if (currentLevels.isEmpty()) {
            showErrorAlert("No levels available for the selected mode.");
            return;
        }

        // Initialize progress bar
        progressBar.setProgress(0.0);
        System.out.println("Initialized game with " + currentLevels.size() + " levels, correctCount: " + correctCount);

        showLevel();
    }

    private void loadHistory() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("history.json")) {
            if (input == null) {
                System.err.println("❌ history.json not found in resources.");
                showErrorAlert("Failed to load game data.");
                return;
            }
            allLeaders = mapper.readValue(input, new TypeReference<List<Leader>>() {});
            System.out.println("Loaded " + allLeaders.size() + " leaders");
            for (Leader leader : allLeaders) {
                System.out.println("Leader: " + leader.getName() + ", Levels: " + leader.getLevels().size());
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load history.json: " + e.getMessage());
            showErrorAlert("Failed to load game data.");
        }
    }

    private Leader findLeaderByName(String name) {
        return allLeaders.stream().filter(l -> l.getName().equals(name)).findFirst().orElse(null);
    }

    private void showLevel() {
        if (currentIndex >= currentLevels.size()) {
            goToEndScreen();
            return;
        }

        Level level = currentLevels.get(currentIndex);
        if (level == null || level.getDescription() == null) {
            System.err.println("❌ Level or description is null at index " + currentIndex);
            showErrorAlert("Failed to load level data.");
            return;
        }

        // Format description with newlines for wrapping
        String formattedDescription = wrapText(level.getDescription(), 50);
        descriptionArea.setText(formattedDescription);
        System.out.println("Displaying level " + currentIndex + ": " + formattedDescription); // Debug

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
                System.err.println("❌ Failed to load image: " + path + " - " + e.getMessage());
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

        System.out.println("Choice " + choiceIndex + " selected: " + choice.getText() + ", isHistorical: " + correct); // Debug

        if (correct) {
            clickedButton.setStyle("-fx-background-color: green;");
            correctCount++;
        } else {
            clickedButton.setStyle("-fx-background-color: red;");
        }

        choiceOneButton.setDisable(true);
        choiceTwoButton.setDisable(true);

        // Update progress bar immediately
        double progress = (double) correctCount / currentLevels.size();
        progressBar.setProgress(progress);
        System.out.println("Updated progress: correctCount=" + correctCount + ", totalLevels=" + currentLevels.size() + ", progress=" + progress); // Debug

        currentIndex++; // Increment to move to next level
        System.out.println("Advancing to next level, new index: " + currentIndex); // Debug

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> showLevel());
        pause.play();
    }

    @FXML
    public void onChoiceOneBC(ActionEvent event) {
        handleChoice(0, choiceOneButton);
    }

    @FXML
    public void onChoiceTwoBC(ActionEvent event) {
        handleChoice(1, choiceTwoButton);
    }

    private void goToEndScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/EndRound.fxml"));
            Scene scene = new Scene(loader.load());
            EndRoundController controller = loader.getController();
            controller.setScore("Score: " + correctCount + " / " + currentLevels.size());

            Stage stage = (Stage) descriptionArea.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("❌ Failed to load EndRound.fxml: " + e.getMessage());
            showErrorAlert("Failed to load end screen. Please try again.");
        }
    }

    @FXML
    public void onBackButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/eoc/ui/Playmode.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode.fxml: " + e.getMessage());
            showErrorAlert("Failed to return to play mode selection. Please try again.");
        }
    }

    private void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}