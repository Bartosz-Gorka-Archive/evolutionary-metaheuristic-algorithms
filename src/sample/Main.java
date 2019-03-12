package sample;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

public class Main extends Application {
    private final static int GROUPS = 10;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Reader reader = new Reader();
        ArrayList<PointCoordinates> coordinates = reader.readInstance("objects.data");

        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double[][] distanceMatrix = euclideanDistance.calculateDistanceMatrix(coordinates);

//        int[] startPoints = {0, 1, 2};
//        ArrayList<Integer> indexes = new ArrayList<>();
//        HashMap<Integer, HashSet<Integer>> groups = new HashMap<>();
//        for (int i : startPoints) {
//            indexes.add(i);
//            HashSet<Integer> hs = new HashSet<>();
//            hs.add(i);
//            groups.put(i, hs);
//        }
//
//        for (int i = 0; i < distanceMatrix.length; i++) {
//            int min = 0;
//            double dist = Integer.MAX_VALUE;
//            for (int pos : startPoints) {
//                if (dist > distanceMatrix[i][pos]) {
//                    dist = distanceMatrix[i][pos];
//                    min = pos;
//                }
//            }
//            HashSet<Integer> var = groups.get(min);
//            var.add(i);
//            groups.replace(min, var);
//        }


        StartPointAssigner assigner = new StartPointAssigner(GROUPS, coordinates, distanceMatrix);
        InputInstance inputInstance = assigner.prepareRandomAssign().get(0);

        PrimSolver primSolver = new PrimSolver();

        // Construct single MST
        int[] indexes = new int[coordinates.size()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        primSolver.construct(indexes, distanceMatrix);

        // Remove GROUPS - 1 arches and prepare GROUPS groups
        ArrayList<PointsPath> tempPath = (ArrayList<PointsPath>) primSolver.getPath().clone();
        tempPath.sort(Collections.reverseOrder());

        for (PointsPath p : tempPath.subList(0, GROUPS - 1)) {
            ListIterator<PointsPath> iter = primSolver.getPath().listIterator();

            while (iter.hasNext()) {
                if (iter.next().compareTo(p) == 0) {
                    iter.remove();
                    break;
                }
            }
        }

        // Recalculate penalties and show it
        primSolver.calculatePenalties();
        System.out.println(GROUPS + " groups with penalties = " + primSolver.getPenalties());

        // Draw solution as a graph
        new Drawer().drawInputInstance(primaryStage, coordinates, inputInstance, primSolver);
    }
}
