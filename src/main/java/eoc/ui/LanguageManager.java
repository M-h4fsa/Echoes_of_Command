package eoc.ui;

import java.util.ArrayList;
import java.util.List;

public class LanguageManager {
    private static LanguageManager instance;
    private String currentLanguage;
    private final List<LanguageChangeListener> listeners = new ArrayList<>();

    private LanguageManager() {
        // Default to English
        this.currentLanguage = "English";
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public void setLanguage(String language) {
        this.currentLanguage = language;
        System.out.println("LanguageManager: Language set to " + language);
        notifyListeners();
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public String getCourseFilePath() {
        return currentLanguage.equals("Arabic") ? "course-ar.txt" : "course.txt";
    }

    public String getHistoryFilePath() {
        return currentLanguage.equals("Arabic") ? "history-ar.json" : "history.json";
    }

    public void addLanguageChangeListener(LanguageChangeListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (LanguageChangeListener listener : listeners) {
            listener.onLanguageChanged(currentLanguage);
        }
    }

    @FunctionalInterface
    public interface LanguageChangeListener {
        void onLanguageChanged(String newLanguage);
    }
}