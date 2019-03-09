package sample;

import java.util.ArrayList;
import java.util.Collections;

public class StartPointAssigner {
    private int totalGroups = 0;
    private ArrayList<PointCoordinates> coords;
    private double[][] distanceMatrix;

    public StartPointAssigner(int groups, ArrayList<PointCoordinates> coords, double[][] matrix) {
        this.totalGroups = groups;
        this.coords = coords;
        this.distanceMatrix = matrix;
    }

    public ArrayList<InputInstance> prepareStaticAssign() {
        ArrayList<InputInstance> instances = new ArrayList<>(this.totalGroups);
        for (int i = 0; i < this.totalGroups; i++) {
            instances.add(new InputInstance(i, this.coords.get(i), this.distanceMatrix, this.coords));
        }
        return instances;
    }

    public ArrayList<InputInstance> prepareRandomAssign() {
        // Make random shuffle
        ArrayList<PointCoordinates> allPoints = (ArrayList<PointCoordinates>) this.coords.clone();
        Collections.shuffle(allPoints);

        // Take totalGroups first elements
        ArrayList<InputInstance> instances = new ArrayList<>(this.totalGroups);
        for (int i = 0; i < this.totalGroups; i++) {
            instances.add(new InputInstance(i, allPoints.get(i), this.distanceMatrix, this.coords));
        }
        return instances;
    }
}
