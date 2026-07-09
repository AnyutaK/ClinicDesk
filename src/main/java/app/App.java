package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Theme;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        MainLayout mainLayout = new MainLayout();

        Scene scene = new Scene(mainLayout, 1440, 900);
        scene.getStylesheets().add(Theme.STYLE_SHEET);

        stage.setTitle("ClinicDesk");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(760);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
