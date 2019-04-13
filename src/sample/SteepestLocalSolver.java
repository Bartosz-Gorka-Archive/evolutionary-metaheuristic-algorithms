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
    private Boolean isCandidateAlgorithm;
    private int candidatesNumber;

    /**
     * Steepest Local Search solver
     *
     * @param groups Basic assignment to groups
     */
    public SteepestLocalSolver(HashMap<Integer, HashSet<Integer>> groups, Boolean isCandidateAlgorithm, int candidatesNumber) {
        this.groups = new HashMap<>();
        this.isCandidateAlgorithm = isCandidateAlgorithm;
        this.candidatesNumber = candidatesNumber;
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
        int totalElements = distanceMatrix.length;

        while (penaltiesChanged) {
            double bestPenalties = this.penalties;
            int[] bestMove = {-1, -1, -1};
            penaltiesChanged = false;

            ArrayList<int[]> moves;

            if (isCandidateAlgorithm) {
                // Prepare potential moves
                moves = new ArrayList<>();

                // Create temporary distance matrix
                double[][] distanceMatrixTemp = new double[totalElements][totalElements];
                for (int i = 0; i < totalElements; i++) {
                    System.arraycopy(distanceMatrix[i], 0, distanceMatrixTemp[i], 0, totalElements);
                }

                for (Map.Entry<Integer, HashSet<Integer>> entry : this.groups.entrySet()) {
                    for (Integer id : entry.getValue()) {
                        // Add proper number of neighbors
                        for (int cn = 0; cn < Math.min(candidatesNumber, totalElements); cn++) {
                            double minDistance = Double.MAX_VALUE;
                            int minimumId = 0;

                            for (int i = 0; i < totalElements; i++) {
                                double value = distanceMatrixTemp[id][i];
                                if (id != i && value < minDistance && value != -1.0) {
                                    minDistance = value;
                                    minimumId = i;
                                }
                            }

                            for (int groupId : this.groups.keySet()) {
                                if (groupId != entry.getKey() && this.groups.get(groupId).contains(minimumId)) {
                                    // {who, from_group, to_group}
                                    int[] record = {id, entry.getKey(), groupId};
                                    if (!moves.contains(record)) {
                                        moves.add(record);
                                    }
                                    break;
                                }
                            }

                            distanceMatrixTemp[id][minimumId] = -1.0;
                            distanceMatrixTemp[minimumId][id] = -1.0;
                        }
                    }
                }
            } else {
                // Prepare potential moves
                moves = new ArrayList<>();
                for (Map.Entry<Integer, HashSet<Integer>> entry : this.groups.entrySet()) {
                    for (Integer id : entry.getValue()) {
                        for (int groupId : this.groups.keySet()) {
                            if (groupId != entry.getKey()) {
                                // {who, from_group, to_group}
                                int[] record = {id, entry.getKey(), groupId};
                                moves.add(record);
                            }
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
