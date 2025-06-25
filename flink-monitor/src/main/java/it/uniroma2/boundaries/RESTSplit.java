package it.uniroma2.boundaries;

import org.apache.flink.api.connector.source.SourceSplit;

public class RESTSplit implements SourceSplit {
    private final String splitId;

    public RESTSplit(String splitId, String endpoint) {
        this.splitId = splitId;
    }

    @Override
    public String splitId() {
        return splitId;
    }
}
