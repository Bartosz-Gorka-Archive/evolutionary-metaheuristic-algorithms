package sample;

public class InputInstance {
    private int groupNumber;
    private double[][] distanceMatrix;

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
}
