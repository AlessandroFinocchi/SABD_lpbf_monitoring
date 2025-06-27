package it.uniroma2.entities.query;

public class Centroid {
    int x, y, points;

    public Centroid(int x, int y, int points) {
        this.x = x;
        this.y = y;
        this.points = points;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPoints() {
        return points;
    }
}
