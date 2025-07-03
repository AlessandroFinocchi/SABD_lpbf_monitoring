package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("0")
public class RESTEndResponse {

    @JsonProperty("count") private final String count;
    @JsonProperty("missing") private final int missing;
    @JsonProperty("throughput") private final String throughput;
    @JsonProperty("latency_mean") private final int latency_mean;
    @JsonProperty("latency_min") private final int latency_min;
    @JsonProperty("latency_max") private final int latency_max;
    @JsonProperty("latency_p99") private final int latency_p99;

    @JsonCreator
    public RESTEndResponse(@JsonProperty("count") String count,
                           @JsonProperty("missing") int missing,
                           @JsonProperty("throughput") String throughput,
                           @JsonProperty("latency_mean") String latencyMean,
                           @JsonProperty("latency_min") String latencyMin,
                           @JsonProperty("latency_max") String latencyMax,
                           @JsonProperty("latency_p99") String latencyP99) {
        this.count = count;
        this.missing = missing;
        this.throughput = throughput;
        latency_mean = parseLatency(latencyMean);
        latency_min = parseLatency(latencyMin);
        latency_max = parseLatency(latencyMax);
        latency_p99 = parseLatency(latencyP99);
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s",
                this.throughput,
                this.latency_min,
                this.latency_mean,
                this.latency_p99,
                this.latency_max
                );
    }

    public String oldToString() {
        return "Challenger metrics{" +
                "\n\tcount=" + this.count +
                "\n\tmissing=" +this.missing +
                "\n\tthroughput=" + this.throughput +
                "\n\tlatency_mean=" + this.latency_mean +
                "\n\tlatency_min=" + this.latency_min +
                "\n\tlatency_max=" + this.latency_max +
                "\n\tlatency_p99=" + this.latency_p99 +
                "\n}";
    }

    public int parseLatency(String latency) {
        if(!latency.contains("ms")) return 0;
        String[] list = latency.split("ms");
        String tmp =  list[0];

        if (!tmp.contains("s")) return Integer.parseInt(tmp);

        list = tmp.split("s");
        int seconds = Integer.parseInt(list[0]);
        int milliseconds = Integer.parseInt(list[1]);

        return 1000 * seconds + milliseconds;
    }

    public String getCount() {
        return count;
    }
    public int getMissing() {
        return missing;
    }
    public String getThroughput() {
        return throughput;
    }
    public int getLatency_mean() {
        return latency_mean;
    }
    public int getLatency_min() {
        return latency_min;
    }
    public int getLatency_max() {
        return latency_max;
    }
    public int getLatency_p99() {
        return latency_p99;
    }
}
