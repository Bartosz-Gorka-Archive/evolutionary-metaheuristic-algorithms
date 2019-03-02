package sample;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Reader reader = new Reader();
        ArrayList<PointCoordinates> coordinates = reader.readInstance("objects.data");

        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double[][] distanceMatrix = euclideanDistance.calculateDistanceMatrix(coordinates);

        InputInstance inputInstance = new InputInstance(distanceMatrix);
        inputInstance.setGroupNumber(10);

        PrimSolver primSolver = new PrimSolver();
        int[] indexes = {0, 1, 2, 3, 4};
        primSolver.construct(indexes, distanceMatrix);

        new Drawer().drawInputInstance(inputInstance, primaryStage);
    }
}
