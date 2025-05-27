package eoc.ui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EocGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EocGUI.class.getResource("/eoc/ui/welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400); // default size
        stage.setResizable(false);
        stage.setTitle("Echoes of Command");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}