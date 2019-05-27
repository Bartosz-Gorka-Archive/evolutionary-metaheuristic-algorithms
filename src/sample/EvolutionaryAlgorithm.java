package sample;

import javafx.util.Pair;

import java.util.*;

public class EvolutionaryAlgorithm {
    /**
     * Coordinates read-only copy
     */
    private final ArrayList<PointCoordinates> coordinates;
    /**
     * Start point read-only copy
     */
    private final ArrayList<Integer> startIndexesList;
    /**
     * How many solutions we will store in memory
     */
    private int HOW_MANY_SOLUTIONS_REMEMBER;
    /**
     * Memory with best solutions
     */
    private List<Pair<Double, HashMap<Integer, HashSet<Integer>>>> memory;

    /**
     * Evolutionary algorithm with memory
     *
     * @param coordinates        Points list
     * @param startIndexesList   Start indexes - assignment to groups
     * @param solutionsCacheSize Memory size
     */
    public EvolutionaryAlgorithm(ArrayList<PointCoordinates> coordinates, ArrayList<Integer> startIndexesList, int solutionsCacheSize) {
        this.HOW_MANY_SOLUTIONS_REMEMBER = solutionsCacheSize;
        this.memory = new ArrayList<>();
        this.coordinates = coordinates;
        this.startIndexesList = startIndexesList;
    }

    /**
     * Run calculations and prepare new assigns
     *
     * @param distanceMatrix Distances between points
     * @param timeLimit      Time limit in nanoseconds
     */
    public void run(double[][] distanceMatrix, Long timeLimit) {
        // 1. Prepare basic random solutions
        while (this.memory.size() < this.HOW_MANY_SOLUTIONS_REMEMBER) {
            HashMap<Integer, HashSet<Integer>> groupsOfPoints = this.randomInitGroups();
            SolverInterface solver = new SteepestLocalSolver(groupsOfPoints, false, 0, false);
            solver.run(distanceMatrix);

            double penalties = solver.getPenalties();
            HashMap<Integer, HashSet<Integer>> groups = solver.getGroups();

            this.addCandidateWhenBetter(penalties, groups);
        }

        for (int i = 0; i < this.HOW_MANY_SOLUTIONS_REMEMBER; i++) {
            System.out.println(this.memory.get(i).getKey());
        }
    }

    private void addCandidateWhenBetter(double penalties, HashMap<Integer, HashSet<Integer>> groups) {
        // Check contain already this value
        boolean match = this.memory.stream().map(Pair::getKey).anyMatch(x -> x == penalties);
        if (!match) {
            // Sort by key (penalties)
            this.memory.sort(Comparator.comparing(Pair::getKey));

            // If already full
            if (this.memory.size() >= this.HOW_MANY_SOLUTIONS_REMEMBER) {
                // When penalties less than last value
                if (penalties < this.memory.get(this.HOW_MANY_SOLUTIONS_REMEMBER - 1).getKey()) {
                    System.out.println("I will remove " + this.memory.get(this.HOW_MANY_SOLUTIONS_REMEMBER - 1).getKey());
                    this.memory.remove(this.HOW_MANY_SOLUTIONS_REMEMBER - 1);

                    // Add new element
                    System.out.println("Now add " + penalties);
                    this.memory.add(new Pair<>(penalties, groups));
                }
            } else {
                // Add new element
                System.out.println("Add " + penalties);
                this.memory.add(new Pair<>(penalties, groups));
            }
        }
    }

    private HashMap<Integer, HashSet<Integer>> randomInitGroups() {
        HashMap<Integer, HashSet<Integer>> groupsWithPoints = new HashMap<>();
        for (Map.Entry<Integer, HashSet<Integer>> entry : Main.randomInitGroups(this.startIndexesList, this.coordinates).entrySet()) {
            HashSet<Integer> set = new HashSet<>(entry.getValue());
            groupsWithPoints.put(entry.getKey(), set);
        }

        return groupsWithPoints;
    }

    public double getBestPenalties() {
        // Sort by key (penalties)
        this.memory.sort(Comparator.comparing(Pair::getKey));

        // Return value stored in first pair
        return this.memory.get(0).getKey();
    }

    public HashMap<Integer, HashSet<Integer>> getBestGroups() {
        // Sort by key (penalties)
        this.memory.sort(Comparator.comparing(Pair::getKey));

        // Return assignment stored in with pair
        return this.memory.get(0).getValue();

    }
}