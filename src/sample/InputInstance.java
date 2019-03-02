package sample;

import java.util.ArrayList;

public class InputInstance {
    private int groupNumber;
    private double[][] distanceMatrix;
    private ArrayList<PointCoordinates> points;

    /**
     * Create new instance.
     * Distance matrix will be clone to prevent changes in original matrix.
     *
     * @param matrix Matrix of distances between points
     */
    public InputInstance(double[][] matrix) {
        this.distanceMatrix = matrix.clone();
    }

    /**
     * Set number of groups in instance
     *
     * @param groupNumber Number of groups
     */
    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }


    /**
     * Set points array in instance state
     *
     * @param coordinates List of points' coordinates
     */
    public void setPoints(ArrayList<PointCoordinates> coordinates) {
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
}
