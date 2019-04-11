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
    /**
     * Should we use following algorithms?
     */
    private final static boolean EXECUTE_GREEDY_NAIVE = false;
    private final static boolean EXECUTE_GREEDY_RANDOM = false;
    private final static boolean EXECUTE_STEEPEST_NAIVE = true;
    private final static boolean EXECUTE_STEEPEST_RANDOM = false;
    private final static boolean EXECUTE_STEEPEST_CANDIDATE = true;
    /**
     * How many candidates we chose in steepest naive candidates algorithm
     */
    private final static int CANDIDATES_NUMBER = 10;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Reader reader = new Reader();
        ArrayList<PointCoordinates> coordinates = reader.readInstance("instances/objects20_06.data");

        EuclideanDistance euclideanDistance = new EuclideanDistance();
        double[][] distanceMatrix = euclideanDistance.calculateDistanceMatrix(coordinates);

        HashSet<ArrayList<PointsPath>> bestNaiveGreedyGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveGreedyGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestRandomGreedyGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestRandomGreedyGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestRandomSteepestGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestRandomSteepestGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestCandidateGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestCandidateGroupsConnections = new HashSet<>();

        double[] naiveGreedyResults = new double[TESTS_NUMBER], naiveSteepestResults = new double[TESTS_NUMBER],
                randomGreedyResults = new double[TESTS_NUMBER], randomSteepestResults = new double[TESTS_NUMBER],
                naiveSteepestCandidateResults = new double[TESTS_NUMBER],
                naiveGreedyTimes = new double[TESTS_NUMBER], naiveSteepestTimes = new double[TESTS_NUMBER],
                randomGreedyTimes = new double[TESTS_NUMBER], randomSteepestTimes = new double[TESTS_NUMBER],
                naiveSteepestCandidateTimes = new double[TESTS_NUMBER];

        double bestNaiveGreedyResult = Double.MAX_VALUE, bestRandomGreedyResult = Double.MAX_VALUE,
                bestNaiveSteepestResult = Double.MAX_VALUE, bestRandomSteepestResult = Double.MAX_VALUE,
                bestNaiveSteepestCandidateResult = Double.MAX_VALUE;

        // Using for statistics
        long startTime;

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
            if (EXECUTE_GREEDY_NAIVE) {
                startTime = System.nanoTime();
                GreedyLocalSolver naiveGreedyLocalSolver = new GreedyLocalSolver(naiveInstances);
                naiveGreedyLocalSolver.run(distanceMatrix);
                naiveGreedyResults[iteration] = naiveGreedyLocalSolver.getPenalties();
                if (naiveGreedyLocalSolver.getPenalties() < bestNaiveGreedyResult) {
                    bestNaiveGreedyResult = naiveGreedyLocalSolver.getPenalties();
                    bestNaiveGreedyGroupsMST = castLocalSearchToMSTGraph(naiveGreedyLocalSolver.getGroups(), distanceMatrix);
                    bestNaiveGreedyGroupsConnections = castLocalSearchToConnectionGraph(naiveGreedyLocalSolver.getGroups(), distanceMatrix);
                }
                naiveGreedyTimes[iteration] = System.nanoTime() - startTime;
            }
            /*
             * GREEDY RANDOM
             */
            if (EXECUTE_GREEDY_RANDOM) {
                startTime = System.nanoTime();
                GreedyLocalSolver randomGreedyLocalSolver = new GreedyLocalSolver(randomInstances);
                randomGreedyLocalSolver.run(distanceMatrix);
                randomGreedyResults[iteration] = randomGreedyLocalSolver.getPenalties();
                if (randomGreedyLocalSolver.getPenalties() < bestRandomGreedyResult) {
                    bestRandomGreedyResult = randomGreedyLocalSolver.getPenalties();
                    bestRandomGreedyGroupsMST = castLocalSearchToMSTGraph(randomGreedyLocalSolver.getGroups(), distanceMatrix);
                    bestRandomGreedyGroupsConnections = castLocalSearchToConnectionGraph(randomGreedyLocalSolver.getGroups(), distanceMatrix);
                }
                randomGreedyTimes[iteration] = System.nanoTime() - startTime;
            }
            /*
             * STEEPEST NAIVE
             */
            if (EXECUTE_STEEPEST_NAIVE) {
                startTime = System.nanoTime();
                SteepestLocalSolver naiveSteepestLocalSolver = new SteepestLocalSolver(naiveInstances, false, CANDIDATES_NUMBER);
                naiveSteepestLocalSolver.run(distanceMatrix);
                naiveSteepestResults[iteration] = naiveSteepestLocalSolver.getPenalties();
                if (naiveSteepestLocalSolver.getPenalties() < bestNaiveSteepestResult) {
                    bestNaiveSteepestResult = naiveSteepestLocalSolver.getPenalties();
                    bestNaiveSteepestGroupsMST = castLocalSearchToMSTGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                    bestNaiveSteepestGroupsConnections = castLocalSearchToConnectionGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                }
                naiveSteepestTimes[iteration] = System.nanoTime() - startTime;
            }
            /*
             * STEEPEST RANDOM
             */
            if (EXECUTE_STEEPEST_RANDOM) {
                startTime = System.nanoTime();
                SteepestLocalSolver randomSteepestLocalSolver = new SteepestLocalSolver(randomInstances, false, CANDIDATES_NUMBER);
                randomSteepestLocalSolver.run(distanceMatrix);
                randomSteepestResults[iteration] = randomSteepestLocalSolver.getPenalties();
                if (randomSteepestLocalSolver.getPenalties() < bestRandomSteepestResult) {
                    bestRandomSteepestResult = randomSteepestLocalSolver.getPenalties();
                    bestRandomSteepestGroupsMST = castLocalSearchToMSTGraph(randomSteepestLocalSolver.getGroups(), distanceMatrix);
                    bestRandomSteepestGroupsConnections = castLocalSearchToConnectionGraph(randomSteepestLocalSolver.getGroups(), distanceMatrix);
                }
                randomSteepestTimes[iteration] = System.nanoTime() - startTime;
            }
            if (EXECUTE_STEEPEST_CANDIDATE) {
                startTime = System.nanoTime();
                SteepestLocalSolver naiveSteepestLocalSolver = new SteepestLocalSolver(naiveInstances, true, CANDIDATES_NUMBER);
                naiveSteepestLocalSolver.run(distanceMatrix);
                naiveSteepestCandidateResults[iteration] = naiveSteepestLocalSolver.getPenalties();
                if (naiveSteepestLocalSolver.getPenalties() < bestNaiveSteepestCandidateResult) {
                    bestNaiveSteepestCandidateResult = naiveSteepestLocalSolver.getPenalties();
                    bestNaiveSteepestCandidateGroupsMST = castLocalSearchToMSTGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                    bestNaiveSteepestCandidateGroupsConnections = castLocalSearchToConnectionGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                }
                naiveSteepestCandidateTimes[iteration] = System.nanoTime() - startTime;
            }
        }

        // Show groups on graph
        if (EXECUTE_GREEDY_NAIVE) {
            new Drawer().drawInputInstance(coordinates, bestNaiveGreedyGroupsMST, "Naive greedy", true, true);
            new Drawer().drawInputInstance(coordinates, bestNaiveGreedyGroupsMST, "Naive greedy", true, false);
            new Drawer().drawInputInstance(coordinates, bestNaiveGreedyGroupsConnections, "Naive greedy", false, true);
        }

        if (EXECUTE_GREEDY_RANDOM) {
            new Drawer().drawInputInstance(coordinates, bestRandomGreedyGroupsMST, "Random greedy", true, true);
            new Drawer().drawInputInstance(coordinates, bestRandomGreedyGroupsMST, "Random greedy", true, false);
            new Drawer().drawInputInstance(coordinates, bestRandomGreedyGroupsConnections, "Random greedy", false, true);
        }

        if (EXECUTE_STEEPEST_NAIVE) {
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestGroupsMST, "Naive steepest", true, true);
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestGroupsMST, "Naive steepest", true, false);
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestGroupsConnections, "Naive steepest", false, true);
        }

        if (EXECUTE_STEEPEST_RANDOM) {
            new Drawer().drawInputInstance(coordinates, bestRandomSteepestGroupsMST, "Random steepest", true, true);
            new Drawer().drawInputInstance(coordinates, bestRandomSteepestGroupsMST, "Random steepest", true, false);
            new Drawer().drawInputInstance(coordinates, bestRandomSteepestGroupsConnections, "Random steepest", false, true);
        }

        if (EXECUTE_STEEPEST_CANDIDATE) {
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCandidateGroupsMST, "Naive steepest candidate", true, true);
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCandidateGroupsMST, "Naive steepest candidate", true, false);
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCandidateGroupsConnections, "Naive steepest candidate", false, true);
        }

        if (SHOW_STATISTICS) {
            if (EXECUTE_GREEDY_NAIVE)
                System.out.println("Min result for naive greedy = " + bestNaiveGreedyResult);
            if (EXECUTE_GREEDY_RANDOM)
                System.out.println("Min result for random greedy = " + bestRandomGreedyResult);
            if (EXECUTE_STEEPEST_NAIVE)
                System.out.println("Min result for naive steepest = " + bestNaiveSteepestResult);
            if (EXECUTE_STEEPEST_RANDOM)
                System.out.println("Min result for random steepest = " + bestRandomSteepestResult);
            if (EXECUTE_STEEPEST_CANDIDATE)
                System.out.println("Min result for naive steepest candidate = " + bestNaiveSteepestCandidateResult);

            if (EXECUTE_GREEDY_NAIVE)
                System.out.println("Mean result for naive greedy = " + Arrays.stream(naiveGreedyResults).average().getAsDouble());
            if (EXECUTE_GREEDY_RANDOM)
                System.out.println("Mean result for random greedy = " + Arrays.stream(randomGreedyResults).average().getAsDouble());
            if (EXECUTE_STEEPEST_NAIVE)
                System.out.println("Mean result for naive steepest = " + Arrays.stream(naiveSteepestResults).average().getAsDouble());
            if (EXECUTE_STEEPEST_RANDOM)
                System.out.println("Mean result for random steepest = " + Arrays.stream(randomSteepestResults).average().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE)
                System.out.println("Mean result for naive steepest candidate = " + Arrays.stream(naiveSteepestCandidateResults).average().getAsDouble());

            if (EXECUTE_GREEDY_NAIVE)
                System.out.println("Max result for naive greedy = " + Arrays.stream(naiveGreedyResults).max().getAsDouble());
            if (EXECUTE_GREEDY_RANDOM)
                System.out.println("Max result for random greedy = " + Arrays.stream(randomGreedyResults).max().getAsDouble());
            if (EXECUTE_STEEPEST_NAIVE)
                System.out.println("Max result for naive steepest = " + Arrays.stream(naiveSteepestResults).max().getAsDouble());
            if (EXECUTE_STEEPEST_RANDOM)
                System.out.println("Max result for random steepest = " + Arrays.stream(randomSteepestResults).max().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE)
                System.out.println("Max result for naive steepest candidate = " + Arrays.stream(naiveSteepestCandidateResults).max().getAsDouble());

            System.out.println("TIMING:");
            if (EXECUTE_GREEDY_NAIVE)
                System.out.println("Min time for naive greedy = " + Arrays.stream(naiveGreedyTimes).min().getAsDouble());
            if (EXECUTE_GREEDY_RANDOM)
                System.out.println("Min time for random greedy = " + Arrays.stream(randomGreedyTimes).min().getAsDouble());
            if (EXECUTE_STEEPEST_NAIVE)
                System.out.println("Min time for naive steepest = " + Arrays.stream(naiveSteepestTimes).min().getAsDouble());
            if (EXECUTE_STEEPEST_RANDOM)
                System.out.println("Min time for random steepest = " + Arrays.stream(randomSteepestTimes).min().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE)
                System.out.println("Min time for naive steepest candidate = " + Arrays.stream(naiveSteepestCandidateTimes).min().getAsDouble());

            if (EXECUTE_GREEDY_NAIVE)
                System.out.println("Mean time for naive greedy = " + Arrays.stream(naiveGreedyTimes).average().getAsDouble());
            if (EXECUTE_GREEDY_RANDOM)
                System.out.println("Mean time for random greedy = " + Arrays.stream(randomGreedyTimes).average().getAsDouble());
            if (EXECUTE_STEEPEST_NAIVE)
                System.out.println("Mean time for naive steepest = " + Arrays.stream(naiveSteepestTimes).average().getAsDouble());
            if (EXECUTE_STEEPEST_RANDOM)
                System.out.println("Mean time for random steepest = " + Arrays.stream(randomSteepestTimes).average().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE)
                System.out.println("Mean time for naive steepest candidate = " + Arrays.stream(naiveSteepestCandidateTimes).average().getAsDouble());

            if (EXECUTE_GREEDY_NAIVE)
                System.out.println("Max time for naive greedy = " + Arrays.stream(naiveGreedyTimes).max().getAsDouble());
            if (EXECUTE_GREEDY_RANDOM)
                System.out.println("Max time for random greedy = " + Arrays.stream(randomGreedyTimes).max().getAsDouble());
            if (EXECUTE_STEEPEST_NAIVE)
                System.out.println("Max time for naive steepest = " + Arrays.stream(naiveSteepestTimes).max().getAsDouble());
            if (EXECUTE_STEEPEST_RANDOM)
                System.out.println("Max time for random steepest = " + Arrays.stream(randomSteepestTimes).max().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE)
                System.out.println("Max time for naive steepest candidate = " + Arrays.stream(naiveSteepestCandidateTimes).max().getAsDouble());
        }
    }

    /**
     * Change assignment to connection on graph
     *
     * @param algorithmResults Assignment to group
     * @param distanceMatrix   Distance matrix
     * @return Connections on graph (paths)
     */
    private HashSet<ArrayList<PointsPath>> castLocalSearchToMSTGraph(HashMap<Integer, HashSet<Integer>> algorithmResults, double[][] distanceMatrix) {
        HashSet<ArrayList<PointsPath>> groupsWithPaths = new HashSet<>();
        PrimSolver solver = new PrimSolver();

        for (Map.Entry<Integer, HashSet<Integer>> res : algorithmResults.entrySet()) {
            solver.construct(res.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            groupsWithPaths.add(solver.getPath());
        }

        return groupsWithPaths;
    }

    /**
     * Change assignment to connection on graph
     *
     * @param algorithmResults Assignment to group
     * @param distanceMatrix   Distance matrix
     * @return Connections on graph (paths)
     */
    private HashSet<ArrayList<PointsPath>> castLocalSearchToConnectionGraph(HashMap<Integer, HashSet<Integer>> algorithmResults, double[][] distanceMatrix) {
        HashSet<ArrayList<PointsPath>> groupsWithPaths = new HashSet<>();

        for (Map.Entry<Integer, HashSet<Integer>> entry : algorithmResults.entrySet()) {
            ArrayList<PointsPath> path = new ArrayList<>();
            int len = entry.getValue().size();
            int ind_i, ind_j;
            int[] indexes = entry.getValue().stream().mapToInt(Integer::intValue).toArray();

            for (int i = 0; i < len; i++) {
                ind_i = indexes[i];

                for (int j = i + 1; j < len; j++) {
                    ind_j = indexes[j];
                    path.add(new PointsPath(ind_i, ind_j, distanceMatrix[ind_i][ind_j]));
                }
            }

            groupsWithPaths.add(path);
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
