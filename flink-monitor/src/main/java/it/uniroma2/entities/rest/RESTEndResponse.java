package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("0")
public class RESTEndResponse {

    @JsonProperty("count") private final String count;
    @JsonProperty("missing") private final int missing;
    @JsonProperty("throughput") private final int throughput;
    @JsonProperty("latency_mean") private final String latency_mean;
    @JsonProperty("latency_min") private final String latency_min;
    @JsonProperty("latency_max") private final String latency_max;
    @JsonProperty("latency_p99") private final String latency_p99;

    @JsonCreator
    public RESTEndResponse(@JsonProperty("count") String count,
                           @JsonProperty("missing") int missing,
                           @JsonProperty("throughput") int throughput,
                           @JsonProperty("latency_mean") String latencyMean,
                           @JsonProperty("latency_min") String latencyMin,
                           @JsonProperty("latency_max") String latencyMax,
                           @JsonProperty("latency_p99") String latencyP99) {
        this.count = count;
        this.missing = missing;
        this.throughput = throughput;
        latency_mean = latencyMean;
        latency_min = latencyMin;
        latency_max = latencyMax;
        latency_p99 = latencyP99;
    }

    @Override
    public String toString() {
        return "Performances{" +
                "\n\tcount=" + this.count +
                "\n\tmissing=" +this.missing +
                "\n\tthroughput=" + this.throughput +
                "\n\tlatency_mean=" + this.latency_mean +
                "\n\tlatency_min=" + this.latency_min +
                "\n\tlatency_max=" + this.latency_max +
                "\n\tlatency_p99=" + this.latency_p99 +
                "\n}";
    }

    public String getCount() {
        return count;
    }
    public int getMissing() {
        return missing;
    }
    public int getThroughput() {
        return throughput;
    }
    public String getLatency_mean() {
        return latency_mean;
    }
    public String getLatency_min() {
        return latency_min;
    }
    public String getLatency_max() {
        return latency_max;
    }
    public String getLatency_p99() {
        return latency_p99;
    }
}
