package sample;

import java.util.*;

public class IteratedLocalSearch {
    private int PERTURBATION_CHANGES_NUMBER;
    private Boolean isSmallPerturbation;
    /**
     * Assignment to group
     */
    private HashMap<Integer, HashSet<Integer>> groups;
    private HashMap<Integer, HashSet<Integer>> bestGroups;

    /**
     * Mean distance between connections
     */
    private double bestPenalties;

    /**
     * Time limit for big perturbation calculated by small perturbation algorithm
     */
    private long timeLimit;

    /**
     * Iterated local search solver
     *
     * @param groups Basic assignment to groups
     */
    public IteratedLocalSearch(HashMap<Integer, HashSet<Integer>> groups, Boolean isSmallPerturbation) {
        this.bestGroups = new HashMap<>();

        int numberOfpoints = 0;
        for (Map.Entry<Integer, HashSet<Integer>> entry : groups.entrySet()) {
            HashSet<Integer> set = new HashSet<>(entry.getValue());
            numberOfpoints += set.size();
            this.bestGroups.put(entry.getKey(), set);
        }
        this.bestPenalties = Double.MAX_VALUE;
        this.isSmallPerturbation = isSmallPerturbation;
        if (isSmallPerturbation) {
            this.PERTURBATION_CHANGES_NUMBER = (int) (numberOfpoints * 0.02);
        } else {
            this.PERTURBATION_CHANGES_NUMBER = (int) (numberOfpoints * 0.3);
        }
    }

    /**
     * Run calculations and prepare new assigns
     *
     * @param distanceMatrix Distances between points
     */
    public void run(double[][] distanceMatrix, int numberOfIterations, Long timeLimit) {
        long startTime = System.nanoTime();
        int iterationNo = 0;
        while (true) {

            //init start groups instance
            this.groups = new HashMap<>();
            for (Map.Entry<Integer, HashSet<Integer>> entry : this.bestGroups.entrySet()) {
                HashSet<Integer> set = new HashSet<>(entry.getValue());
                this.groups.put(entry.getKey(), set);
            }

            if (iterationNo > 0 && this.isSmallPerturbation) {
                smallGroupPerturbation();
            } else if (iterationNo > 0) {
                bigGroupPerturbation(distanceMatrix);
            }

            SteepestLocalSolver randomSteepestLocalSolver = new SteepestLocalSolver(this.groups, false, 0, false);
            randomSteepestLocalSolver.run(distanceMatrix);
            double penalties = randomSteepestLocalSolver.getPenalties();
            this.groups = randomSteepestLocalSolver.getGroups();

            if (penalties < this.bestPenalties) {
                this.bestPenalties = penalties;
                this.bestGroups = this.groups;
            }

            if (this.isSmallPerturbation && iterationNo >= numberOfIterations) {
                this.timeLimit = System.nanoTime() - startTime;
                break;
            } else if (!this.isSmallPerturbation && System.nanoTime() - startTime >= timeLimit) {
                break;
            }
            iterationNo++;
        }
    }

    /**
     * Execute after assign global best groups to groups usung in single iteration when isSmallPerturbation == true
     */
    private void smallGroupPerturbation() {
        Random rand = new Random();
        int startRandomGroupId, targetRandomGroupId;

        for (int i = 0; i < PERTURBATION_CHANGES_NUMBER; i++) {
            //get a random group
            do {
                startRandomGroupId = rand.nextInt(this.groups.size());
                targetRandomGroupId = rand.nextInt(this.groups.size());

            } while (startRandomGroupId == targetRandomGroupId);

            Integer pointID = this.groups.get(startRandomGroupId).iterator().next();
            this.groups.get(startRandomGroupId).remove(pointID);
            this.groups.get(targetRandomGroupId).add(pointID);
        }
    }

    /**
     * Execute after assign global best groups to groups usung in single iteration when isSmallPerturbation == false
     */
    private void bigGroupPerturbation(double[][] distanceMatrix) {
        List<Integer> destroyedPoints = new ArrayList<>();
        Random rand = new Random();
        int randomGroupId;

        //destroy
        for (int i = 0; i < PERTURBATION_CHANGES_NUMBER; i++) {
            do {
                randomGroupId = rand.nextInt(this.groups.size());
            } while (this.groups.get(randomGroupId).size() == 0);

            Integer pointID = this.groups.get(randomGroupId).iterator().next();
            this.groups.get(randomGroupId).remove(pointID);
            destroyedPoints.add(pointID);
        }

        //repair
        for (Integer point : destroyedPoints) {
            int selectedGroupIndex;
            do {
                selectedGroupIndex = this.groups.entrySet().iterator().next().getKey();
            } while (this.groups.get(selectedGroupIndex).size() == 0);
            double minDistanceValue = Double.MAX_VALUE;

            for (Map.Entry<Integer, HashSet<Integer>> entry : this.groups.entrySet()) {
                if (entry.getValue().size() == 0) {
                    continue;
                }
                int pointIdFromGroup = entry.getValue().iterator().next();
                // Get distance from array
                double distance = distanceMatrix[pointIdFromGroup][point];

                // Check distance is smaller than current stored - if yes => update index
                if (distance < minDistanceValue) {
                    minDistanceValue = distance;
                    selectedGroupIndex = entry.getKey();
                }
            }
            // Add point to selected group
            this.groups.get(selectedGroupIndex).add(point);
        }
    }

    /**
     * To use it, you should first call `calc` method.
     *
     * @return Prepared best groups
     */
    public HashMap<Integer, HashSet<Integer>> getBestGroups() {
        return this.bestGroups;
    }

    /**
     * To use it, you should first call `calc` method.
     *
     * @return Best penalties in assignment
     */
    public double getBestPenalties() {
        return this.bestPenalties;
    }

    /**
     * To use it, you should first execute IteratedLocalSolver with isSmallPerturbation = true.
     *
     * @return time limit for big perturbation algorithm
     */
    public long getTimeLimit() {
        return this.timeLimit;
    }
}

