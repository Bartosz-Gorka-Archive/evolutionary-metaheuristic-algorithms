package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputInstance {
    private int groupNumber;
    private List<PointCoordinates> pointCoordinatesList;
    private double[][] distanceMatrix;

    public void initInputInstance(String fileName) {
        try {
            pointCoordinatesList = new ArrayList<>();
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String coordinatesLine;
            while ((coordinatesLine = in.readLine()) != null) {
                int x = Integer.parseInt(coordinatesLine.split(" ", -1)[0]);
                int y = Integer.parseInt(coordinatesLine.split(" ", -1)[1]);
                pointCoordinatesList.add(new PointCoordinates(x, y));
            }
            in.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        createDistanceMatrix();
    }

    private void createDistanceMatrix() {
        distanceMatrix = new double[pointCoordinatesList.size()][pointCoordinatesList.size()];
        int row = 0, col = 0;
        for (PointCoordinates pointCoordinatesRow : pointCoordinatesList) {
            col = 0;
            for (PointCoordinates pointCoordinatesCol : pointCoordinatesList) {
                distanceMatrix[col][row] = Math.sqrt(Math.pow(
                        pointCoordinatesRow.getX() - pointCoordinatesCol.getX(), 2) +
                        Math.pow(pointCoordinatesRow.getY() - pointCoordinatesCol.getY(), 2));
                col++;
            }
            row++;
        }
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public void setDistanceMatrix(double[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }
}
