package com.echoesofcommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a level in the game with a description, choices, and summary.
 */
public class Level {
    private int number;
    private String description;
    private List<Choice> choices;
    private String summary;
    private String leaderName;

    /**
     * Default constructor for JSON deserialization.
     */
    public Level() {
        this.choices = new ArrayList<>();
    }

    /**
     * Creates a new level.
     * @param number The level number.
     * @param description The level description.
     * @param choices The list of choices.
     * @param summary The level summary.
     * @param leaderName The name of the leader associated with this level.
     */
    public Level(int number, String description, List<Choice> choices, String summary, String leaderName) {
        this.number = number;
        this.description = description;
        this.choices = choices;
        this.summary = summary;
        this.leaderName = leaderName;
    }

    /**
     * Gets the level number.
     * @return The level number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the level description.
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the list of choices.
     * @return The choices.
     */
    public List<Choice> getChoices() {
        return new ArrayList<>(choices);
    }

    /**
     * Gets the level summary.
     * @return The summary.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Gets the leader's name associated with this level.
     * @return The leader's name.
     */
    public String getLeaderName() {
        return leaderName;
    }

    /**
     * Randomizes the order of the choices.
     */
    public void randomizeChoices() {
        Collections.shuffle(choices);
    }
}