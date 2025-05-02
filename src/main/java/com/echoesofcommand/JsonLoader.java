package com.echoesofcommand;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Loads game data from JSON files.
 */
public class JsonLoader {
    /**
     * Loads leaders from a JSON resource file.
     * @param resourcePath The path to the JSON file.
     * @return A list of Leader objects.
     * @throws IllegalArgumentException If the resource is not found.
     * @throws IllegalStateException If the JSON is invalid or empty.
     */
    public List<Leader> loadLeaders(String resourcePath) {
        try (Reader reader = new InputStreamReader(JsonLoader.class.getClassLoader().getResourceAsStream(resourcePath))) {
            if (reader == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            Gson gson = new Gson();
            Type leaderListType = new TypeToken<List<Leader>>(){}.getType();
            List<Leader> leaders = gson.fromJson(reader, leaderListType);
            if (leaders == null || leaders.isEmpty()) {
                throw new IllegalStateException("No leaders found in the JSON file.");
            }
            return leaders;
        } catch (Exception e) {
            throw new IllegalStateException("Error loading " + resourcePath + ": " + e.getMessage(), e);
        }
    }
}