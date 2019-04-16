package sample;

import java.util.*;

public class IteratedLocalSolver {

    private static int SMALL_PERTURBATION_CHANGES_NUMBER = 3;
    /**
     * Assignment to group
     */
    private HashMap<Integer, HashSet<Integer>> groups;
    private HashMap<Integer, HashSet<Integer>> bestGroups;

    /**
     * Mean distance between connections
     */
    private double penalties;
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
    public IteratedLocalSolver(HashMap<Integer, HashSet<Integer>> groups) {
        this.bestGroups = new HashMap<>();

        for (Map.Entry<Integer, HashSet<Integer>> entry : groups.entrySet()) {
            HashSet<Integer> set = new HashSet<>(entry.getValue());
            this.bestGroups.put(entry.getKey(), set);
        }
        this.bestPenalties = Double.MAX_VALUE;
    }

    /**
     * Run calculations and prepare new assigns
     *
     * @param distanceMatrix Distances between points
     */
    public void run(double[][] distanceMatrix, int numberOfIterations, Boolean isSmallPerturbation, Long timeLimit) {
        Long startTime = System.nanoTime();
        int iterationNo = 0;
        while (true) {

            //init start groups instance
            this.groups = new HashMap<>();
            for (Map.Entry<Integer, HashSet<Integer>> entry : this.bestGroups.entrySet()) {
                HashSet<Integer> set = new HashSet<>(entry.getValue());
                this.groups.put(entry.getKey(), set);
            }

            if (isSmallPerturbation) {
                smallGroupPerturbation();
            }
            else {
                bigGroupPerturbation();
            }

            SteepestLocalSolver randomSteepestLocalSolver = new SteepestLocalSolver(this.groups, false, 0, false);
            randomSteepestLocalSolver.run(distanceMatrix, 1);
            this.penalties = randomSteepestLocalSolver.getPenalties();
            this.groups = randomSteepestLocalSolver.getGroups();

            if (this.penalties < this.bestPenalties) {
                this.bestPenalties = this.penalties;
                this.bestGroups = this.groups;
            }
            if (isSmallPerturbation && iterationNo >= numberOfIterations) {
                this.timeLimit = System.nanoTime() - startTime;
                break;
            }
            else if (!isSmallPerturbation && System.nanoTime() - startTime >= timeLimit) {
                break;
            }
            iterationNo++;
        }
    }

    /**
     * Execute after assign global best groups to groups usung in single iteration when isSmallPerturbation == true
     */
    private void smallGroupPerturbation() {

        for (int i = 0; i < SMALL_PERTURBATION_CHANGES_NUMBER; i++) {
            //get a random group
            int startRandomGroupId, targetRandomGroupId;
            Random rand = new Random();
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
    private void bigGroupPerturbation() {
        //TODO
    }

    /**
     * To use it, you should first call `calc` method.
     *
     * @return Prepared new groups
     */
    public HashMap<Integer, HashSet<Integer>> getGroups() {
        return this.groups;
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
     * @return Penalties in assignment
     */
    public double getPenalties() {
        return penalties;
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
