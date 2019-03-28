package sample;

import java.util.*;

public class GreedyLocalSolver {
    private HashMap<Integer, HashSet<Integer>> groups;
    private double penalties;

    public GreedyLocalSolver(HashMap<Integer, HashSet<Integer>> groups) {
        this.groups = (HashMap<Integer, HashSet<Integer>>) groups.clone();
    }

    public void run(double[][] distanceMatrix) {
        // Flag - penalties changed
        boolean penaltiesChanged = true;

        // Prepare basic penalties - reference
        Judge judge = new Judge();
        this.penalties = judge.calcMeanDistance(this.groups, distanceMatrix);

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

            // Apply move
            for (int[] move : moves) {
                judge.calculateChangedDistance(this.groups, move, distanceMatrix);

                // Move decremented current penalties - use it
                if (judge.getChangedDistance() < 0) {
                    // Save changes in groups
                    Integer pointID = move[0];
                    this.groups.get(move[1]).remove(pointID);
                    this.groups.get(move[2]).add(pointID);

                    // Apply changes in penalties
                    this.penalties = judge.updateDistance();

                    // Enable next iteration
                    penaltiesChanged = true;
                    break;
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
