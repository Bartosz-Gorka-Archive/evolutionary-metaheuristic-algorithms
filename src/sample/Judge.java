package sample;

import java.util.HashMap;
import java.util.HashSet;

public class Judge {
    public double calcMeanDistance(HashMap<Integer, HashSet<Integer>> groups, double[][] distanceMatrix) {
        double sumOfDistances = 0.0;
        int totalArcs = 0;

        for (HashSet<Integer> group : groups.values()) {
            int len = group.size();
            int[] indexes = group.stream().mapToInt(Integer::intValue).toArray();

            for (int i = 0; i < len; i++) {
                int ind_i = indexes[i];

                for (int j = i + 1; j < len; j++) {
                    totalArcs += 1;
                    sumOfDistances += distanceMatrix[ind_i][indexes[j]];
                }
            }
        }

        return sumOfDistances / (totalArcs > 0 ? totalArcs : 1);
    }
}