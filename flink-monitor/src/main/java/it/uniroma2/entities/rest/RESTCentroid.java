package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RESTCentroid {
    private final int x;
    private final int y;
    private final int count;

    @JsonCreator
    public RESTCentroid(
            @JsonProperty("x") int x,
            @JsonProperty("y") int y,
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCount() {
        return count;
    }
}
