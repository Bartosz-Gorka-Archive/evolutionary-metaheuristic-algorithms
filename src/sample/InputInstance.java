package sample;

import java.util.ArrayList;

public class InputInstance {
    private int instanceNo;
    private PointCoordinates startPoint;
    private double[][] distanceMatrix;
    private ArrayList<PointCoordinates> points;

    /**
     * Create new instance.
     * Distance matrix and points will be clone to prevent changes in original matrixes.
     *
     * @param instanceNo  Number of instance
     * @param startPoint  Start point
     * @param matrix      Distance matrix
     * @param coordinates Coordinates list
     */
    public InputInstance(int instanceNo, PointCoordinates startPoint, double[][] matrix, ArrayList<PointCoordinates> coordinates) {
        this.instanceNo = instanceNo;
        this.startPoint = startPoint;
        this.distanceMatrix = matrix.clone();
        this.points = (ArrayList<PointCoordinates>) coordinates.clone();
    }

    /**
     * Get point by index value
     *
     * @param index Point index
     * @return Point coordinates
     */
    public PointCoordinates getPoint(int index) {
        return this.points.get(index);
    }

    @Override
    public String toString() {
        return "Instance No: " + this.instanceNo + " with start point = "
                + this.startPoint.getX() + "," + this.startPoint.getY();
    }
}
