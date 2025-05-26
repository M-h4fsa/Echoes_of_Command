package eoc.ui.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Choice {
    private String text;

    @JsonProperty("isHistorical")
    private boolean isHistorical;

    public String getText() {
        return text;
    }

    public boolean isHistorical() {
        return isHistorical;
    }

    // Optional setters if needed by Jackson
    public void setText(String text) {
        this.text = text;
    }

    public void setIsHistorical(boolean isHistorical) {
        this.isHistorical = isHistorical;
    }
}
