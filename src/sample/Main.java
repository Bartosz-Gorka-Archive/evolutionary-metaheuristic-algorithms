package sample;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        InputInstance inputInstance = new InputInstance();
        inputInstance.initInputInstance("objects.data");
        inputInstance.setGroupNumber(10);

        new Drawer().drawInputInstance(inputInstance, primaryStage);
    }
}
