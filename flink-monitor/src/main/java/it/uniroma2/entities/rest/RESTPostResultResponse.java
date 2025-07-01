package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RESTPostResultResponse {

    @JsonProperty("timestamp") private final String timestamp;

    @JsonCreator
    public RESTPostResultResponse(@JsonProperty("timestamp") String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "timestamp=" + timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
