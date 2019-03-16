package sample;

import com.sun.tools.javac.util.ArrayUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.*;

public class Main {
// public class Main extends Application {
    private final static int GROUPS = 10;

    public static void main(String[] args) {
//        launch(args);
//    }

//    @Override
//    public void start(Stage primaryStage) {
        Reader reader = new Reader();
        ArrayList<PointCoordinates> coordinates = reader.readInstance("objects.data");

        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double[][] distanceMatrix = euclideanDistance.calculateDistanceMatrix(coordinates);

        // Random start indexes
        HashSet<Integer> startIndexesList = new HashSet<>();
        int totalElementsLength = coordinates.size();
        Random random = new Random();

        // Generate randomized indexes of start points
        while (startIndexesList.size() < GROUPS) {
            startIndexesList.add(random.nextInt(totalElementsLength));
        }

        // k-means with static center
        HashMap<Integer, HashSet<Integer>> elementsWithAssigmentToGroups = new HashMap<>();

        // Initialize groups
        for (int index : startIndexesList) {
            elementsWithAssigmentToGroups.put(index, new HashSet<>());
        }

        // Assign each point to group
        for (PointCoordinates point : coordinates) {
            int ID = point.getID();
            int selectedGroupIndex = 0;
            double minDistanceValue = Double.MAX_VALUE;

            for (int centerPointIndex : startIndexesList) {
                // Get distance from array
                double distance = distanceMatrix[centerPointIndex][ID];

                // Check distance is smaller than current stored - if yes => update index
                if (distance < minDistanceValue) {
                    minDistanceValue = distance;
                    selectedGroupIndex = centerPointIndex;
                }
            }

            // Add point to selected group
            elementsWithAssigmentToGroups.get(selectedGroupIndex).add(ID);
        }

        // Calculate sum of MSTs
        double sumOfPenalties = 0.0;
        for (Map.Entry<Integer, HashSet<Integer>> group : elementsWithAssigmentToGroups.entrySet()) {
            PrimSolver solver = new PrimSolver();
            solver.construct(group.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            sumOfPenalties += solver.getPenalties();
        }
        System.out.println("Sum of penalties for naive = " + sumOfPenalties);

//        int[] startPoints = {0, 1, 2};
//        ArrayList<Integer> startIndexes = new ArrayList<>();
//        HashMap<Integer, HashSet<Integer>> groups = new HashMap<>();
//        for (int i : startPoints) {
//            startIndexes.add(i);
//            HashSet<Integer> hs = new HashSet<>();
//            hs.add(i);
//            groups.put(i, hs);
//        }

        // Copy distance array to enable modifications
        // TODO

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

        // For each group - recalculate center
        // TODO

        // If assignation element to group changed - run next loop
        // TODO


//        StartPointAssigner assigner = new StartPointAssigner(GROUPS, coordinates, distanceMatrix);
//        InputInstance inputInstance = assigner.prepareRandomAssign().get(0);

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
//        new Drawer().drawInputInstance(primaryStage, coordinates, inputInstance, primSolver);
    }
}
