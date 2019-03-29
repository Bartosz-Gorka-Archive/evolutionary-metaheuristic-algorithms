package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SteepestLocalSolver {
    /**
     * Assignment to group
     */
    private HashMap<Integer, HashSet<Integer>> groups;
    /**
     * Mean distance between connections
     */
    private double penalties;

    /**
     * Steepest Local Search solver
     *
     * @param groups Basic assignment to groups
     */
    public SteepestLocalSolver(HashMap<Integer, HashSet<Integer>> groups) {
        this.groups = new HashMap<>();
        for (Map.Entry<Integer, HashSet<Integer>> entry : groups.entrySet()) {
            HashSet<Integer> set = new HashSet<>(entry.getValue());
            this.groups.put(entry.getKey(), set);
        }
    }

    /**
     * Run calculations and prepare new assigns
     *
     * @param distanceMatrix Distances between points
     */
    public void run(double[][] distanceMatrix) {
        // Flag - penalties changed
        boolean penaltiesChanged = true;

        // Prepare basic penalties - reference
        Judge judge = new Judge();
        this.penalties = judge.calcMeanDistance(this.groups, distanceMatrix);

        while (penaltiesChanged) {
            double bestPenalties = this.penalties;
            int[] bestMove = {-1, -1, -1};
            penaltiesChanged = false;

            // Prepare potential moves
            ArrayList<int[]> moves = new ArrayList<>();
            for (Map.Entry<Integer, HashSet<Integer>> entry : this.groups.entrySet()) {
                for (Integer id : entry.getValue()) {
                    for (int groupId : this.groups.keySet()) {
                        if (groupId != entry.getKey()) {
                            int[] record = {id, entry.getKey(), groupId};
                            moves.add(record);
                        }
                    }
                }
            }

            // Check moves
            for (int[] move : moves) {
                judge.calculateChangedDistance(this.groups, move, distanceMatrix);

                // Verify move
                if (judge.tempMeanDistance() < bestPenalties) {
                    bestPenalties = judge.tempMeanDistance();
                    bestMove[0] = move[0];
                    bestMove[1] = move[1];
                    bestMove[2] = move[2];
                }
            }

            // Apply best move
            if (bestMove[0] != -1) {
                // Save changes in groups
                Integer pointID = bestMove[0];
                this.groups.get(bestMove[1]).remove(pointID);
                this.groups.get(bestMove[2]).add(pointID);

                judge.calculateChangedDistance(this.groups, bestMove, distanceMatrix);
                this.penalties = judge.updateDistance();

                // Enable next iteration
                penaltiesChanged = true;
            }
        }
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
     * @return Penalties in assignment
     */
    public double getPenalties() {
        return penalties;
    }
}
