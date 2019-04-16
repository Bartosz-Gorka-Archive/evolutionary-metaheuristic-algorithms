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
    private final static int TESTS_NUMBER = 5;
    /**
     * Should we show extra logs and statistics?
     */
    private final static boolean SHOW_STATISTICS = true;
    /**
     * Should we use following algorithms?
     */
    private final static boolean EXECUTE_GREEDY_NAIVE = false;
    private final static boolean EXECUTE_GREEDY_RANDOM = false;
    private final static boolean EXECUTE_STEEPEST_NAIVE = false;
    private final static boolean EXECUTE_STEEPEST_RANDOM = true;
    private final static boolean EXECUTE_STEEPEST_CANDIDATE = false;
    private final static boolean EXECUTE_STEEPEST_CANDIDATE_CACHE = false;
    private final static boolean EXECUTE_STEEPEST_CACHE = false;
    private final static boolean EXECUTE_STEEPEST_MSLS = false;
    private final static boolean EXECUTE_ITERATED_SMALL_PERTURBATION = true;
    private final static boolean EXECUTE_ITERATED_BIG_PERTURBATION = true;

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
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestCacheGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestCandidateCacheGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestCandidateGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestCacheGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestNaiveSteepestCandidateCacheGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestRandomSteepestMSLSGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestRandomSteepestMSLSGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestIteratedSmallPerturbationGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestIteratedSmallPerturbationGroupsMST = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestIteratedBigPerturbationGroupsConnections = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestIteratedBigPerturbationGroupsMST = new HashSet<>();

        double[] naiveGreedyResults = new double[TESTS_NUMBER], naiveSteepestResults = new double[TESTS_NUMBER],
                randomGreedyResults = new double[TESTS_NUMBER], randomSteepestResults = new double[TESTS_NUMBER],
                naiveSteepestCandidateResults = new double[TESTS_NUMBER], naiveSteepestCacheResults = new double[TESTS_NUMBER],
                naiveSteepestCandidateCacheResults = new double[TESTS_NUMBER], randomSteepestMSLSResults = new double[TESTS_NUMBER],
                iteratedSmallPerturbationResults = new double[TESTS_NUMBER], iteratedBigPerturbationResults = new double[TESTS_NUMBER],

                naiveGreedyTimes = new double[TESTS_NUMBER], naiveSteepestTimes = new double[TESTS_NUMBER],
                randomGreedyTimes = new double[TESTS_NUMBER], randomSteepestTimes = new double[TESTS_NUMBER],
                naiveSteepestCandidateTimes = new double[TESTS_NUMBER], naiveSteepestCacheTimes = new double[TESTS_NUMBER],
                naiveSteepestCandidateCacheTimes = new double[TESTS_NUMBER], randomSteepestMSLSTimes = new double[TESTS_NUMBER],
                iteratedSmallPerturbationTimes = new double[TESTS_NUMBER], iteratedBigPerturbationTimes = new double[TESTS_NUMBER];

        double bestNaiveGreedyResult = Double.MAX_VALUE, bestRandomGreedyResult = Double.MAX_VALUE,
                bestNaiveSteepestResult = Double.MAX_VALUE, bestRandomSteepestResult = Double.MAX_VALUE,
                bestNaiveSteepestCandidateResult = Double.MAX_VALUE, bestNaiveSteepestCacheResult = Double.MAX_VALUE,
                bestNaiveSteepestCandidateCacheResult = Double.MAX_VALUE, bestRandomSteepestMSLSResult = Double.MAX_VALUE,
                bestIteratedSmallPerturbationResult = Double.MAX_VALUE, bestIteratedBigPerturbationResult = Double.MAX_VALUE;

        // Using for statistics
        long startTime;

        // Iterations
        for (int iteration = 0; iteration < TESTS_NUMBER; iteration++) {
            System.out.println("Iteration " + (iteration + 1));
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
                SteepestLocalSolver naiveSteepestLocalSolver = new SteepestLocalSolver(naiveInstances, false, CANDIDATES_NUMBER, false);
                naiveSteepestLocalSolver.run(distanceMatrix, 1);
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
                SteepestLocalSolver randomSteepestLocalSolver = new SteepestLocalSolver(randomInstances, false, CANDIDATES_NUMBER, false);
                randomSteepestLocalSolver.run(distanceMatrix, 1);
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
                SteepestLocalSolver naiveSteepestLocalSolver = new SteepestLocalSolver(naiveInstances, true, CANDIDATES_NUMBER, false);
                naiveSteepestLocalSolver.run(distanceMatrix, 1);
                naiveSteepestCandidateResults[iteration] = naiveSteepestLocalSolver.getPenalties();
                if (naiveSteepestLocalSolver.getPenalties() < bestNaiveSteepestCandidateResult) {
                    bestNaiveSteepestCandidateResult = naiveSteepestLocalSolver.getPenalties();
                    bestNaiveSteepestCandidateGroupsMST = castLocalSearchToMSTGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                    bestNaiveSteepestCandidateGroupsConnections = castLocalSearchToConnectionGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                }
                naiveSteepestCandidateTimes[iteration] = System.nanoTime() - startTime;
            }
            if (EXECUTE_STEEPEST_CACHE) {
                startTime = System.nanoTime();
                SteepestLocalSolver naiveSteepestLocalSolver = new SteepestLocalSolver(naiveInstances, false, CANDIDATES_NUMBER, true);
                naiveSteepestLocalSolver.run(distanceMatrix, 1);
                naiveSteepestCacheResults[iteration] = naiveSteepestLocalSolver.getPenalties();
                if (naiveSteepestLocalSolver.getPenalties() < bestNaiveSteepestCacheResult) {
                    bestNaiveSteepestCacheResult = naiveSteepestLocalSolver.getPenalties();
                    bestNaiveSteepestCacheGroupsMST = castLocalSearchToMSTGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                    bestNaiveSteepestCacheGroupsConnections = castLocalSearchToConnectionGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                }
                naiveSteepestCacheTimes[iteration] = System.nanoTime() - startTime;
            }
            if (EXECUTE_STEEPEST_CANDIDATE_CACHE) {
                startTime = System.nanoTime();
                SteepestLocalSolver naiveSteepestLocalSolver = new SteepestLocalSolver(naiveInstances, true, CANDIDATES_NUMBER, true);
                naiveSteepestLocalSolver.run(distanceMatrix, 1);
                naiveSteepestCandidateCacheResults[iteration] = naiveSteepestLocalSolver.getPenalties();
                if (naiveSteepestLocalSolver.getPenalties() < bestNaiveSteepestCandidateCacheResult) {
                    bestNaiveSteepestCandidateCacheResult = naiveSteepestLocalSolver.getPenalties();
                    bestNaiveSteepestCandidateCacheGroupsMST = castLocalSearchToMSTGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                    bestNaiveSteepestCandidateCacheGroupsConnections = castLocalSearchToConnectionGraph(naiveSteepestLocalSolver.getGroups(), distanceMatrix);
                }
                naiveSteepestCandidateCacheTimes[iteration] = System.nanoTime() - startTime;
            }
            if (EXECUTE_STEEPEST_MSLS) {
                startTime = System.nanoTime();
                SteepestLocalSolver randomSteepestLocalSolver = new SteepestLocalSolver(randomInstances, false, CANDIDATES_NUMBER, false);
                randomSteepestLocalSolver.run(distanceMatrix, 10);
                randomSteepestMSLSResults[iteration] = randomSteepestLocalSolver.getBestPenalties();
                if (randomSteepestLocalSolver.getBestPenalties() < bestNaiveSteepestCandidateCacheResult) {
                    bestRandomSteepestMSLSResult = randomSteepestLocalSolver.getBestPenalties();
                    bestRandomSteepestMSLSGroupsMST = castLocalSearchToMSTGraph(randomSteepestLocalSolver.getBestGroups(), distanceMatrix);
                    bestRandomSteepestMSLSGroupsConnections = castLocalSearchToConnectionGraph(randomSteepestLocalSolver.getBestGroups(), distanceMatrix);
                }
                randomSteepestMSLSTimes[iteration] = System.nanoTime() - startTime;
            }
            long bigPertubationTimeLimit = 0L;
            if (EXECUTE_ITERATED_SMALL_PERTURBATION) {
                startTime = System.nanoTime();
                IteratedLocalSolver iteratedLocalSolver = new IteratedLocalSolver(randomInstances);
                iteratedLocalSolver.run(distanceMatrix, 5, true, 0L);
                iteratedSmallPerturbationResults[iteration] = iteratedLocalSolver.getBestPenalties();
                if (iteratedLocalSolver.getBestPenalties() < bestIteratedSmallPerturbationResult) {
                    bestIteratedSmallPerturbationResult = iteratedLocalSolver.getBestPenalties();
                    bestIteratedSmallPerturbationGroupsMST = castLocalSearchToMSTGraph(iteratedLocalSolver.getBestGroups(), distanceMatrix);
                    bestIteratedSmallPerturbationGroupsConnections = castLocalSearchToConnectionGraph(iteratedLocalSolver.getBestGroups(), distanceMatrix);
                    bigPertubationTimeLimit = iteratedLocalSolver.getTimeLimit();
                }
                iteratedSmallPerturbationTimes[iteration] = System.nanoTime() - startTime;
            }
            if (EXECUTE_ITERATED_BIG_PERTURBATION) {
                startTime = System.nanoTime();
                IteratedLocalSolver iteratedLocalSolver = new IteratedLocalSolver(randomInstances);
                iteratedLocalSolver.run(distanceMatrix, 0, false, bigPertubationTimeLimit);
                iteratedBigPerturbationResults[iteration] = iteratedLocalSolver.getBestPenalties();
                if (iteratedLocalSolver.getBestPenalties() < bestIteratedBigPerturbationResult) {
                    bestIteratedBigPerturbationResult = iteratedLocalSolver.getBestPenalties();
                    bestIteratedBigPerturbationGroupsMST = castLocalSearchToMSTGraph(iteratedLocalSolver.getBestGroups(), distanceMatrix);
                    bestIteratedBigPerturbationGroupsConnections = castLocalSearchToConnectionGraph(iteratedLocalSolver.getBestGroups(), distanceMatrix);
                }
                iteratedBigPerturbationTimes[iteration] = System.nanoTime() - startTime;
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

        if (EXECUTE_STEEPEST_CACHE) {
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCacheGroupsMST, "Naive steepest cache", true, true);
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCacheGroupsMST, "Naive steepest cache", true, false);
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCacheGroupsConnections, "Naive steepest cache", false, true);
        }

        if (EXECUTE_STEEPEST_CANDIDATE_CACHE) {
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCandidateCacheGroupsMST, "Naive steepest candidate + cache", true, true);
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCandidateCacheGroupsMST, "Naive steepest candidate + cache", true, false);
            new Drawer().drawInputInstance(coordinates, bestNaiveSteepestCandidateCacheGroupsConnections, "Naive steepest candidate + cache", false, true);
        }

        if (EXECUTE_STEEPEST_MSLS) {
            new Drawer().drawInputInstance(coordinates, bestRandomSteepestMSLSGroupsMST, "Random steepest MSLS", true, true);
            new Drawer().drawInputInstance(coordinates, bestRandomSteepestMSLSGroupsMST, "Random steepest MSLS", true, false);
            new Drawer().drawInputInstance(coordinates, bestRandomSteepestMSLSGroupsConnections, "Random steepest MSLS", false, true);
        }

        if (EXECUTE_ITERATED_SMALL_PERTURBATION) {
            new Drawer().drawInputInstance(coordinates, bestIteratedSmallPerturbationGroupsMST, "Iterated small perturbation", true, true);
            new Drawer().drawInputInstance(coordinates, bestIteratedSmallPerturbationGroupsMST, "Iterated small perturbation", true, false);
            new Drawer().drawInputInstance(coordinates, bestIteratedSmallPerturbationGroupsConnections, "Iterated small perturbation", false, true);
        }

        if (EXECUTE_ITERATED_BIG_PERTURBATION) {
            new Drawer().drawInputInstance(coordinates, bestIteratedBigPerturbationGroupsMST, "Iterated big perturbation", true, true);
            new Drawer().drawInputInstance(coordinates, bestIteratedBigPerturbationGroupsMST, "Iterated big perturbation", true, false);
            new Drawer().drawInputInstance(coordinates, bestIteratedBigPerturbationGroupsConnections, "Iterated big perturbation", false, true);
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
            if (EXECUTE_STEEPEST_CACHE)
                System.out.println("Min result for naive steepest cache = " + bestNaiveSteepestCacheResult);
            if (EXECUTE_STEEPEST_CANDIDATE_CACHE)
                System.out.println("Min result for naive steepest candidate + cache = " + bestNaiveSteepestCandidateCacheResult);
            if (EXECUTE_STEEPEST_MSLS)
                System.out.println("Min result for random steepest MSLS = " + bestRandomSteepestMSLSResult);
            if (EXECUTE_ITERATED_SMALL_PERTURBATION)
                System.out.println("Min result for iterated small perturbation = " + bestIteratedSmallPerturbationResult);
            if (EXECUTE_ITERATED_BIG_PERTURBATION)
                System.out.println("Min result for iterated big perturbation = " + bestIteratedBigPerturbationResult);

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
            if (EXECUTE_STEEPEST_CACHE)
                System.out.println("Mean result for naive steepest cache = " + Arrays.stream(naiveSteepestCacheResults).average().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE_CACHE)
                System.out.println("Mean result for naive steepest candidate + cache = " + Arrays.stream(naiveSteepestCandidateCacheResults).average().getAsDouble());
            if (EXECUTE_STEEPEST_MSLS)
                System.out.println("Mean result for random steepest MSLS = " + Arrays.stream(randomSteepestMSLSResults).average().getAsDouble());
            if (EXECUTE_ITERATED_SMALL_PERTURBATION)
                System.out.println("Mean result for iterated small perturbation = " + Arrays.stream(iteratedSmallPerturbationResults).average().getAsDouble());
            if (EXECUTE_ITERATED_BIG_PERTURBATION)
                System.out.println("Mean result for iterated big perturbation = " + Arrays.stream(iteratedBigPerturbationResults).average().getAsDouble());

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
            if (EXECUTE_STEEPEST_CACHE)
                System.out.println("Max result for naive steepest cache = " + Arrays.stream(naiveSteepestCacheResults).max().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE_CACHE)
                System.out.println("Max result for naive steepest candidate + cache = " + Arrays.stream(naiveSteepestCandidateCacheResults).max().getAsDouble());
            if (EXECUTE_STEEPEST_MSLS)
                System.out.println("Max result for random steepest MSLS = " + Arrays.stream(randomSteepestMSLSResults).max().getAsDouble());
            if (EXECUTE_ITERATED_SMALL_PERTURBATION)
                System.out.println("Max result for iterated small perturbation = " + Arrays.stream(iteratedSmallPerturbationResults).max().getAsDouble());
            if (EXECUTE_ITERATED_BIG_PERTURBATION)
                System.out.println("Max result for iterated big perturbation = " + Arrays.stream(iteratedBigPerturbationResults).max().getAsDouble());

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
            if (EXECUTE_STEEPEST_CACHE)
                System.out.println("Min time for naive steepest cache = " + Arrays.stream(naiveSteepestCacheTimes).min().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE_CACHE)
                System.out.println("Min time for naive steepest candidate + cache = " + Arrays.stream(naiveSteepestCandidateCacheTimes).min().getAsDouble());
            if (EXECUTE_STEEPEST_MSLS)
                System.out.println("Min time for random steepest MSLS = " + Arrays.stream(randomSteepestMSLSTimes).min().getAsDouble());
            if (EXECUTE_ITERATED_SMALL_PERTURBATION)
                System.out.println("Min time for iterated small perturbation = " + Arrays.stream(iteratedSmallPerturbationTimes).min().getAsDouble());
            if (EXECUTE_ITERATED_BIG_PERTURBATION)
                System.out.println("Min time for iterated big perturbation = " + Arrays.stream(iteratedBigPerturbationTimes).min().getAsDouble());

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
            if (EXECUTE_STEEPEST_CACHE)
                System.out.println("Mean time for naive steepest cache = " + Arrays.stream(naiveSteepestCacheTimes).average().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE_CACHE)
                System.out.println("Mean time for naive steepest candidate + cache = " + Arrays.stream(naiveSteepestCandidateCacheTimes).average().getAsDouble());
            if (EXECUTE_STEEPEST_MSLS)
                System.out.println("Mean time for random steepest MSLS = " + Arrays.stream(randomSteepestMSLSTimes).average().getAsDouble());
            if (EXECUTE_ITERATED_SMALL_PERTURBATION)
                System.out.println("Mean time for iterated small perturbation = " + Arrays.stream(iteratedSmallPerturbationTimes).average().getAsDouble());
            if (EXECUTE_ITERATED_BIG_PERTURBATION)
                System.out.println("Mean time for iterated big perturbation = " + Arrays.stream(iteratedBigPerturbationTimes).average().getAsDouble());

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
            if (EXECUTE_STEEPEST_CACHE)
                System.out.println("Max time for naive steepest cache = " + Arrays.stream(naiveSteepestCacheTimes).max().getAsDouble());
            if (EXECUTE_STEEPEST_CANDIDATE_CACHE)
                System.out.println("Max time for naive steepest candidate + cache = " + Arrays.stream(naiveSteepestCandidateCacheTimes).max().getAsDouble());
            if (EXECUTE_STEEPEST_MSLS)
                System.out.println("Max time for random steepest MSLS = " + Arrays.stream(randomSteepestMSLSTimes).max().getAsDouble());
            if (EXECUTE_ITERATED_SMALL_PERTURBATION)
                System.out.println("Max time for iterated small perturbation = " + Arrays.stream(iteratedSmallPerturbationTimes).max().getAsDouble());
            if (EXECUTE_ITERATED_BIG_PERTURBATION)
                System.out.println("Max time for iterated big perturbation = " + Arrays.stream(iteratedBigPerturbationTimes).max().getAsDouble());
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
