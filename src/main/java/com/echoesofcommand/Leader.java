package com.echoesofcommand;
import java.util.List;

public class Leader {
    private String name;
    private String backstory;
    private List<Level> levels;

    public Leader(String name, String backstory, List<Level> levels) {
        this.name = name;
        this.backstory = backstory;
        this.levels = levels;
    }

    public String getName() {
        return name;
    }

    public String getBackstory() {
        return backstory;
    }

    public List<Level> getLevels() {
        return levels;
    }
}
