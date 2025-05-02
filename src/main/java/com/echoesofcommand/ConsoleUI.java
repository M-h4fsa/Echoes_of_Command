package com.echoesofcommand;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Implements the game UI using the console.
 */
public class ConsoleUI implements GameUI {
    private final Scanner sc = new Scanner(System.in);
    private static final String VALID_USERNAME_REGEX = "[a-zA-Z0-9_]+";

    @Override
    public void displayWelcomeMessage() {
        System.out.println("=== Echoes of Command ===");
    }

    @Override
    public String promptUsername() {
        while (true) {
            System.out.print("Enter your username: ");
            String username = sc.nextLine().trim().toLowerCase();
            if (username.isEmpty()) {
                System.out.println("Error: Username cannot be empty.");
            } else if (!username.matches(VALID_USERNAME_REGEX)) {
                System.out.println("Error: Username can only contain letters, numbers, or underscores.");
            } else {
                return username;
            }
        }
    }

    @Override
    public void searchDisabledNotice() {
        System.out.println("[Note] Archive-search disabled until after play.");
    }

    @Override
    public int promptPlayMode() {
        System.out.println("\nHow do you want to play?");
        System.out.println("  1) Play ONE leader");
        System.out.println("  2) Play ALL leaders in sequence");
        System.out.println("  3) Play ALL leaders with randomized levels and choices");
        System.out.println("  4) Quit");
        System.out.print("Enter choice (1–4): ");
        while (true) {
            try {
                int mode = Integer.parseInt(sc.nextLine().trim());
                if (mode >= 1 && mode <= 4) {
                    return mode;
                }
            } catch (NumberFormatException ignored) {}
            System.out.print("Invalid. Please enter 1, 2, 3, or 4: ");
        }
    }

