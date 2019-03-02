package sample;

public class PointsPath implements Comparable<PointsPath> {
    private int startIndex;
    private int endIndex;
    private double penalties;

    /**
     * Create new point path
     *
     * @param start     Start point index
     * @param end       End point index
     * @param penalties Distance between start and end points
     */
    public PointsPath(int start, int end, double penalties) {
        this.startIndex = start;
        this.endIndex = end;
        this.penalties = penalties;
    }

    /**
     * Override default method and print more details about point
     *
     * @return Start -> End = Distance
     */
    @Override
    public String toString() {
        return this.startIndex + " -> " + this.endIndex + " = " + this.penalties;
    }

    /**
     * Method should return integer
     * This multiply by 1000 isn't amazing but should be enough to compare elements.
     *
     * @param object Object to compare
     * @return Difference in distances
     */
    @Override
    public int compareTo(PointsPath object) {
        return (int) ((this.penalties - object.penalties) * 1_000);
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getEndIndex() {
        return this.endIndex;
    }

    public double getPenalties() {
        return penalties;
    }
}
