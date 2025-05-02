package com.echoesofcommand;

import java.util.List;

/**
 * Main entry point for the Echoes of Command game.
 */
public class Main {
    private static final String LEADERS_FILE = "history.json";

    /**
     * Starts the game.
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        PlayerManager pm = new PlayerManager();
        ArchiveManager am = new ArchiveManager();
        JsonLoader loader = new JsonLoader();

        boolean running = true;
        while (running) {
            ui.displayWelcomeMessage();
            ui.offerCourseMaterial();
            // Player login
            PlayerRecord player;
            String username = ui.promptUsername();
            player = pm.login(username);
            ui.displayWelcomeForPlayer(player);
            pm.recordLogin(player); // Record login after displaying welcome message

            boolean userActive = true;
            while (userActive) {
                ui.searchDisabledNotice();
                int mode = ui.promptPlayMode();
                if (mode == 4) {
                    userActive = false;
                    running = false;
                    break;
                }

                List<Leader> allLeaders;
                try {
                    allLeaders = loader.loadLeaders(LEADERS_FILE);
                } catch (Exception e) {
                    System.err.println("Error loading leaders: " + e.getMessage());
                    System.out.println("Cannot start game without leaders. Please try again later.");
                    break;
                }

                List<Leader> toPlay;
                boolean sequential = false;
                boolean randomized = false;
                if (mode == 1) {
                    toPlay = List.of(ui.selectLeader(allLeaders));
                } else if (mode == 2) {
                    toPlay = allLeaders;
                    sequential = true;
                } else {
                    toPlay = allLeaders;
                    randomized = true;
                }

                Game game = new Game(toPlay, ui, player, am, sequential, randomized);
                game.start();

                pm.save();

                if (ui.promptArchiveSearch()) {
                    am.promptSearch(ui);
                }

                int next = ui.promptPostRoundOption();
                switch (next) {
                    case 1 -> { /* replay with same username */ }
                    case 2 -> userActive = false;
                    case 3 -> ui.displayPlayerStats(player);
                    case 4 -> {
                        userActive = false;
                        running = false;
                    }
                }
            }
            ui.displayLeaderboard(pm.leaderboard());
        }
        ui.displayGoodbyeMessage();
    }
}