package sample;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {
    private final static int GROUPS = 20;
    private final static int TESTS_NUMBER = 1;
    private final static boolean SHOW_STATISTICS = true;

    private HashSet<ArrayList<PointsPath>> preparedGroups;
    //For random algorithm init
    private HashSet<ArrayList<PointsPath>> bestRandomGroups;
    //For regret algorithm
    private HashSet<ArrayList<PointsPath>> bestRegretGroups;
    //For steepest local algorithm
    private HashSet<ArrayList<PointsPath>> bestSteepestGroups;
    //For steepest local algorithm
    private HashSet<ArrayList<PointsPath>> bestGreedyGroups;

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
            // Random start indexes for naive
            HashSet<Integer> startIndexesSet = new HashSet<>();
            int totalElementsLength = coordinates.size();
            Random random = new Random();
            // Generate randomized indexes of start points
            while (startIndexesSet.size() < GROUPS) {
                startIndexesSet.add(random.nextInt(totalElementsLength));
            }
            ArrayList<Integer> startIndexesList = new ArrayList<>(startIndexesSet);

            //Naive and random init instance
            HashMap<Integer, HashSet<Integer>> naiveInstance = naiveAlgorithm(distanceMatrix, startIndexesList, coordinates);
            HashMap<Integer, HashSet<Integer>> randomInstance = randomInitGroups(distanceMatrix, startIndexesList, coordinates);

            //GREEDY NAIVE ALGORITHM
            long startTime = System.nanoTime();
            GreedyLocalSolver naiveGreedyLocalSolver = new GreedyLocalSolver(naiveInstance, GROUPS);
            List<ArrayList<Integer>> naiveGreedyLocalResults = naiveGreedyLocalSolver.run(distanceMatrix);
            naiveGreedyResults[iteration] = solveLocalSearch(naiveGreedyLocalResults, distanceMatrix);
            if (naiveGreedyResults[iteration] < bestNaiveGreedyResult) {
                bestNaiveGreedyResult = naiveGreedyResults[iteration];
                bestNaiveGreedyGroups = preparedGroups;
            }
            naiveGreedyTimes[iteration] = System.nanoTime() - startTime;

            //GREEDY RANDOM ALGORITHM
            startTime = System.nanoTime();
            GreedyLocalSolver randomGreedyLocalSolver = new GreedyLocalSolver(randomInstance, GROUPS);
            List<ArrayList<Integer>> randomGreedyLocalResults = randomGreedyLocalSolver.run(distanceMatrix);
            randomGreedyResults[iteration] = solveLocalSearch(randomGreedyLocalResults, distanceMatrix);
            if (randomGreedyResults[iteration] < bestRandomGreedyResult) {
                bestRandomGreedyResult = randomGreedyResults[iteration];
                bestRandomGreedyGroups = preparedGroups;
            }
            randomGreedyTimes[iteration] = System.nanoTime() - startTime;

            //STEEPEST NAIVE ALGORITHM
            startTime = System.nanoTime();
            SteepestLocalSolver naiveSteepestLocalSolver = new SteepestLocalSolver(naiveInstance, GROUPS);
            List<ArrayList<Integer>> naiveSteepestLocalResults = naiveSteepestLocalSolver.run(distanceMatrix);
            naiveSteepestResults[iteration] = solveLocalSearch(naiveSteepestLocalResults, distanceMatrix);
            if (naiveSteepestResults[iteration] < bestNaiveSteepestResult) {
                bestNaiveSteepestResult = naiveSteepestResults[iteration];
                bestNaiveSteepestGroups = preparedGroups;
            }
            naiveSteepestTimes[iteration] = System.nanoTime() - startTime;

            //STEEPEST RANDOM ALGORITHM
            startTime = System.nanoTime();
            SteepestLocalSolver randomSteepestLocalSolver = new SteepestLocalSolver(randomInstance, GROUPS);
            List<ArrayList<Integer>> randomSteepestLocalResults = randomSteepestLocalSolver.run(distanceMatrix);
            randomSteepestResults[iteration] = solveLocalSearch(randomSteepestLocalResults, distanceMatrix);
            if (randomSteepestResults[iteration] < bestRandomSteepestResult) {
                bestRandomSteepestResult = randomSteepestResults[iteration];
                bestRandomSteepestGroups = preparedGroups;
            }
            randomSteepestTimes[iteration] = System.nanoTime() - startTime;

        }

        // Show groups on graph
        new Drawer().drawInputInstance(coordinates, bestNaiveGreedyGroups);
        new Drawer().drawInputInstance(coordinates, bestNaiveSteepestGroups);
        new Drawer().drawInputInstance(coordinates, bestRandomGreedyGroups);
        new Drawer().drawInputInstance(coordinates, bestRandomSteepestGroups);

        if (SHOW_STATISTICS) {
            System.out.println("Min result for naive greedy = " + bestNaiveGreedyResult);
            System.out.println("Min result for random greedy = " + bestRandomGreedyResult);
            System.out.println("Min result for naive steepest = " + bestNaiveSteepestResult);
            System.out.println("Min result for random steepest = " + bestRandomSteepestResult);

            System.out.println("Mean result for naive greedy = " + Arrays.stream(naiveGreedyResults).average().getAsDouble());
            System.out.println("Mean result for  random greedy = " + Arrays.stream(randomGreedyResults).average().getAsDouble());
            System.out.println("Mean result for naive steepest = " + Arrays.stream(naiveSteepestResults).average().getAsDouble());
            System.out.println("Mean result for random steepest = " + Arrays.stream(randomSteepestResults).average().getAsDouble());

            System.out.println("Max result for naive greedy = " + Arrays.stream(naiveGreedyResults).max().getAsDouble());
            System.out.println("Max result for  random greedy = " + Arrays.stream(randomGreedyResults).max().getAsDouble());
            System.out.println("Max result for naive steepest = " + Arrays.stream(naiveSteepestResults).max().getAsDouble());
            System.out.println("Max result for random steepest = " + Arrays.stream(randomSteepestResults).max().getAsDouble());

            System.out.println("TIMING:");
            System.out.println("Min time for naive greedy = " + Arrays.stream(naiveGreedyTimes).min().getAsDouble());
            System.out.println("Min time for random greedy = " + Arrays.stream(randomGreedyTimes).min().getAsDouble());
            System.out.println("Min time for naive steepest  = " + Arrays.stream(naiveSteepestTimes).min().getAsDouble());
            System.out.println("Min time for random steepest = " + Arrays.stream(randomSteepestTimes).min().getAsDouble());

            System.out.println("Mean time for  naive greedy = " + Arrays.stream(naiveGreedyTimes).average().getAsDouble());
            System.out.println("Mean time for random greedy = " + Arrays.stream(randomGreedyTimes).average().getAsDouble());
            System.out.println("Mean time for naive steepest = " + Arrays.stream(naiveSteepestTimes).average().getAsDouble());
            System.out.println("Mean time for random steepest = " + Arrays.stream(randomSteepestTimes).average().getAsDouble());

            System.out.println("Max time for naive greedy = " + Arrays.stream(naiveGreedyTimes).max().getAsDouble());
            System.out.println("Max time for random greedy = " + Arrays.stream(randomGreedyTimes).max().getAsDouble());
            System.out.println("Max time for naive steepest = " + Arrays.stream(naiveSteepestTimes).max().getAsDouble());
            System.out.println("Max time for random steepest  = " + Arrays.stream(randomSteepestTimes).max().getAsDouble());
        }
    }

    private double solveLocalSearch(List<ArrayList<Integer>> algorithmResults, double[][] distanceMatrix) {
        preparedGroups = new HashSet<>();
        PrimSolver solver = new PrimSolver();
        for (ArrayList<Integer> integers : algorithmResults) {
            int z = 0;
            int[] arr = new int[integers.size()];
            for (Integer i : integers) {
                arr[z] = i;
                z++;
            }
            solver.construct(arr, distanceMatrix);
            solver.constructMeanOfDistance(arr, distanceMatrix);
            System.out.println("Greedy algorithm = " + solver.getMeanOfDistances() + "\n");
            preparedGroups.add(solver.getPath());
        }
        return solver.getMeanOfDistances();
    }

    private HashMap<Integer, HashSet<Integer>> naiveAlgorithm(double[][] distanceMatrix, ArrayList<Integer> startIndexesList, ArrayList<PointCoordinates> coordinates) {
        //NAIVE ALGORITHM
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

        // Calculate sum of MSTs
        preparedGroups = new HashSet<>();
        double meanOfPenalties = 0.0;
        for (Map.Entry<Integer, HashSet<Integer>> group : elementsWithAssignmentToGroups.entrySet()) {
            PrimSolver solver = new PrimSolver();
            solver.construct(group.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            //sumOfPenalties += solver.getPenalties();
            solver.constructMeanOfDistance(group.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            meanOfPenalties += solver.getMeanOfDistances();
            preparedGroups.add(solver.getPath());
        }
        meanOfPenalties = meanOfPenalties / GROUPS;
        System.out.println("Mean of penalties for naive = " + meanOfPenalties);
        return elementsWithAssignmentToGroups;
    }

    private HashMap<Integer, HashSet<Integer>> randomInitGroups(double[][] distanceMatrix, ArrayList<Integer> startIndexesList, ArrayList<PointCoordinates> coordinates) {
        //NAIVE ALGORITHM
        // k-means with static center
        HashMap<Integer, HashSet<Integer>> elementsWithAssignmentToGroups = new HashMap<>();

        // Initialize groups
        for (int i = 0; i < GROUPS; i++) {
            elementsWithAssignmentToGroups.put(i, new HashSet<>());
        }

        // Assign each point to group
        for (PointCoordinates point : coordinates) {
            Random random = new Random();
            elementsWithAssignmentToGroups.get(random.nextInt(GROUPS)).add(point.getID());
        }

        // Calculate sum of MSTs
        HashSet<ArrayList<PointsPath>> preparedRandomGroups = new HashSet<>();
        double meanOfPenalties = 0.0;
        for (Map.Entry<Integer, HashSet<Integer>> group : elementsWithAssignmentToGroups.entrySet()) {
            PrimSolver solver = new PrimSolver();
            solver.construct(group.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            //sumOfPenalties += solver.getPenalties();
            solver.constructMeanOfDistance(group.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            meanOfPenalties += solver.getMeanOfDistances();
            preparedRandomGroups.add(solver.getPath());
        }
        meanOfPenalties = meanOfPenalties / GROUPS;
        System.out.println("Mean of penalties for random init = " + meanOfPenalties);
        return elementsWithAssignmentToGroups;
    }

    private double regretAlgorithm(double[][] distanceMatrix, ArrayList<Integer> startIndexesList, ArrayList<PointCoordinates> coordinates) {
        // Custom assignment
        // Start with not used points list
        HashSet<PointCoordinates> notUsedPoints = new HashSet<>();
        for (PointCoordinates point : coordinates) {
            if (!startIndexesList.contains(point.getID())) {
                notUsedPoints.add(point);
            }
        }
        int notUsedPointsCount = notUsedPoints.size();

        // Set current MST value
        int totalElementsLength = coordinates.size();
        ArrayList<Double> sumOfMSTs = new ArrayList<>();
        HashMap<Integer, ArrayList<Double>> mstValues = new HashMap<>();
        for (int index = 0; index < totalElementsLength; index++) {
            sumOfMSTs.add(0.0);
            ArrayList<Double> list = new ArrayList<>();
            for (int i = 0; i < GROUPS; i++) {
                list.add(0.0);
            }
            mstValues.put(index, list);
        }

        // List of points in groups
        ArrayList<HashSet<Integer>> listOfPoints = new ArrayList<>();

        // Set start indexes' points as already used
        for (int index : startIndexesList) {
            sumOfMSTs.set(index, -10.0);
        }
        for (int index : startIndexesList) {
            HashSet<Integer> set = new HashSet<>();
            set.add(index);
            listOfPoints.add(set);
        }

        // Run in loop until used all points
        int lastChangedGroupID = -1;
        while (notUsedPointsCount > 0) {
            for (PointCoordinates point : coordinates) {
                // Ignore when already used
                if (sumOfMSTs.get(point.getID()) < -2) {
                    continue;
                }

                // Add point to group and calculate MST
                for (int index = 0; index < GROUPS; index++) {
                    if (index == lastChangedGroupID || lastChangedGroupID == -1) {
                        HashSet<Integer> set = (HashSet<Integer>) listOfPoints.get(index).clone();
                        set.add(point.getID());
                        int[] ints = set.stream().mapToInt(Integer::intValue).toArray();

                        PrimSolver solver = new PrimSolver();
                        solver.construct(ints, distanceMatrix);
                        solver.constructMeanOfDistance(ints, distanceMatrix);
                        //mstValues.get(point.getID()).set(index, solver.getPenalties());
                        mstValues.get(point.getID()).set(index, solver.getMeanOfDistances());
                    }
                }

                // Recalculate sum of MSTs
                double minValue = mstValues.get(point.getID()).stream().mapToDouble(Double::doubleValue).min().getAsDouble();
                double sum = mstValues.get(point.getID()).stream().mapToDouble(Double::doubleValue).sum() - GROUPS * minValue;
                sumOfMSTs.set(point.getID(), sum);
            }

            // Add point to group
            double maxValue = sumOfMSTs.stream().mapToDouble(Double::doubleValue).max().getAsDouble();
            int indexOfPointWithMaxValue = sumOfMSTs.indexOf(maxValue);

            double v = mstValues.get(indexOfPointWithMaxValue).stream().mapToDouble(Double::doubleValue).min().getAsDouble();
            lastChangedGroupID = mstValues.get(indexOfPointWithMaxValue).indexOf(v);
            listOfPoints.get(lastChangedGroupID).add(indexOfPointWithMaxValue);

            // Set point as used
            sumOfMSTs.set(indexOfPointWithMaxValue, -10.0);
            notUsedPointsCount--;
        }

        // Calculate sum of penalties for regret algorithm
        double meanPenaltiesRegret = 0.0;
        HashSet<ArrayList<PointsPath>> preparedRegretGroups = new HashSet<>();
        for (HashSet<Integer> group : listOfPoints) {
            //System.out.println(group);
            PrimSolver solver = new PrimSolver();
            solver.construct(group.stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            //sumPenaltiesRegret += solver.getPenalties();
            solver.constructMeanOfDistance(group.stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
            meanPenaltiesRegret += solver.getMeanOfDistances();
            preparedRegretGroups.add(solver.getPath());
        }
        meanPenaltiesRegret = meanPenaltiesRegret / GROUPS;
        System.out.println("Mean of penalties for regret = " + meanPenaltiesRegret);
        return meanPenaltiesRegret;
    }

    private void constructSingleMST(double[][] distanceMatrix, ArrayList<PointCoordinates> coordinates) {

        // Construct single MST
        PrimSolver primSolver = new PrimSolver();
        int[] indexes = new int[coordinates.size()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        primSolver.construct(indexes, distanceMatrix);

        // Remove GROUPS - 1 arches and prepare GROUPS groups
        ArrayList<PointsPath> tempPath = (ArrayList<PointsPath>) primSolver.getPath().clone();
        tempPath.sort(Collections.reverseOrder());

        for (PointsPath p : tempPath.subList(0, GROUPS - 1)) {
            ListIterator<PointsPath> iterator = primSolver.getPath().listIterator();

            while (iterator.hasNext()) {
                if (iterator.next().compareTo(p) == 0) {
                    iterator.remove();
                    break;
                }
            }
        }

        // Recalculate penalties and show it
        primSolver.calculatePenalties();
        System.out.println(GROUPS + " groups with penalties = " + primSolver.getPenalties());
        HashSet<ArrayList<PointsPath>> preparedFinalGroups = new HashSet<>();
        preparedFinalGroups.add(primSolver.getPath());

        // Draw solution as a graph
        new Drawer().drawInputInstance(coordinates, preparedFinalGroups);
    }
}
