package com.echoesofcommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the game logic, including gameplay loop and scoring.
 */
public class Game {
    private final List<Leader> leaders;
    private final GameUI ui;
    private final PlayerRecord player;
    private final ArchiveManager archive;
    private final boolean sequential;
    private final boolean randomized;
    private int score;
    private long startTime;
    private int levelsPlayed;
    private int correctChoices;

    /**
     * Creates a new game instance.
     * @param leaders The list of leaders to play.
     * @param ui The user interface.
     * @param player The player's record.
     * @param archive The archive manager.
     * @param sequential Whether to play in sequential mode.
     * @param randomized Whether to play in randomized mode.
     */
    public Game(List<Leader> leaders, GameUI ui, PlayerRecord player, ArchiveManager archive, boolean sequential, boolean randomized) {
        this.leaders = leaders;
        this.ui = ui;
        this.player = player;
        this.archive = archive;
        this.sequential = sequential;
        this.randomized = randomized;
    }

    /**
     * Starts the game, running through all levels for the selected leaders.
     */
    public void start() {
        score = 0;
        startTime = System.currentTimeMillis();
        levelsPlayed = 0;
        correctChoices = 0;

        List<Level> levelsToPlay = new ArrayList<>();
        int totalLeaders = leaders.size();

        // Collect all levels
        for (Leader leader : leaders) {
            for (Level level : leader.getLevels()) {
                // Set the leader name for each level
                level = new Level(level.getNumber(), level.getDescription(), level.getChoices(), level.getSummary(), leader.getName());
                levelsToPlay.add(level);
            }
        }

        int totalLevels = levelsToPlay.size();
        int leaderCount = 0;

        // Randomize levels if in randomized mode
        if (randomized) {
            Collections.shuffle(levelsToPlay);
        }

        // Play through levels
        for (int i = 0; i < levelsToPlay.size(); i++) {
            Level level = levelsToPlay.get(i);
            if (sequential && !randomized) {
                // Find the leader index for sequential mode
                leaderCount = getLeaderIndex(levelsToPlay, i, totalLeaders);
                ui.displayLeaderSequence(level.getLeaderName(), leaderCount, totalLeaders);
            }

            // Randomize choices for randomized mode
            if (randomized) {
                level.randomizeChoices();
            }

            ui.displayLevel(level);
            int choice = ui.getPlayerChoice();
            levelsPlayed++;
            String playerChoiceText = "";
            boolean isCorrect = false;
            if (choice == 1 || choice == 2) {
                playerChoiceText = level.getChoices().get(choice - 1).getText();
                isCorrect = level.getChoices().get(choice - 1).isHistorical();
                if (isCorrect) {
                    score++;
                    correctChoices++;
                }
                ui.displayResult(isCorrect, level.getSummary());
            } else {
                ui.displayTimeoutSkip();
            }
            archive.addEntry(level.getLeaderName(), level, playerChoiceText, isCorrect);
            archive.saveToJson();
            ui.showProgress(score, totalLevels);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        ui.displayEndOfRound(score, totalLevels, elapsed);
        player.recordSession(score, elapsed, sequential, randomized);
        player.updateStatistics(levelsPlayed, correctChoices, elapsed);
    }

    /**
     * Determines the current leader index for sequential mode.
     * @param levels The list of levels.
     * @param currentIndex The current level index.
     * @param totalLeaders The total number of leaders.
     * @return The leader index (1-based).
     */
    private int getLeaderIndex(List<Level> levels, int currentIndex, int totalLeaders) {
        String currentLeader = levels.get(currentIndex).getLeaderName();
        int leaderCount = 1;
        for (int i = 0; i < currentIndex; i++) {
            if (!levels.get(i).getLeaderName().equals(currentLeader)) {
                leaderCount++;
                currentLeader = levels.get(i).getLeaderName();
            }
        }
        return leaderCount;
    }
}