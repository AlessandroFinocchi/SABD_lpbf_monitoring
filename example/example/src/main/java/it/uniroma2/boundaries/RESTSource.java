package it.uniroma2.boundaries;

import org.apache.flink.api.connector.source.*;
import org.apache.flink.core.io.SimpleVersionedSerializer;

public class RESTSource implements Source<Integer, RESTSplit, Void> {

    public RESTSource() {
    }

    @Override
    public Boundedness getBoundedness() {
        return Boundedness.CONTINUOUS_UNBOUNDED;
    }

    @Override
    public SplitEnumerator<RESTSplit, Void> createEnumerator(SplitEnumeratorContext<RESTSplit> enumContext) {
        return new RESTSplitEnumerator(enumContext);
    }

    @Override
    public SplitEnumerator<RESTSplit, Void> restoreEnumerator(
            SplitEnumeratorContext<RESTSplit> enumContext, Void checkpoint) {
        // no state, same as fresh
        return createEnumerator(enumContext);
    }

    @Override
    public SourceReader<Integer, RESTSplit> createReader(SourceReaderContext readerContext) {
        return new RESTSourceReader(readerContext);
    }

    @Override
    public SimpleVersionedSerializer<RESTSplit> getSplitSerializer() {
        return new RESTSplitSerializer();
    }

    @Override
    public SimpleVersionedSerializer<Void> getEnumeratorCheckpointSerializer() {
        // no state, so a trivial serializer
        return new SimpleVersionedSerializer<>() {
            @Override public int getVersion() { return 1; }
            @Override public byte[] serialize(Void checkpoint) { return new byte[0]; }
            @Override public Void deserialize(int version, byte[] serialized) { return null; }
        };
    }
}