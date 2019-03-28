package sample;

import java.util.HashMap;
import java.util.HashSet;

public class Judge {
    private int totalArcs;
    private double sumOfDistances;
    private int changedArcs;
    private double changedDistance;

    public double calcMeanDistance(HashMap<Integer, HashSet<Integer>> groups, double[][] distanceMatrix) {
        this.sumOfDistances = 0.0;
        this.totalArcs = 0;

        for (HashSet<Integer> group : groups.values()) {
            int len = group.size();
            int[] indexes = group.stream().mapToInt(Integer::intValue).toArray();

            for (int i = 0; i < len; i++) {
                int ind_i = indexes[i];

                for (int j = i + 1; j < len; j++) {
                    this.totalArcs += 1;
                    this.sumOfDistances += distanceMatrix[ind_i][indexes[j]];
                }
            }
        }

        return this.sumOfDistances / (this.totalArcs > 0 ? this.totalArcs : 1);
    }

    public void calculateChangedDistance(HashMap<Integer, HashSet<Integer>> groups, int[] changes, double[][] distanceMatrix) {
        this.changedDistance = 0.0;
        this.changedArcs = 0;

        // Point ID
        int pointID = changes[0];

        // Check current group
        int[] indexesInMyGroup = groups.get(changes[1]).stream().mapToInt(Integer::intValue).toArray();
        for (int index : indexesInMyGroup) {
            if (index != pointID) {
                this.changedArcs -= 1;
                this.changedDistance -= distanceMatrix[index][pointID];
            }
        }

        // Check new group
        int[] indexesInNewGroup = groups.get(changes[2]).stream().mapToInt(Integer::intValue).toArray();
        for (int index : indexesInNewGroup) {
            if (index != pointID) {
                this.changedArcs += 1;
                this.changedDistance += distanceMatrix[index][pointID];
            }
        }
    }

    public double updateDistance() {
        this.sumOfDistances += this.changedDistance;
        this.totalArcs += this.changedArcs;

        return this.sumOfDistances / (this.totalArcs > 0 ? this.totalArcs : 1);
    }

    public double getChangedDistance() {
        return changedDistance;
    }
}