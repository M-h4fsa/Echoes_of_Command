package com.echoesofcommand;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the game archive, storing and searching level entries.
 */
public class ArchiveManager {
    private List<ArchiveEntry> archive = new ArrayList<>();
    private static final String ARCHIVE_FILE = "archive.json";

    /**
     * Adds a new entry to the archive with the player's choice and correctness.
     * @param leader The leader's name.
     * @param level The level to archive.
     * @param playerChoice The player's chosen option (text of the choice).
     * @param isCorrect Whether the player's choice was correct.
     */
    public void addEntry(String leader, Level level, String playerChoice, boolean isCorrect) {
        String histChoice = level.getChoices().stream()
                .filter(Choice::isHistorical)
                .findFirst()
                .map(Choice::getText)
                .orElse("");
        archive.add(new ArchiveEntry(leader, level.getNumber(), level.getDescription(), histChoice, level.getSummary(), playerChoice, isCorrect));
    }

    /**
     * Saves the archive to a JSON file.
     */
    public void saveToJson() {
        try (Writer writer = new FileWriter(ARCHIVE_FILE)) {
            new Gson().toJson(archive, writer);
        } catch (IOException e) {
            System.err.println("Warning: Failed to save archive: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to search the archive using the provided UI.
     * @param ui The GameUI instance for user interaction.
     */
    public void promptSearch(GameUI ui) {
        if (archive.isEmpty()) {
            System.out.println("[Your archive is empty. Complete levels to build your archive!]");
            return;
        }
        if (!ui.promptArchiveSearch()) {
            return;
        }
        String keyword = ui.promptSearchKeyword().toLowerCase();
        List<ArchiveEntry> results = archive.stream()
                .filter(e -> e.description.toLowerCase().contains(keyword)
                        || e.leader.toLowerCase().contains(keyword)
                        || e.summary.toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            System.out.println("[No results found. Try a different keyword or play more levels.]");
            return;
        }
        System.out.println("\n=== Archive Search Results ===");
        for (ArchiveEntry entry : results) {
            System.out.println("Leader: " + entry.leader);
            System.out.println("Level " + entry.levelNumber + ": " + entry.description);
            System.out.println("Your Choice: " + (entry.playerChoice.isEmpty() ? "Skipped" : entry.playerChoice));
            System.out.println("Result: " + (entry.playerChoice.isEmpty() ? "Skipped" : (entry.isCorrect ? "Correct ✔️" : "Incorrect ❌")));
            System.out.println("Historical Decision: " + entry.historicalChoice);
            System.out.println("Summary: " + entry.summary + "\n");
        }
    }

    /**
     * Represents a single entry in the archive.
     */
    static class ArchiveEntry {
        String leader;
        int levelNumber;
        String description;
        String historicalChoice;
        String summary;
        String playerChoice;
        boolean isCorrect;

        /**
         * Creates a new archive entry.
         * @param leader The leader's name.
         * @param levelNumber The level number.
         * @param description The level description.
         * @param historicalChoice The historical choice made.
         * @param summary The level summary.
         * @param playerChoice The player's chosen option.
         * @param isCorrect Whether the player's choice was correct.
         */
        public ArchiveEntry(String leader, int levelNumber, String description, String historicalChoice, String summary, String playerChoice, boolean isCorrect) {
            this.leader = leader;
            this.levelNumber = levelNumber;
            this.description = description;
            this.historicalChoice = historicalChoice;
            this.summary = summary;
            this.playerChoice = playerChoice != null ? playerChoice : "";
            this.isCorrect = isCorrect;
        }
    }
}