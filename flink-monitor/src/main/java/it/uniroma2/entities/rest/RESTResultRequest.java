package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RESTResultRequest {
    private final int batchId;
    private final int query;
    private final String printId;
    private final int tileId;
    private final long saturated;
    private final List<RESTCentroid> centroids;

    @JsonCreator
    public RESTResultRequest(
            @JsonProperty("batch_id") int batchId,
            @JsonProperty("query") int query,
            @JsonProperty("print_id") String printId,
            @JsonProperty("tile_id") int tileId,
            @JsonProperty("saturated") long saturated,
            @JsonProperty("centroids") List<RESTCentroid> centroids) {
        this.batchId = batchId;
        this.query = query;
        this.printId = printId;
        this.tileId = tileId;
        this.saturated = saturated;
        this.centroids = centroids;
    }

    @Override
    public String toString() {
        return "QueryResult{" +
                "batchId=" + batchId +
                ", query=" + query +
                ", printId='" + printId + '\'' +
                ", tileId=" + tileId +
                ", saturated=" + saturated +
                ", centroids=" + centroids +
                '}';
    }

    public int getBatchId() {
        return batchId;
    }

    public int getQuery() {
        return query;
    }

    public String getPrintId() {
        return printId;
    }

    public int getTileId() {
        return tileId;
    }

    public long getSaturated() {
        return saturated;
    }

    public List<RESTCentroid> getCentroids() {
        return centroids;
    }
}
