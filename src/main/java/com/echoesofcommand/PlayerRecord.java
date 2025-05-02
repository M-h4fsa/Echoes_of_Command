package com.echoesofcommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's record, including scores, statistics, and login history.
 */
public class PlayerRecord {
    private String username;
    private String password;
    private int bestSingleScore;
    private long bestSingleTimeMillis;
    private int bestSequentialScore;
    private long bestSequentialTimeMillis;
    private int bestRandomizedScore;
    private long bestRandomizedTimeMillis;
    private List<Long> loginHistory;
    private int totalLevelsPlayed;
    private int totalCorrectChoices;
    private long totalTimeMillis;

    /**
     * Creates a new player record.
     * @param username The player's username.
     * @param password The player's password.
     */
    public PlayerRecord(String username, String password) {
        this.username = username;
        this.password = password;
        this.bestSingleScore = 0;
        this.bestSingleTimeMillis = 0;
        this.bestSequentialScore = 0;
        this.bestSequentialTimeMillis = 0;
        this.bestRandomizedScore = 0;
        this.bestRandomizedTimeMillis = 0;
        this.loginHistory = new ArrayList<>();
        this.totalLevelsPlayed = 0;
        this.totalCorrectChoices = 0;
        this.totalTimeMillis = 0;
    }

    /**
     * Gets the username.
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password.
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the best single-leader score.
     * @return The best score.
     */
    public int getBestSingleScore() {
        return bestSingleScore;
    }

    /**
     * Gets the best single-leader time.
     * @return The best time in milliseconds.
     */
    public long getBestSingleTimeMillis() {
        return bestSingleTimeMillis;
    }

    /**
     * Gets the best sequential score.
     * @return The best score.
     */
    public int getBestSequentialScore() {
        return bestSequentialScore;
    }

    /**
     * Gets the best sequential time.
     * @return The best time in milliseconds.
     */
    public long getBestSequentialTimeMillis() {
        return bestSequentialTimeMillis;
    }

    /**
     * Gets the best randomized score.
     * @return The best score.
     */
    public int getBestRandomizedScore() {
        return bestRandomizedScore;
    }

    /**
     * Gets the best randomized time.
     * @return The best time in milliseconds.
     */
    public long getBestRandomizedTimeMillis() {
        return bestRandomizedTimeMillis;
    }

    /**
     * Gets the login history.
     * @return The list of login timestamps.
     */
    public List<Long> getLoginHistory() {
        return new ArrayList<>(loginHistory);
    }

    /**
     * Gets the last login timestamp.
     * @return The last login timestamp, or null if none.
     */
    public Long getLastLogin() {
        return loginHistory.isEmpty() ? null : loginHistory.get(loginHistory.size() - 1);
    }

    /**
     * Adds a login timestamp.
     * @param timestamp The timestamp to add.
     */
    public void addLogin(long timestamp) {
        loginHistory.add(timestamp);
    }

    /**
     * Records a game session's score and time.
     * @param score The score achieved.
     * @param timeMillis The time taken in milliseconds.
     * @param sequential True if sequential mode.
     * @param randomized True if randomized mode.
     */
    public void recordSession(int score, long timeMillis, boolean sequential, boolean randomized) {
        if (randomized) {
            if (score > bestRandomizedScore || (score == bestRandomizedScore && timeMillis < bestRandomizedTimeMillis)) {
                bestRandomizedScore = score;
                bestRandomizedTimeMillis = timeMillis;
            }
        } else if (sequential) {
            if (score > bestSequentialScore || (score == bestSequentialScore && timeMillis < bestSequentialTimeMillis)) {
                bestSequentialScore = score;
                bestSequentialTimeMillis = timeMillis;
            }
        } else {
            if (score > bestSingleScore || (score == bestSingleScore && timeMillis < bestSingleTimeMillis)) {
                bestSingleScore = score;
                bestSingleTimeMillis = timeMillis;
            }
        }
    }

    /**
     * Updates the player's statistics.
     * @param levelsPlayed The number of levels played.
     * @param correctChoices The number of correct choices.
     * @param timeMillis The time taken in milliseconds.
     */
    public void updateStatistics(int levelsPlayed, int correctChoices, long timeMillis) {
        this.totalLevelsPlayed += levelsPlayed;
        this.totalCorrectChoices += correctChoices;
        this.totalTimeMillis += timeMillis;
    }

    /**
     * Gets the total levels played.
     * @return The total levels played.
     */
    public int getTotalLevelsPlayed() {
        return totalLevelsPlayed;
    }

    /**
     * Gets the player's accuracy.
     * @return The accuracy as a percentage.
     */
    public double getAccuracy() {
        return totalLevelsPlayed == 0 ? 0 : (double) totalCorrectChoices / totalLevelsPlayed * 100;
    }

    /**
     * Gets the average time per level.
     * @return The average time in seconds.
     */
    public double getAverageTimePerLevel() {
        return totalLevelsPlayed == 0 ? 0 : (double) totalTimeMillis / totalLevelsPlayed / 1000;
    }
}