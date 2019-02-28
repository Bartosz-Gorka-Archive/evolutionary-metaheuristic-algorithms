package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        InputInstance inputInstance = new InputInstance();
        inputInstance.initInputInstance("Objects.data");
        inputInstance.setGroupNumber(10);

        new Drawer().drawInputInstance(inputInstance, primaryStage);


    }


    public static void main(String[] args) {
        launch(args);
    }
}
