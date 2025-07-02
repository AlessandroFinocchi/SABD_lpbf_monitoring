package it.uniroma2.entities.query;

public class Centroid {
    private double x;
    private double y;
    private int points;

    public Centroid(double x, double y, int points) {
        this.x = x;
        this.y = y;
        this.points = points;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getPoints() {
        return points;
    }
}
