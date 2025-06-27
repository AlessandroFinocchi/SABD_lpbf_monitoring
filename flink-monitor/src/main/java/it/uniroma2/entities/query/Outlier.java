package it.uniroma2.entities.query;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class Outlier implements Clusterable {
    int x, y, value;

    public Outlier(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Outlier{" +
                "x=" + x +
                ", y=" + y +
                ", value=" + value +
                '}';
    }

    @Override
    public double[] getPoint() {
        return new double[]{x, y};
    }
}
