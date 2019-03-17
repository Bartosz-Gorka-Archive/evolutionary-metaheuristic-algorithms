package sample;

public class PointCoordinates {
    private int ID;
    private int x;
    private int y;

    public PointCoordinates(int ID, int x, int y) {
        this.ID = ID;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getID() {
        return ID;
    }
}
