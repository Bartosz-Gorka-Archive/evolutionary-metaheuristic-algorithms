package sample;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

public class Drawer {

    /**
     * Draw instance as a graph
     *
     * @param primaryStage  Stage
     * @param inputInstance Instance record
     * @param primSolver    PRIM's solution
     */
    public void drawInputInstance(Stage primaryStage, InputInstance inputInstance, PrimSolver primSolver) {
        Path path = new Path();
        path.setSmooth(true);

        // Draw lines on the graph
        for (PointsPath route : primSolver.getPath()) {
            PointCoordinates startPoint = inputInstance.getPoint(route.getStartIndex());
            PointCoordinates endPoint = inputInstance.getPoint(route.getEndIndex());

            // Move to start X, start Y
            path.getElements().add(new MoveTo(startPoint.getX() * 2 + 20, startPoint.getY() * 2 + 20));

            // Add line to end X, end Y
            path.getElements().add(new LineTo(endPoint.getX() * 2 + 20, endPoint.getY() * 2 + 20));
        }

        // Prepare scene
        Group root = new Group(path);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Points connections");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

