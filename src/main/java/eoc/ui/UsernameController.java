package eoc.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class UsernameController {

    @FXML private TextField usernameField;
    @FXML private Button submitButton;
    @FXML private Button backButton;

    private Stage welcomeStage;
    private Stage usernameStage;
    private static final String PLAYERS_JSON_PATH = "players.json";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    private static final java.nio.file.Path PLAYERS_FILE_PATH = Paths.get("Echoes_of_Command", PLAYERS_JSON_PATH);

    public void setWelcomeStage(Stage stage) {
        this.welcomeStage = stage;
    }

    public void setUsernameStage(Stage stage) {
        this.usernameStage = stage;
    }

    public void setWelcomeStageUsername(String username) {
        usernameField.setText(username);
    }

    public void initialize() {
        setupHoverEffect(submitButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);

        // Try to create directory if it doesn't exist
        try {
            Files.createDirectories(PLAYERS_FILE_PATH.getParent());
        } catch (IOException e) {
            System.err.println("❌ Failed to create directory: " + e.getMessage());
        }
    }

    private void setupHoverEffect(Button button, String hoverColor, double scaleX, double scaleY) {
        String originalStyle = button.getStyle() != null ? button.getStyle() : "";
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
    private void handleSubmit(ActionEvent event) throws IOException {
        onSubmitButtonClick();
    }

    @FXML
    public void onSubmitButtonClick() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            showAlert("Please enter a username.");
            return;
        }

        // Load existing players
        List<Map<String, Object>> players = loadPlayers();
        String currentTime = Instant.now().toString();
        String lastLogin = null;

        // Check if user exists
        for (Map<String, Object> player : players) {
            if (player.get("username").equals(username)) {
                lastLogin = (String) player.get("lastLogin");
                player.put("lastLogin", currentTime);
                break;
            }
        }

        // Add new user if doesn't exist
        if (lastLogin == null) {
            Map<String, Object> newPlayer = createNewPlayer(username, currentTime);
            players.add(newPlayer);
        }

        // Save updated players
        savePlayers(players);

        // Show welcome message
        showWelcomeMessage(username, lastLogin);

        // Proceed to playmode
        navigateToPlaymode(username);
    }

    private Map<String, Object> createNewPlayer(String username, String currentTime) {
        Map<String, Object> newPlayer = new HashMap<>();
        newPlayer.put("username", username);
        newPlayer.put("lastLogin", currentTime);
        newPlayer.put("bestScoreRandom", 0);
        newPlayer.put("bestTimeRandom", "00:00");
        newPlayer.put("bestScoreSequential", 0);
        newPlayer.put("bestTimeSequential", "00:00");
        newPlayer.put("bestScoreSingle", 0);
        newPlayer.put("bestTimeSingle", "00:00");
        newPlayer.put("totalLevelsPlayed", 0);
        newPlayer.put("totalCorrectChoices", 0);
        return newPlayer;
    }

    private void showWelcomeMessage(String username, String lastLogin) {
        String welcomeMessage;
        if (lastLogin != null) {
            Instant lastLoginInstant = Instant.parse(lastLogin);
            String formattedLastLogin = DATE_TIME_FORMATTER.format(lastLoginInstant);
            welcomeMessage = "Welcome back commander " + username + "!\nLast time logged in: " + formattedLastLogin;
        } else {
            welcomeMessage = "Welcome commander " + username + "! You're new!";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome");
        alert.setHeaderText(null);
        alert.setContentText(welcomeMessage);
        alert.getDialogPane().setStyle("-fx-background-color: #eadcc7;");
        alert.showAndWait();
    }

    private void navigateToPlaymode(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Playmode.fxml"));
            Stage stage = new Stage(); // Create new stage
            stage.setScene(new Scene(loader.load()));

            PlaymodeController controller = loader.getController();
            controller.setUsername(username); // Set username before showing

            stage.setTitle("Play Mode");
            stage.show();

            // Close previous windows
            if (usernameStage != null) usernameStage.close();
            if (welcomeStage != null) welcomeStage.close();

        } catch (IOException e) {
            System.err.println("❌ Failed to load Playmode.fxml: " + e.getMessage());
            showAlert("Failed to proceed to game. Please try again.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (usernameStage != null) {
            usernameStage.close();
        }
    }

    private List<Map<String, Object>> loadPlayers() {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> players = new ArrayList<>();

        try {
            if (Files.exists(PLAYERS_FILE_PATH)) {
                try (InputStream input = Files.newInputStream(PLAYERS_FILE_PATH)) {
                    players = mapper.readValue(input, new TypeReference<List<Map<String, Object>>>() {});
                }
            } else {
                try (InputStream input = getClass().getClassLoader().getResourceAsStream(PLAYERS_JSON_PATH)) {
                    if (input != null) {
                        players = mapper.readValue(input, new TypeReference<List<Map<String, Object>>>() {});
                        savePlayers(players); // Save to filesystem
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load players.json: " + e.getMessage());
        }
        return players;
    }

    private void savePlayers(List<Map<String, Object>> players) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            try (OutputStream output = Files.newOutputStream(PLAYERS_FILE_PATH)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(output, players);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save players.json: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Username Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #eadcc7;");
        alert.showAndWait();
    }
}