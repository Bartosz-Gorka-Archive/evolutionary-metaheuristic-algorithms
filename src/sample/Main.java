package sample;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {
    /**
     * Number of groups (clusters)
     */
    private final static int GROUPS = 20;
    /**
     * Number of iterations in test stage
     */
    private final static int TESTS_NUMBER = 1;
    /**
     * Should we show extra logs and statistics?
     */
    private final static boolean SHOW_STATISTICS = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Reader reader = new Reader();
        ArrayList<PointCoordinates> coordinates = reader.readInstance("instances/objects20_06.data");

        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double[][] distanceMatrix = euclideanDistance.calculateDistanceMatrix(coordinates);

        HashSet<ArrayList<PointsPath>> bestNaiveGreedyGroups = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestGroups = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestRandomGreedyGroups = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestRandomSteepestGroups = new HashSet<>();

        double[] naiveGreedyResults = new double[TESTS_NUMBER], naiveSteepestResults = new double[TESTS_NUMBER],
                randomGreedyResults = new double[TESTS_NUMBER], randomSteepestResults = new double[TESTS_NUMBER],
                naiveGreedyTimes = new double[TESTS_NUMBER], naiveSteepestTimes = new double[TESTS_NUMBER],
                randomGreedyTimes = new double[TESTS_NUMBER], randomSteepestTimes = new double[TESTS_NUMBER];
        double bestNaiveGreedyResult = Double.MAX_VALUE, bestRandomGreedyResult = Double.MAX_VALUE,
                bestNaiveSteepestResult = Double.MAX_VALUE, bestRandomSteepestResult = Double.MAX_VALUE;

        // Iterations
        for (int iteration = 0; iteration < TESTS_NUMBER; iteration++) {
            /*
             * INITIALIZATION STEP IN ITERATION
             */
            Random random = new Random();
            int totalElementsLength = coordinates.size();

            // Generate randomized indexes of start points
            HashSet<Integer> startIndexesSet = new HashSet<>();
            while (startIndexesSet.size() < GROUPS) {
                startIndexesSet.add(random.nextInt(totalElementsLength));
            }

            // Cast set to list
            ArrayList<Integer> startIndexesList = new ArrayList<>(startIndexesSet);

            // Naive and random instances
            HashMap<Integer, HashSet<Integer>> naiveInstances = naiveAlgorithm(distanceMatrix, startIndexesList, coordinates);
            HashMap<Integer, HashSet<Integer>> randomInstances = randomInitGroups(distanceMatrix, startIndexesList, coordinates);

            /*
             * GREEDY NAIVE
             */
            long startTime = System.nanoTime();
            GreedyLocalSolver naiveGreedyLocalSolver = new GreedyLocalSolver(naiveInstances);
            naiveGreedyLocalSolver.run(distanceMatrix);
            naiveGreedyResults[iteration] = naiveGreedyLocalSolver.getPenalties();
            if (naiveGreedyLocalSolver.getPenalties() < bestNaiveGreedyResult) {
                bestNaiveGreedyResult = naiveGreedyLocalSolver.getPenalties();
                bestNaiveGreedyGroups = castLocalSearchToGraph(naiveGreedyLocalSolver.getGroups(), distanceMatrix);
            }
            naiveGreedyTimes[iteration] = System.nanoTime() - startTime;

            /*
             * GREEDY RANDOM
             */
            startTime = System.nanoTime();
            GreedyLocalSolver randomGreedyLocalSolver = new GreedyLocalSolver(randomInstances);
            randomGreedyLocalSolver.run(distanceMatrix);
            randomGreedyResults[iteration] = randomGreedyLocalSolver.getPenalties();
            if (randomGreedyLocalSolver.getPenalties() < bestRandomGreedyResult) {
                bestRandomGreedyResult = randomGreedyLocalSolver.getPenalties();
                bestRandomGreedyGroups = castLocalSearchToGraph(randomGreedyLocalSolver.getGroups(), distanceMatrix);
            }
            randomGreedyTimes[iteration] = System.nanoTime() - startTime;

            /*
             * STEEPEST NAIVE
             */
            startTime = System.nanoTime();
            SteepestLocalSolver naiveSteepestLocalSolver = new SteepestLocalSolver(naiveInstances);
            naiveSteepestLocalSolver.run(distanceMatrix);
            naiveSteepestResults[iteration] = naiveSteepestLocalSolver.getPenalties();
            if (naiveSteepestLocalSolver.getPenalties() < bestNaiveSteepestResult) {
                bestNaiveSteepestResult = naiveSteepestLocalSolver.getPenalties();
                bestNaiveSteepestGroups = castLocalSearchToGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
            }
            naiveSteepestTimes[iteration] = System.nanoTime() - startTime;

            /*
             * STEEPEST RANDOM
             */
            startTime = System.nanoTime();
            SteepestLocalSolver randomSteepestLocalSolver = new SteepestLocalSolver(randomInstances);
            randomSteepestLocalSolver.run(distanceMatrix);
            randomSteepestResults[iteration] = randomSteepestLocalSolver.getPenalties();
            if (randomSteepestLocalSolver.getPenalties() < bestRandomSteepestResult) {
                bestRandomSteepestResult = randomSteepestLocalSolver.getPenalties();
                bestRandomSteepestGroups = castLocalSearchToGraph(randomSteepestLocalSolver.getGroups(), distanceMatrix);
            }
            randomSteepestTimes[iteration] = System.nanoTime() - startTime;
        }

        // Show groups on graph
        new Drawer().drawInputInstance(coordinates, bestNaiveGreedyGroups, "Naive greedy");
        new Drawer().drawInputInstance(coordinates, bestRandomGreedyGroups, "Random greedy");
        new Drawer().drawInputInstance(coordinates, bestRandomSteepestGroups, "Random steepest");
        new Drawer().drawInputInstance(coordinates, bestNaiveSteepestGroups, "Naive steepest");

        if (SHOW_STATISTICS) {
            System.out.println("Min result for naive greedy = " + bestNaiveGreedyResult);
            System.out.println("Min result for random greedy = " + bestRandomGreedyResult);
            System.out.println("Min result for naive steepest = " + bestNaiveSteepestResult);
            System.out.println("Min result for random steepest = " + bestRandomSteepestResult);

            System.out.println("Mean result for naive greedy = " + Arrays.stream(naiveGreedyResults).average().getAsDouble());
            System.out.println("Mean result for random greedy = " + Arrays.stream(randomGreedyResults).average().getAsDouble());
            System.out.println("Mean result for naive steepest = " + Arrays.stream(naiveSteepestResults).average().getAsDouble());
            System.out.println("Mean result for random steepest = " + Arrays.stream(randomSteepestResults).average().getAsDouble());

            System.out.println("Max result for naive greedy = " + Arrays.stream(naiveGreedyResults).max().getAsDouble());
            System.out.println("Max result for random greedy = " + Arrays.stream(randomGreedyResults).max().getAsDouble());
            System.out.println("Max result for naive steepest = " + Arrays.stream(naiveSteepestResults).max().getAsDouble());
            System.out.println("Max result for random steepest = " + Arrays.stream(randomSteepestResults).max().getAsDouble());

            System.out.println("TIMING:");
            System.out.println("Min time for naive greedy = " + Arrays.stream(naiveGreedyTimes).min().getAsDouble());
            System.out.println("Min time for random greedy = " + Arrays.stream(randomGreedyTimes).min().getAsDouble());
            System.out.println("Min time for naive steepest = " + Arrays.stream(naiveSteepestTimes).min().getAsDouble());
            System.out.println("Min time for random steepest = " + Arrays.stream(randomSteepestTimes).min().getAsDouble());

            System.out.println("Mean time for  naive greedy = " + Arrays.stream(naiveGreedyTimes).average().getAsDouble());
            System.out.println("Mean time for random greedy = " + Arrays.stream(randomGreedyTimes).average().getAsDouble());
            System.out.println("Mean time for naive steepest = " + Arrays.stream(naiveSteepestTimes).average().getAsDouble());
            System.out.println("Mean time for random steepest = " + Arrays.stream(randomSteepestTimes).average().getAsDouble());

            System.out.println("Max time for naive greedy = " + Arrays.stream(naiveGreedyTimes).max().getAsDouble());
            System.out.println("Max time for random greedy = " + Arrays.stream(randomGreedyTimes).max().getAsDouble());
            System.out.println("Max time for naive steepest = " + Arrays.stream(naiveSteepestTimes).max().getAsDouble());
            System.out.println("Max time for random steepest = " + Arrays.stream(randomSteepestTimes).max().getAsDouble());
        }
    }

    /**
     * Change assignment to connection on graph
     *
     * @param algorithmResults Assignment to group
     * @param distanceMatrix   Distance matrix
     * @return Connections on graph (paths)
     */
    private HashSet<ArrayList<PointsPath>> castLocalSearchToGraph(HashMap<Integer, HashSet<Integer>> algorithmResults, double[][] distanceMatrix) {
        HashSet<ArrayList<PointsPath>> groupsWithPaths = new HashSet<>();
        PrimSolver solver = new PrimSolver();

        for (Map.Entry<Integer, HashSet<Integer>> res : algorithmResults.entrySet()) {
            solver.construct(res.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            groupsWithPaths.add(solver.getPath());
        }

        return groupsWithPaths;
    }

    /**
     * Naive clustering algorithm
     *
     * @param distanceMatrix   Distance matrix
     * @param startIndexesList Start assignment (indexes)
     * @param coordinates      All points list
     * @return Naive assignment to groups
     */
    private HashMap<Integer, HashSet<Integer>> naiveAlgorithm(double[][] distanceMatrix, ArrayList<Integer> startIndexesList, ArrayList<PointCoordinates> coordinates) {
        // k-means with static center
        HashMap<Integer, HashSet<Integer>> elementsWithAssignmentToGroups = new HashMap<>();

        // Initialize groups
        for (int index : startIndexesList) {
            elementsWithAssignmentToGroups.put(index, new HashSet<>());
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
            elementsWithAssignmentToGroups.get(selectedGroupIndex).add(ID);
        }

        // Calculate penalties when enabled statistics
        if (SHOW_STATISTICS) {
            System.out.println("Mean of penalties for naive = " + new Judge().calcMeanDistance(elementsWithAssignmentToGroups, distanceMatrix));
        }

        return elementsWithAssignmentToGroups;
    }

    /**
     * @param distanceMatrix   Distance matrix
     * @param startIndexesList Start assignment (indexes)
     * @param coordinates      All points list
     * @return Random assignment to groups
     */
    private HashMap<Integer, HashSet<Integer>> randomInitGroups(double[][] distanceMatrix, ArrayList<Integer> startIndexesList, ArrayList<PointCoordinates> coordinates) {
        HashMap<Integer, HashSet<Integer>> elementsWithAssignmentToGroups = new HashMap<>();
        Random random = new Random();

        // Initialize groups
        for (int i = 0; i < GROUPS; i++) {
            HashSet<Integer> set = new HashSet<>();
            set.add(startIndexesList.get(i));
            elementsWithAssignmentToGroups.put(i, set);
        }

        // Assign each point to group
        for (PointCoordinates point : coordinates) {
            // Add point only when not in start
            if (!startIndexesList.contains(point.getID())) {
                elementsWithAssignmentToGroups.get(random.nextInt(GROUPS)).add(point.getID());
            }
        }

        // Calculate penalties when enabled statistics
        if (SHOW_STATISTICS) {
            System.out.println("Mean of penalties for random init = " + new Judge().calcMeanDistance(elementsWithAssignmentToGroups, distanceMatrix));
        }

        return elementsWithAssignmentToGroups;
    }
}
