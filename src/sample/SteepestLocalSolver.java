package sample;

import java.util.*;

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
        this.candidatesNumber= candidatesNumber;
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

            ArrayList<int[]> moves = new ArrayList<>();

            if (isCandidateAlgorithm) {
                // Prepare potential moves
                moves = new ArrayList<>();

                //create temporary distanceMatrix
                double[][] distanceMatrixTemp = new double [distanceMatrix.length][distanceMatrix.length];
                for (int i = 0; i < distanceMatrix.length; i++)
                    for (int j = 0; j < distanceMatrix[i].length; j++)
                        distanceMatrixTemp[i][j] = distanceMatrix[i][j];

                for (Map.Entry<Integer, HashSet<Integer>> entry : this.groups.entrySet()) {
                    for (Integer id : entry.getValue()) { //for each element in group
                        for (int cn = 0; cn < Math.min(candidatesNumber, distanceMatrix.length); cn++) { //add proper number of neighbors
                            double minimunDistance = 999999.0;
                            int minimumId = 0;
                            for (int i = 0; i < distanceMatrixTemp.length; i++) {
                                if (id != i && distanceMatrixTemp[id][i] != -1.0 && distanceMatrixTemp[id][i] < minimunDistance) {
                                    minimunDistance = distanceMatrixTemp[id][i];
                                    minimumId = i;
                                }
                            }
                            for (int groupId : this.groups.keySet()) {
                                if (groupId != entry.getKey() && this.groups.get(groupId).contains(minimumId)) {
                                    int[] record = {id, entry.getKey(), groupId}; //{who, from_group, to_group}
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
                System.out.println(moves.size() + " candidate");
            }
            else {
                // Prepare potential moves
                moves = new ArrayList<>();
                for (Map.Entry<Integer, HashSet<Integer>> entry : this.groups.entrySet()) {
                    for (Integer id : entry.getValue()) {
                        for (int groupId : this.groups.keySet()) {
                            if (groupId != entry.getKey()) {
                                int[] record = {id, entry.getKey(), groupId}; //{who, from_group, to_group}
                                moves.add(record);
                            }
                        }
                    }
                }
                System.out.println(moves.size() + " non candidate");

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
