package it.uniroma2.boundaries;

import org.apache.flink.core.io.SimpleVersionedSerializer;

import java.nio.charset.StandardCharsets;

public class RESTSplitSerializer implements SimpleVersionedSerializer<RESTSplit> {
    private static final int VERSION = 1;

    @Override
    public int getVersion() {
        return VERSION;
    }

    @Override
    public byte[] serialize(RESTSplit split) {
        // Format: splitId + '\n' + endpoint
        return split.splitId().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public RESTSplit deserialize(int version, byte[] serialized) {
        String[] parts = new String(serialized, StandardCharsets.UTF_8).split("\n", 2);
        return new RESTSplit(parts[0], parts[1]);
    }
}