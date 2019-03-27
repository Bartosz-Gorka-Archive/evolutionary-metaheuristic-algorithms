package sample;

import java.util.*;

/*
 * TODO list
 *  penalties as delta
 *  clever move from group to another group
 */

public class GreedyLocalSolver {
    private HashMap<Integer, HashSet<Integer>> groups;
    private double penalties;

    public GreedyLocalSolver(HashMap<Integer, HashSet<Integer>> groups) {
        this.groups = (HashMap<Integer, HashSet<Integer>>) groups.clone();
    }

    public void run(double[][] distanceMatrix) {
        // Flag - penalties changed
        boolean penaltiesChanged = true;

        while (penaltiesChanged) {
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

            // Random order
            Collections.shuffle(moves);

            // Prepare basic penalties - reference
            this.penalties = new Judge().calcMeanDistance(this.groups, distanceMatrix);

            // Apply move
            for (int[] move : moves) {
                Integer pointID = move[0];
                this.groups.get(move[1]).remove(pointID);
                this.groups.get(move[2]).add(pointID);

                double newPenalties = new Judge().calcMeanDistance(this.groups, distanceMatrix);
                if (newPenalties < this.penalties) {
                    penaltiesChanged = true;
                    break;
                } else {
                    this.groups.get(move[1]).add(pointID);
                    this.groups.get(move[2]).remove(pointID);
                }
            }
        }
    }

    public HashMap<Integer, HashSet<Integer>> getGroups() {
        return this.groups;
    }

    public double getPenalties() {
        return penalties;
    }
}
