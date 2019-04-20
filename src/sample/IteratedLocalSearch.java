package sample;

import java.util.*;

public class IteratedLocalSearch {
    /**
     * List with duration of each iteration (in nanoseconds)
     */
    private ArrayList<Long> iterationTimes;
    /**
     * How many points should be moved in perturbation
     */
    private int PERTURBATION_CHANGES_NUMBER;
    /**
     * Which version of perturbation we should use. Small when True
     */
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
     * Iterated local search solver
     *
     * @param groups              Basic assignment to groups
     * @param isSmallPerturbation Boolean status is small perturbation. When false use big perturbation
     */
    public IteratedLocalSearch(HashMap<Integer, HashSet<Integer>> groups, Boolean isSmallPerturbation) {
        this.bestGroups = new HashMap<>();
        this.bestPenalties = Double.MAX_VALUE;
        this.isSmallPerturbation = isSmallPerturbation;
        this.iterationTimes = new ArrayList<>();

        int numberOfPoints = 0;
        for (Map.Entry<Integer, HashSet<Integer>> entry : groups.entrySet()) {
            HashSet<Integer> set = new HashSet<>(entry.getValue());
            numberOfPoints += set.size();
            this.bestGroups.put(entry.getKey(), set);
        }

        if (isSmallPerturbation) {
            this.PERTURBATION_CHANGES_NUMBER = (int) (numberOfPoints * 0.02) + 1;
        } else {
            this.PERTURBATION_CHANGES_NUMBER = (int) (numberOfPoints * 0.3) + 1;
        }
    }

    /**
     * Run calculations and prepare new assigns
     *
     * @param distanceMatrix Distances between points
     * @param timeLimit      Time limit in nanoseconds
     */
    public void run(double[][] distanceMatrix, Long timeLimit) {
        long startTime = System.nanoTime();
        do {
            long time = System.nanoTime();
            //init start groups instance
            this.groups = new HashMap<>();
            for (Map.Entry<Integer, HashSet<Integer>> entry : this.bestGroups.entrySet()) {
                HashSet<Integer> set = new HashSet<>(entry.getValue());
                this.groups.put(entry.getKey(), set);
            }

            if (this.isSmallPerturbation) {
                smallGroupPerturbation();
            } else {
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

            this.iterationTimes.add(System.nanoTime() - time);
        } while (System.nanoTime() - startTime < timeLimit);
    }

    /**
     * Execute after assign global best groups to groups usung in single iteration when isSmallPerturbation == true
     */
    private void smallGroupPerturbation() {
        Random rand = new Random();
        int startRandomGroupId, targetRandomGroupId;

        for (int i = 0; i < PERTURBATION_CHANGES_NUMBER; i++) {
            // Get a random group
            do {
                startRandomGroupId = rand.nextInt(this.groups.size());
                targetRandomGroupId = rand.nextInt(this.groups.size());
            } while (startRandomGroupId == targetRandomGroupId || this.groups.get(startRandomGroupId).size() == 1);

            // Get point ID
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
     * To use it, you should first call `calc` method.
     *
     * @return List with duration of each iteration
     */
    public ArrayList<Long> getIterationTimes() {
        return iterationTimes;
    }
}

