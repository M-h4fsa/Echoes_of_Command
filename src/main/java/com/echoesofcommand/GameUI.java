package com.echoesofcommand;

import java.util.List;

/**
 * Defines the user interface methods for the game.
 */
public interface GameUI {
    /**
     * Displays the welcome message.
     */
    void displayWelcomeMessage();

    /**
     * Prompts the user for their username.
     * @return The entered username.
     */
    String promptUsername();

    /**
     * Displays a notice that archive search is disabled.
     */
    void searchDisabledNotice();

    /**
     * Prompts the user to select a play mode.
     * @return 1 for single-leader mode, 2 for sequential mode, 3 for randomized mode, 4 to quit.
     */
    int promptPlayMode();

    /**
     * Allows the user to select a leader.
     * @param leaders The list of available leaders.
     * @return The selected leader.
     */
    Leader selectLeader(List<Leader> leaders);

    /**
     * Displays the current leader in sequential mode.
     * @param leaderName The name of the leader.
     * @param index The current leader index.
     * @param total The total number of leaders.
     */
    void displayLeaderSequence(String leaderName, int index, int total);

    /**
     * Displays a level's details.
     * @param level The level to display.
     */
    void displayLevel(Level level);

    /**
     * Gets the player's choice for a level.
     * @return The player's choice (1 or 2).
     */
    int getPlayerChoice();

    /**
     * Displays a message when a level is skipped.
     */
    void displayTimeoutSkip();

    /**
     * Displays the result of a player's choice.
     * @param correct True if the choice was correct.
     * @param summary The level's summary.
     */
    void displayResult(boolean correct, String summary);

    /**
     * Shows the player's progress.
     * @param score The current score.
     * @param total The total number of levels.
     */
    void showProgress(int score, int total);

    /**
     * Displays the end-of-round summary.
     * @param score The final score.
     * @param total The total number of levels.
     * @param timeMillis The time taken in milliseconds.
     */
    void displayEndOfRound(int score, int total, long timeMillis);

    /**
     * Prompts the user to search the archive.
     * @return True if the user wants to search, false otherwise.
     */
    boolean promptArchiveSearch();

    /**
     * Prompts the user for a search keyword.
     * @return The keyword entered by the user.
     */
    String promptSearchKeyword();

    /**
     * Prompts the user for a post-round option.
     * @return 1 to play again, 2 to switch user, 3 to view stats, 4 to quit.
     */
    int promptPostRoundOption();

    /**
     * Displays the leaderboard.
     * @param list The list of player records.
     */
    void displayLeaderboard(List<PlayerRecord> list);

    /**
     * Displays a goodbye message.
     */
    void displayGoodbyeMessage();

    /**
     * Displays the player's statistics.
     * @param player The player's record.
     */
    void displayPlayerStats(PlayerRecord player);
}