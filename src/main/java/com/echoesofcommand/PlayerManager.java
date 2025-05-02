package com.echoesofcommand;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages player records, including login and leaderboard functionality.
 */
public class PlayerManager {
    private final Map<String, PlayerRecord> records;
    private static final String PLAYER_FILE = "players.json";

    /**
     * Creates a new PlayerManager, loading existing player records from file.
     */
    public PlayerManager() {
        records = load();
    }

    /**
     * Logs in a player, creating a new record if the username doesn't exist.
     * @param username The username to log in.
     * @return The player's record.
     */
    public PlayerRecord login(String username) {
        PlayerRecord player = records.get(username);
        if (player == null) {
            player = new PlayerRecord(username, "");
            records.put(username, player);
        }
        return player;
    }

    /**
     * Records a login timestamp for a player.
     * @param player The player to record the login for.
     */
    public void recordLogin(PlayerRecord player) {
        player.addLogin(System.currentTimeMillis());
    }

    /**
     * Saves player records to a JSON file.
     */
    public void save() {
        try (Writer writer = new FileWriter(PLAYER_FILE)) {
            new Gson().toJson(records.values(), writer);
        } catch (IOException e) {
            System.err.println("Warning: Failed to save players: " + e.getMessage());
        }
    }

    /**
     * Generates the leaderboard, sorted by scores and times.
     * @return The list of player records, sorted.
     */
    public List<PlayerRecord> leaderboard() {
        List<PlayerRecord> list = new ArrayList<>(records.values());

        // Sort by single-leader score, then time
        list.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getBestSingleScore(), a.getBestSingleScore());
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Long.compare(a.getBestSingleTimeMillis(), b.getBestSingleTimeMillis());
        });

        // Sort by sequential score, then time (in-place, after single-leader sort)
        list.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getBestSequentialScore(), a.getBestSequentialScore());
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Long.compare(a.getBestSequentialTimeMillis(), b.getBestSequentialTimeMillis());
        });

        // Sort by randomized score, then time (in-place, after sequential sort)
        list.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getBestRandomizedScore(), a.getBestRandomizedScore());
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Long.compare(a.getBestRandomizedTimeMillis(), b.getBestRandomizedTimeMillis());
        });

        return list;
    }

    /**
     * Loads player records from a JSON file.
     * @return The map of usernames to player records.
     */
    private Map<String, PlayerRecord> load() {
        File file = new File(PLAYER_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            // First, try to load as a List<PlayerRecord> (new format)
            try {
                Type listType = new TypeToken<List<PlayerRecord>>() {}.getType();
                List<PlayerRecord> list = gson.fromJson(reader, listType);
                if (list != null) {
                    return list.stream()
                            .collect(Collectors.toMap(PlayerRecord::getUsername, p -> p));
                }
            } catch (com.google.gson.JsonSyntaxException e) {
                // If that fails, try to load as a Map<String, PlayerRecord> (old format)
                reader.close();
                try (Reader reader2 = new FileReader(file)) {
                    Type mapType = new TypeToken<Map<String, PlayerRecord>>() {}.getType();
                    Map<String, PlayerRecord> map = gson.fromJson(reader2, mapType);
                    if (map != null) {
                        return map;
                    }
                }
            }
            return new HashMap<>();
        } catch (IOException e) {
            System.err.println("Warning: Failed to load players: " + e.getMessage());
            return new HashMap<>();
        }
    }
}