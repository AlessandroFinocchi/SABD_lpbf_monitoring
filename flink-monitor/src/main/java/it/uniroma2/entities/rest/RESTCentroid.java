package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RESTCentroid {
    private final double x;
    private final double y;
    private final int count;

    @JsonCreator
    public RESTCentroid(
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("count") int count) {
        this.x = x;
        this.y = y;
        this.count = count;
    }

    @Override
    public String toString() {
        return "Centroid{" +
                "x=" + x +
                ", y=" + y +
                ", count=" + count +
                '}';
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getCount() {
        return count;
    }
}
