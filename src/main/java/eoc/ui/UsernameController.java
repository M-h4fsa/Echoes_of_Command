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

    @FXML
    private TextField usernameField;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;
    private Stage welcomeStage;  // reference to close later
    private Stage usernameStage; // popup window
    private static final String PLAYERS_JSON_PATH = "players.json";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    private static final java.nio.file.Path PLAYERS_FILE_PATH = Paths.get("C:/Users/DELL/Desktop/Echoes_of_Command/Echoes_of_Command", PLAYERS_JSON_PATH);

    public void setWelcomeStage(Stage stage) {
        this.welcomeStage = stage;
    }

    public void setUsernameStage(Stage stage) {
        this.usernameStage = stage;
    }

    public void initialize() {
        setupHoverEffect(submitButton, "#e6d9a8", 1.05, 1.05);
        setupHoverEffect(backButton, "#3a4219", 1.05, 1.05);
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
    private void handleSubmit(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        if (username == null || username.trim().isEmpty()) {
            showAlert("Please enter a username.");
            return;
        }

        // Load existing players from players.json
        List<Map<String, String>> players = loadPlayers();
        String currentTime = Instant.now().toString();
        String lastLogin = null;

        // Check if the user already exists
        for (Map<String, String> player : players) {
            if (player.get("username").equals(username)) {
                lastLogin = player.get("lastLogin");
                player.put("lastLogin", currentTime); // Update last login time
                break;
            }
        }

        // If user doesn't exist, add them
        if (lastLogin == null) {
            Map<String, String> newPlayer = new HashMap<>();
            newPlayer.put("username", username);
            newPlayer.put("lastLogin", currentTime);
            players.add(newPlayer);
        }

        // Save updated players list back to players.json
        savePlayers(players);

        // Format welcome message
        String welcomeMessage;
        if (lastLogin != null) {
            Instant lastLoginInstant = Instant.parse(lastLogin);
            String formattedLastLogin = DATE_TIME_FORMATTER.format(lastLoginInstant);
            welcomeMessage = "Welcome back commander " + username + "!\nLast time logged in: " + formattedLastLogin;
        } else {
            welcomeMessage = "Welcome commander " + username + "! You're new!";
        }

        // Show welcome message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome");
        alert.setHeaderText(null);
        alert.setContentText(welcomeMessage);
        alert.showAndWait();

        // Load Playmode scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Playmode.fxml"));
        Scene playmodeScene = new Scene(loader.load());

        // Show in a new stage or reuse welcomeStage
        Stage playStage = new Stage();
        playStage.setTitle("Play Mode");
        playStage.setScene(playmodeScene);
        playStage.show();

        // Close both previous windows
        if (usernameStage != null) usernameStage.close();
        if (welcomeStage != null) welcomeStage.close();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (usernameStage != null) {
            usernameStage.close();
        }
    }

    private List<Map<String, String>> loadPlayers() {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> players = new ArrayList<>();
        try {
            // Try to read from the save location first
            if (Files.exists(PLAYERS_FILE_PATH)) {
                try (InputStream input = Files.newInputStream(PLAYERS_FILE_PATH)) {
                    players = mapper.readValue(input, new TypeReference<List<Map<String, String>>>() {});
                    System.out.println("Loaded players from " + PLAYERS_FILE_PATH + ": " + players);
                }
            } else {
                // If file doesn't exist in save location, check resources as a fallback
                try (InputStream input = getClass().getClassLoader().getResourceAsStream(PLAYERS_JSON_PATH)) {
                    if (input != null) {
                        players = mapper.readValue(input, new TypeReference<List<Map<String, String>>>() {});
                        System.out.println("Loaded players from resources: " + players);
                        // Save to the correct location to sync
                        savePlayers(players);
                    } else {
                        System.out.println("players.json not found in resources or save location, starting with empty list");
                        // Create an empty file in the save location
                        savePlayers(players);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to load players.json: " + e.getMessage());
            e.printStackTrace();
        }
        return players;
    }

    private void savePlayers(List<Map<String, String>> players) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Save to the specified directory
            java.nio.file.Path dir = Paths.get("C:/Users/DELL/Desktop/Echoes_of_Command/Echoes_of_Command");
            Files.createDirectories(dir); // Create directory if it doesn't exist
            java.nio.file.Path path = dir.resolve(PLAYERS_JSON_PATH);
            try (OutputStream output = Files.newOutputStream(path)) {
                mapper.writeValue(output, players);
                System.out.println("Saved players to " + path);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save players.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Username Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}