    @Override
    public Leader selectLeader(List<Leader> leaders) {
        List<Leader> sorted = leaders.stream()
                .sorted(Comparator.comparing(Leader::getName))
                .toList();
        System.out.println("\n=== Select a Leader ===");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.printf("  %d) %s  —  %s%n",
                    i + 1,
                    sorted.get(i).getName(),
                    sorted.get(i).getBackstory()
            );
        }
        System.out.print("Enter your choice (1–" + sorted.size() + "): ");
        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine().trim()) - 1;
                if (choice >= 0 && choice < sorted.size()) {
                    Leader selected = sorted.get(choice);
                    System.out.println("You chose \"" + selected.getName() + "\"\n");
                    return selected;
                }
            } catch (NumberFormatException ignored) {}
            System.out.print("Invalid. Please enter a valid number: ");
        }
    }

    @Override
    public void displayLeaderSequence(String leaderName, int index, int total) {
        System.out.printf("%n=== Leader %d of %d: %s ===%n", index, total, leaderName);
    }

    @Override
    public void displayLevel(Level level) {
        System.out.println("\n--- Level " + level.getNumber() + " (Leader: " + level.getLeaderName() + ") ---");
        System.out.println(level.getDescription());
        System.out.println("1) " + level.getChoices().get(0).getText());
        System.out.println("2) " + level.getChoices().get(1).getText());
        System.out.print("Your choice (1 or 2): ");
    }

    @Override
    public int getPlayerChoice() {
        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice == 1 || choice == 2) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {}
            System.out.print("Invalid. Please enter 1 or 2: ");
        }
    }

    @Override
    public void displayTimeoutSkip() {
        System.out.println("[No valid input — skipping level]");
    }

    @Override
    public void displayResult(boolean correct, String summary) {
        System.out.println(correct ? "✔️ Correct!" : "❌ Incorrect");
        System.out.println(summary);
    }

    @Override
    public void showProgress(int score, int total) {
        System.out.printf("Progress: %d/%d%n", score, total);
    }

    @Override
    public void displayEndOfRound(int score, int total, long timeMillis) {
        System.out.println("\n=== Round Complete ===");
        System.out.printf("Score: %d out of %d%n", score, total);
        System.out.printf("Total Time: %.2f seconds%n", timeMillis / 1000.0);
    }

    @Override
    public boolean promptArchiveSearch() {
        System.out.print("Search your archive now? (yes/no): ");
        return sc.nextLine().trim().equalsIgnoreCase("yes");
    }

    @Override
    public String promptSearchKeyword() {
        System.out.print("Enter keyword to search: ");
        return sc.nextLine().trim();
    }

    @Override
    public int promptPostRoundOption() {
        System.out.println("\nWhat next?");
        System.out.println("  1) Play again");
        System.out.println("  2) Switch user");
        System.out.println("  3) View player statistics");
        System.out.println("  4) Quit");
        System.out.print("Enter choice (1–4): ");
        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice >= 1 && choice <= 4) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {}
            System.out.print("Invalid. Please enter 1, 2, 3, or 4: ");
        }
    }

    @Override
    public void displayLeaderboard(List<PlayerRecord> list) {
        System.out.println("\n=== Single–Leader Best Scores ===");
        System.out.printf("%-15s  %-5s  %-6s%n", "Player", "Score", "Time(s)");
        for (PlayerRecord record : list) {
            if (record.getBestSingleScore() > 0) {
                System.out.printf(
                        "%-15s  %-5d  %-6.2f%n",
                        record.getUsername(),
                        record.getBestSingleScore(),
                        record.getBestSingleTimeMillis() / 1000.0
                );
            }
        }

        System.out.println("\n=== Sequential (All Leaders) Best Scores ===");
        System.out.printf("%-15s  %-5s  %-6s%n", "Player", "Score", "Time(s)");
        for (PlayerRecord record : list) {
            if (record.getBestSequentialScore() > 0) {
                System.out.printf(
                        "%-15s  %-5d  %-6.2f%n",
                        record.getUsername(),
                        record.getBestSequentialScore(),
                        record.getBestSequentialTimeMillis() / 1000.0
                );
            }
        }

        System.out.println("\n=== Randomized (All Leaders) Best Scores ===");
        System.out.printf("%-15s  %-5s  %-6s%n", "Player", "Score", "Time(s)");
        for (PlayerRecord record : list) {
            if (record.getBestRandomizedScore() > 0) {
                System.out.printf(
                        "%-15s  %-5d  %-6.2f%n",
                        record.getUsername(),
                        record.getBestRandomizedScore(),
                        record.getBestRandomizedTimeMillis() / 1000.0
                );
            }
        }
    }

    @Override
    public void displayGoodbyeMessage() {
        System.out.println("\nThanks for playing!");
    }

    public void displayWelcomeForPlayer(PlayerRecord player) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long lastLogin = player.getLastLogin();
        System.out.println();
        if (lastLogin == null) {
            System.out.println("Welcome, " + player.getUsername() + "! You're new to Echoes of Command!");
        } else {
            System.out.println("Welcome back, " + player.getUsername() + "! Last login: " + sdf.format(new Date(lastLogin)));
        }
        System.out.print("View login history? (yes/no): ");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            displayLoginHistory(player);
        }
    }

    public void displayLoginHistory(PlayerRecord player) {
        System.out.println("\n=== Login History for " + player.getUsername() + " ===");
        List<Long> history = player.getLoginHistory();
        if (history.isEmpty()) {
            System.out.println("No login history available.");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < history.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, sdf.format(new Date(history.get(i))));
            }
        }
    }

    @Override
    public void displayPlayerStats(PlayerRecord player) {
        System.out.println("\n=== Player Statistics for " + player.getUsername() + " ===");
        System.out.printf("Total Levels Played: %d%n", player.getTotalLevelsPlayed());
        System.out.printf("Accuracy: %.2f%%%n", player.getAccuracy());
        System.out.printf("Average Time per Level: %.2f seconds%n", player.getAverageTimePerLevel());
    }

    public void offerCourseMaterial() {
        System.out.print("\nWould you like to access the course material before starting? (yes/no): ");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            accessCourseMaterial();
        }
    }

    private void accessCourseMaterial() {
        System.out.println("\nYou chose to access the course material:");
        System.out.println("  1) Read it now");
        System.out.println("  2) Download it");
        System.out.println("  3) Skip");
        System.out.print("Enter choice (1, 2, or 3): ");
        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                switch (choice) {
                    case 1 -> { readCourseMaterial(); return; }
                    case 2 -> { downloadCourseMaterial(); return; }
                    case 3 -> { System.out.println("Skipping course material."); return; }
                    default -> System.out.print("Invalid. Please enter 1, 2, or 3: ");
                }
            } catch (NumberFormatException ignored) {
                System.out.print("Invalid. Please enter 1, 2, or 3: ");
            }
        }
    }

    private void readCourseMaterial() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("course.txt")) {
            if (inputStream == null) {
                System.out.println("Course material not found.");
                return;
            }
            Scanner scanner = new Scanner(inputStream);
            System.out.println("\n=== Course Material Start ===\n");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
            System.out.println("\n=== Course Material End ===\n");
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error reading course material: " + e.getMessage());
        }
    }


    private void downloadCourseMaterial() {
        System.out.print("Enter full path to save course.pdf (example: C:/Users/You/Desktop/course.txt): ");
        String path = sc.nextLine().trim();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("course.txt")) {
            if (inputStream == null) {
                System.out.println("Course material not found.");
                return;
            }
            Files.copy(inputStream, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Course material saved successfully to: " + path);
        } catch (IOException e) {
            System.out.println("Error saving course material: " + e.getMessage());
        }
    }
}
