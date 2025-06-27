package it.uniroma2.boundaries;

import it.uniroma2.controllers.GcRestController;
import it.uniroma2.entities.rest.RESTResponse;
import org.apache.flink.api.connector.source.ReaderOutput;
import org.apache.flink.api.connector.source.SourceEvent;
import org.apache.flink.api.connector.source.SourceReader;
import org.apache.flink.api.connector.source.SourceReaderContext;
import org.apache.flink.core.io.InputStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RESTSourceReader implements SourceReader<RESTResponse, RESTSplit> {

    private final List<RESTSplit> splits = new ArrayList<>();
    private final String benchId;

    public RESTSourceReader(SourceReaderContext context) {
        try {
            this.benchId = GcRestController.getBenchId();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    public void start() {
        try {
            GcRestController.startBench(benchId);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    public InputStatus pollNext(ReaderOutput<RESTResponse> output) throws Exception {
        RESTResponse batch = GcRestController.getBatch(benchId);
        if(batch == null) return InputStatus.END_OF_INPUT;

        output.collect(batch);

        // simple backoff; you could use context.callAsync(...) for non-blocking
        Thread.sleep(1);
        return InputStatus.MORE_AVAILABLE;
    }

    @Override
    public List<RESTSplit> snapshotState(long checkpointId) {
        // return splits so that on restore they’re re-added
        return new ArrayList<>(splits);
    }

    @Override
    public CompletableFuture<Void> isAvailable() {
        return null;
    }

    @Override
    public void addSplits(List<RESTSplit> newSplits) {
        this.splits.addAll(newSplits);
    }

    @Override
    public void notifyNoMoreSplits() {
        // no-op
    }

    @Override
    public void handleSourceEvents(SourceEvent sourceEvent) {
        // no-op
    }

    @Override
    public void close() throws Exception {
        // no-op
    }
}
