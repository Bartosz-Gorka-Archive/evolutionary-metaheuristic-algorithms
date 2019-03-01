package sample;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Drawer {
    public void drawInputInstance(InputInstance inputInstance, Stage primaryStage) {
        Path path = new Path();
        MoveTo moveTo = new MoveTo(100, 100);
        List<LineTo> line5 = new ArrayList<>();
        line5.add(new LineTo(24, 11));
        line5.add(new LineTo(14, 14));
        line5.add(new LineTo(111, 4));

        path.getElements().add(moveTo);
        path.getElements().addAll(line5);
        Group root = new Group(path);
        Scene scene = new Scene(root, 600, 300);

        primaryStage.setTitle("Drawing points connection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

