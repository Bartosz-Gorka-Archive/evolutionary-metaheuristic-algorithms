package sample;

import java.util.*;

public class GreedyLocalSolver {
    private List<ArrayList<Integer>> groups;
    private int groupsNumber;

    public GreedyLocalSolver(HashMap<Integer, HashSet<Integer>> g, int groupsNumber) {
        this.groupsNumber = groupsNumber;
        this.groups = new ArrayList<>();
        int groupId = 0;
        for (Map.Entry<Integer, HashSet<Integer>> group : g.entrySet()) {
            int[] groupList = group.getValue().stream().mapToInt(Integer::intValue).toArray();
            this.groups.add(new ArrayList<>());
            for (int point : groupList) {
                this.groups.get(groupId).add(point);
            }
            groupId++;
        }
    }

    public List<ArrayList<Integer>> getGroups() {
        return groups;
    }

    public List<ArrayList<Integer>> run(double[][] distanceMatrix) {

        int localMinimumsCount = 0;
        double[] previousMeanOfDistances = new double[groupsNumber];
        int i = 0;
        for (ArrayList<Integer> group : groups) {
            previousMeanOfDistances[i] = meanOfDistances(group, distanceMatrix);
            i++;
        }

        //do as long as points are moving from some groups
        while (localMinimumsCount < groupsNumber) {
            int groupId = 0;
            int pointsCheckedCount = 0;

            //for each group
            for (ArrayList<Integer> group : groups) {
                if (groups.get(groupId).size() < 1) {
                    groupId++;
                    continue;
                }
                Random random = new Random();
                int startPointId = random.nextInt(groups.get(groupId).size());
                while (pointsCheckedCount < distanceMatrix.length) {

                    random = new Random();
                    int targetGroupId = random.nextInt(groupsNumber);
                    //do not move points from group to the same one
                    if (targetGroupId != groupId) {
                        if (groups.get(groupId).size() < 1) {
                            pointsCheckedCount++;
                            continue;
                        }
                        groups.get(targetGroupId).add(new Integer(groups.get(groupId).get(startPointId)));
                        groups.get(groupId).remove(startPointId);
                        double meanOfDistances1 = meanOfDistances(groups.get(targetGroupId), distanceMatrix);
                        double meanOfDistances2 = meanOfDistances(groups.get(groupId), distanceMatrix);

                        if (meanOfDistances1 + meanOfDistances2 <
                                previousMeanOfDistances[targetGroupId] + previousMeanOfDistances[groupId]) {
                            previousMeanOfDistances[targetGroupId] = meanOfDistances1;
                            previousMeanOfDistances[groupId] = meanOfDistances2;
                            //groups = new ArrayList<>(groupsTemp);
                            localMinimumsCount = -1;
                        } else {
                            groups.get(groupId).add(new Integer(groups.get(targetGroupId).get(groups.get(targetGroupId).size() - 1)));
                            groups.get(targetGroupId).remove(groups.get(targetGroupId).size() - 1);
                        }
                        pointsCheckedCount++;

                        if (startPointId > groups.get(groupId).size() - 2) {
                            startPointId = 0;
                        } else {
                            startPointId++;
                        }
                    }
                }
                groupId++;
            }
            //asume that group didn't make any changes (if yes, then localMinimumsCount == -1 so after ++ then it will be 0
            localMinimumsCount++;
        }
        return groups;
    }

    private double meanOfDistances(ArrayList<Integer> group, double[][] distanceMatrix) {
        if (group.size() <= 1) {
            return 0.0;
        }
        double distancesSum = 0.0;
        for (int i : group) {
            for (int j : group) {
                if (i != j) {
                    distancesSum += distanceMatrix[i][j];
                }
            }
        }
        if (Double.isNaN(distancesSum / (2 * (group.size() * group.size())))) {
            System.out.println("aaaaaaaa\n");
        }
        return distancesSum / (2 * (group.size() * group.size()));
    }

}
