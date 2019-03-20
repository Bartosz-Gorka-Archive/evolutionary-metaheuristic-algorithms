package sample;

import java.util.*;

public class SteepestLocalSolver {
    private List<ArrayList<Integer>> groups;
    private int groupsNumber;

    public List<ArrayList<Integer>> getGroups() {
        return groups;
    }

    public SteepestLocalSolver(HashMap<Integer, HashSet<Integer>> g, int groupsNumber) {
        this.groupsNumber = groupsNumber;
        this.groups = new ArrayList<>();
        int groupId = 0;
        for (Map.Entry<Integer, HashSet<Integer>> group : g.entrySet()) {
            int[] groupList = group.getValue().stream().mapToInt(Integer::intValue).toArray();
            this.groups.add(new ArrayList<>());
            for(int point: groupList) {
                this.groups.get(groupId).add(point);
            }
            groupId++;
        }
    }

    public List<ArrayList<Integer>> run(double[][] distanceMatrix) {

        double[] bestMeanOfDistances = new double[groupsNumber];
        int i = 0;
        for (ArrayList<Integer> group: groups) {
            bestMeanOfDistances[i] = meanOfDistances(group, distanceMatrix);
            i++;
        }
        int srcId = -1, destId = -1, srcGroup = -1, destGroup = -1;

        //do as long as points are moving from some groups
        while(true) {
            if (srcGroup != -1) {
                groups.get(destGroup).add((int)(groups.get(srcGroup).get(srcId)));
                groups.get(srcGroup).remove(srcId);
                srcId = -1; destId = -1; srcGroup = -1; destGroup = -1;
            }
            //for each group
            int group1Id = 0;
            for (ArrayList<Integer> group1 : groups) {
                int group2Id = 0;
                for (ArrayList<Integer> group2 : groups) {
                    for (i = 0; i < group1.size(); i++) {
                        //do not move points from group to the same one
                        if (group1 == group2) {
                            continue;
                        }
                        //move from 1 to 2
                        group2.add((int)(group1.get(i)));
                        group1.remove(i);
                        double meanOfDistances1 = meanOfDistances(group1, distanceMatrix);
                        double meanOfDistances2 = meanOfDistances(group2, distanceMatrix);

                        if (meanOfDistances1 + meanOfDistances2 <
                                bestMeanOfDistances[group1Id] + bestMeanOfDistances[group2Id]) {
                            bestMeanOfDistances[group1Id] = meanOfDistances1;
                            bestMeanOfDistances[group2Id] = meanOfDistances2;
                            srcId = i;
                            destId = group2.size() - 1;
                            srcGroup = group1Id;
                            destGroup = group2Id;
                        }
                        else{
                            group1.add((int)(group2.get(group2.size() - 1)));
                            group2.remove(group2.size() - 1);
                        }
                    }
                    group2Id++;
                }
                group1Id++;
            }
            if (srcGroup != -1) {
                break;
            }
        }
        return groups;
    }

    private double meanOfDistances(ArrayList<Integer> group, double[][] distanceMatrix) {
        if(group.size() <= 1) {
            return 0.0;
        }
        double distancesSum = 0.0;
        for (int i: group) {
            for (int j: group) {
                if (i != j) {
                    distancesSum += distanceMatrix[i][j];
                }
            }
        }
        return distancesSum / (2 * (group.size() * group.size()));
    }

}
