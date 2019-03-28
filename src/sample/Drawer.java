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
import java.util.HashSet;

/*
 * TODO list
 *  add different colors
 */
public class Drawer {
    Color[] colors = {
            Color.RED,
            Color.BLUE,
            Color.GOLD,
            Color.BLACK,
            Color.SIENNA,
            Color.SILVER,
            Color.YELLOW,
            Color.VIOLET,
            Color.SKYBLUE,
            Color.SPRINGGREEN,
            Color.RED,
            Color.BLUE,
            Color.GOLD,
            Color.BLACK,
            Color.SIENNA,
            Color.SILVER,
            Color.YELLOW,
            Color.VIOLET,
            Color.SKYBLUE,
            Color.SPRINGGREEN
    };

    /**
     * Draw instance as a graph
     *
     * @param coordinates Coordinates
     * @param groups      PRIMs' solutions
     */
    public void drawInputInstance(ArrayList<PointCoordinates> coordinates, HashSet<ArrayList<PointsPath>> groups) {
        Pane root = new Pane();
        Path path = new Path();
        path.setSmooth(true);

        // Draw lines on the graph
        int groupColorID = 0;
        for (ArrayList<PointsPath> group : groups) {
            Color selectedColor = colors[groupColorID];
            for (PointsPath route : group) {
                PointCoordinates startPoint = coordinates.get(route.getStartIndex());
                PointCoordinates endPoint = coordinates.get(route.getEndIndex());

                // Move to start X, start Y
                path.getElements().add(new MoveTo(startPoint.getX() * 2 + 20, startPoint.getY() * 2 + 20));
                Circle startCircle = new Circle(2, selectedColor);
                startCircle.relocate(startPoint.getX() * 2 + 18, startPoint.getY() * 2 + 18);
                root.getChildren().add(startCircle);

                // Add line to end X, end Y
                path.getElements().add(new LineTo(endPoint.getX() * 2 + 20, endPoint.getY() * 2 + 20));
                Circle endCircle = new Circle(2, selectedColor);
                endCircle.relocate(endPoint.getX() * 2 + 18, endPoint.getY() * 2 + 18);
                root.getChildren().add(endCircle);
            }

            groupColorID++;
        }

        // Prepare path on scene
        root.getChildren().add(path);

        // Set scene
        Stage stage = new Stage();
        Scene scene = new Scene(root, 700, 600);
        stage.setTitle("Points connections");
        stage.setScene(scene);
        stage.show();
    }
}

