package sample;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Drawer {

    /**
     * Draw instance as a graph
     *
     * @param primaryStage  Stage
     * @param coordinates   Coordinates
     * @param inputInstance Instance record
     * @param primSolver    PRIM's solution
     */
    public void drawInputInstance(Stage primaryStage, ArrayList<PointCoordinates> coordinates, InputInstance inputInstance, PrimSolver primSolver) {
        Pane root = new Pane();
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

        // Prepare path on scene
        root.getChildren().add(path);

        // Add basic points
        for (PointCoordinates coord : coordinates) {
            Circle circle = new Circle(2, Color.RED);
            circle.relocate(coord.getX() * 2 + 18, coord.getY() * 2 + 18);
            root.getChildren().add(circle);
        }

        // Set scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Points connections");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

