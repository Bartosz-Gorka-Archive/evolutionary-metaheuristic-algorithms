package sample;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.*;

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

        HashSet<ArrayList<PointsPath>> bestNaivepreparedGroups = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestPreparedRegretGroups = new HashSet<>();
        HashSet<ArrayList<PointsPath>> bestPreparedRegretGroups2 = new HashSet<>();

        double[] naiveResults = new double[100], regretResults = new double[100], regretResults2 = new double[100],
                regretTimes = new double[100], naiveTimes = new double[100];

        double bestNaiveResult = 999999.0, bestRegretResult = 999999.0, bestRegretResult2 = 999999.0;

        for (int iteration = 0; iteration < 100; iteration++) {

            //NAIVE ALGORITHM
            long naiveStartTime = System.nanoTime();

            // Random start indexes
            HashSet<Integer> startIndexesSet = new HashSet<>();
            int totalElementsLength = coordinates.size();
            Random random = new Random();

            // Generate randomized indexes of start points
            while (startIndexesSet.size() < GROUPS) {
                startIndexesSet.add(random.nextInt(totalElementsLength));
            }
            ArrayList<Integer> startIndexesList = new ArrayList<>(startIndexesSet);

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

            long naiveEndTime = System.nanoTime();
            naiveTimes[iteration] = (naiveEndTime - naiveStartTime) / 100;
            System.out.println("Naive time= " + (naiveEndTime - naiveStartTime) / 1000 + " ms\n");

            // Calculate sum of MSTs
            HashSet<ArrayList<PointsPath>> preparedGroups = new HashSet<>();
            double sumOfPenalties = 0.0;
            for (Map.Entry<Integer, HashSet<Integer>> group : elementsWithAssignmentToGroups.entrySet()) {
                PrimSolver solver = new PrimSolver();
                solver.construct(group.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
                //sumOfPenalties += solver.getPenalties();
                solver.constructMeanOfDistance(group.getValue().stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
                sumOfPenalties += solver.getMeanOfDistances();
                preparedGroups.add(solver.getPath());
            }
            System.out.println("Sum of penalties for naive = " + sumOfPenalties);
            naiveResults[iteration] = sumOfPenalties;
            if(sumOfPenalties < bestNaiveResult) {
                bestNaivepreparedGroups = preparedGroups;
                bestNaiveResult = sumOfPenalties;
            }

            //REGRET ALGORITHM
            long regretStartTime = System.nanoTime();

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

            long regretEndTime = System.nanoTime();
            regretTimes[iteration] = (regretEndTime - regretStartTime) / 1000;
            System.out.println("Regret time= " + (regretEndTime - regretStartTime) / 1000 + " ms\n");

            // Calculate sum of penalties for regret algorithm
            double sumPenaltiesRegret = 0.0;
            HashSet<ArrayList<PointsPath>> preparedRegretGroups = new HashSet<>();
            for (HashSet<Integer> group : listOfPoints) {
                //System.out.println(group);
                PrimSolver solver = new PrimSolver();
                solver.construct(group.stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
                //sumPenaltiesRegret += solver.getPenalties();
                solver.constructMeanOfDistance(group.stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
                sumPenaltiesRegret += solver.getMeanOfDistances();
                preparedRegretGroups.add(solver.getPath());
            }
            System.out.println("Sum of penalties for regret = " + sumPenaltiesRegret);
            regretResults[iteration] = sumPenaltiesRegret;
            if(sumPenaltiesRegret < bestRegretResult) {
                bestPreparedRegretGroups = preparedRegretGroups;
                bestRegretResult = sumPenaltiesRegret;
            }

            //REGRET ALGORITHM 2
            long regret2StartTime = System.nanoTime();

            // Custom assignment
            // Start with not used points list
            List<Integer> notUsedPointsList = new ArrayList<Integer>();
            for (PointCoordinates point : coordinates) {
                if (!startIndexesList.contains(point.getID())) {
                    notUsedPointsList.add(point.getID());
                }
            }
            notUsedPointsCount = notUsedPointsList.size();

            // List of points in groups
            List<List<Integer>> groupList = new ArrayList<>();

            for (int index : startIndexesList) {
                List<Integer> pointsList = new ArrayList<>();
                pointsList.add(index);
                groupList.add(pointsList);
            }

            double[] lastMeanOfDistance = new double[10];
            Arrays.fill(lastMeanOfDistance, -0.0);

            // Run in loop until used all points
            while (notUsedPointsCount > 0) {

                double[] kRegret = new double[10];
                Arrays.fill(kRegret, -1.0);
                int[] worseIndexesForGroup = new int[10];
                Arrays.fill(worseIndexesForGroup, -1);
                List<List<Integer>> naiveNewPointsList = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    naiveNewPointsList.add(new ArrayList<>(groupList.get(i)));
                }

                //assign each not used point to group
                for (Integer notUsedId: notUsedPointsList) {
                    int ID = notUsedId;
                    int selectedGroupIndex = 0, groupNr = 0;
                    double minDistanceValue = 9999999.0;

                    for (int centerPointIndex : startIndexesList) {
                        // Get distance from array
                        double distance = distanceMatrix[centerPointIndex][ID];

                        // Check distance is smaller than current stored - if yes => update index
                        if (distance < minDistanceValue) {
                            minDistanceValue = distance;
                            selectedGroupIndex = groupNr;
                        }
                        groupNr++;
                    }
                    //save max k-regret for the group
                    PrimSolver solver = new PrimSolver();
                    naiveNewPointsList.get(selectedGroupIndex).add(ID);
                    solver.constructMeanOfDistance(naiveNewPointsList.get(selectedGroupIndex).stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);

                    if(kRegret[selectedGroupIndex] < (solver.getMeanOfDistances() - lastMeanOfDistance[selectedGroupIndex])) {
                        kRegret[selectedGroupIndex] = (solver.getMeanOfDistances() - lastMeanOfDistance[selectedGroupIndex]);
                        worseIndexesForGroup[selectedGroupIndex] = ID;
                    }
                }
                //assign only 1 point for each group with biggest regret and remove from notUsedList
                // Set point as used
                int i = 0;
                for (List<Integer> group: groupList) {
                    if(worseIndexesForGroup[i] != -1) {
                        group.add(worseIndexesForGroup[i]);
                        notUsedPointsList.remove(notUsedPointsList.indexOf(worseIndexesForGroup[i]));
                        notUsedPointsCount--;

                        PrimSolver solver = new PrimSolver();
                        solver.constructMeanOfDistance(group.stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
                        lastMeanOfDistance[i] = solver.getMeanOfDistances();
                    }
                    i++;
                }
            }

            long regret2EndTime = System.nanoTime();
            regretTimes[iteration] = (regret2EndTime - regret2StartTime) / 1000;
            System.out.println("Regret2 time= " + (regret2EndTime - regret2StartTime) / 1000 + " ms\n");

            // Calculate sum of penalties for regret2 algorithm
            double sumPenaltiesRegret2 = 0.0;
            HashSet<ArrayList<PointsPath>> preparedRegretGroups2 = new HashSet<>();
            for (List<Integer> group : groupList) {
                PrimSolver solver = new PrimSolver();
                solver.construct(group.stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
                solver.constructMeanOfDistance(group.stream().mapToInt(Integer::intValue).toArray(), distanceMatrix);
                sumPenaltiesRegret2 += solver.getMeanOfDistances();
                preparedRegretGroups2.add(solver.getPath());
            }
            System.out.println("Sum of penalties for regret2 = " + sumPenaltiesRegret2);
            regretResults2[iteration] = sumPenaltiesRegret2;
            if(sumPenaltiesRegret2 < bestRegretResult2) {
                bestPreparedRegretGroups2 = preparedRegretGroups2;
                bestRegretResult2 = sumPenaltiesRegret2;
            }
        }
        // Show groups on graph
        new Drawer().drawInputInstance(coordinates, bestNaivepreparedGroups);
        // Show regret on graph
        //new Drawer().drawInputInstance(coordinates, bestPreparedRegretGroups);
        new Drawer().drawInputInstance(coordinates, bestPreparedRegretGroups2);

        System.out.println("Best result for naive = " + bestNaiveResult);
        System.out.println("Best result for regret = " + bestRegretResult);
        System.out.println("Best result for regret2 = " + bestRegretResult2);
        System.out.println("Mean result for naive = " + Arrays.stream(naiveResults).average().getAsDouble());
        System.out.println("Mean result for regret = " + Arrays.stream(regretResults).average().getAsDouble());
        System.out.println("Mean result for regret2 = " + Arrays.stream(regretResults2).average().getAsDouble());

        System.out.println("Max result for naive = " + Arrays.stream(naiveResults).max().getAsDouble());
        System.out.println("Max result for regret = " + Arrays.stream(regretResults).max().getAsDouble());
        System.out.println("Max result for regret2 = " + Arrays.stream(regretResults2).max().getAsDouble());

        System.out.println("TIMING:");

        System.out.println("Best time for naive = " + Arrays.stream(naiveTimes).min().getAsDouble());
        System.out.println("Best time for regret = " + Arrays.stream(regretTimes).min().getAsDouble());
        System.out.println("Mean time for naive = " + Arrays.stream(naiveTimes).average().getAsDouble());
        System.out.println("Mean time for regret = " + Arrays.stream(regretTimes).average().getAsDouble());
        System.out.println("Max time for naive = " + Arrays.stream(naiveTimes).max().getAsDouble());
        System.out.println("Max time for regret = " + Arrays.stream(regretTimes).max().getAsDouble());

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
        //new Drawer().drawInputInstance(coordinates, preparedFinalGroups);
    }
}
