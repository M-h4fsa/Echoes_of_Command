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


    opens eoc.ui to javafx.fxml;
    exports eoc.ui;
    opens com.echoesofcommand to com.google.gson, javafx.fxml;
    exports com.echoesofcommand;
}