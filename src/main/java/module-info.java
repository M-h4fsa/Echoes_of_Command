module eoc.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires java.desktop;
    requires annotations;
    requires com.fasterxml.jackson.databind;
    requires javafx.media;

    exports eoc.ui.model;

    opens eoc.ui.model to com.fasterxml.jackson.databind;
    opens eoc.ui to javafx.fxml;
    exports eoc.ui;
}