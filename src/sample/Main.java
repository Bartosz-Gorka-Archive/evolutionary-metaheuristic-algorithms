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
        inputInstance.setPoints(coordinates);
        inputInstance.setGroupNumber(10);

        PrimSolver primSolver = new PrimSolver();

        // Construct single MST
        int[] indexes = new int[coordinates.size()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        primSolver.construct(indexes, distanceMatrix);

        // Draw solution as a graph
        new Drawer().drawInputInstance(primaryStage, inputInstance, primSolver);
    }
}
