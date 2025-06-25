package it.uniroma2.boundaries;

import org.apache.flink.api.connector.source.SplitEnumerator;
import org.apache.flink.api.connector.source.SplitEnumeratorContext;

import javax.annotation.Nullable;
import java.util.List;

public class RESTSplitEnumerator implements SplitEnumerator<RESTSplit, Void> {

    private final SplitEnumeratorContext<RESTSplit> context;
    private boolean assigned = false;

    public RESTSplitEnumerator(SplitEnumeratorContext<RESTSplit> context) {
        this.context = context;
    }

    @Override
    public void start() {
        // no-op
    }

    @Override
    public void handleSplitRequest(int subtaskId, @Nullable String requesterHostname) {
        // no-op
    }

    @Override
    public void addSplitsBack(List<RESTSplit> splits, int subtaskId) {
        // for simplicity, just re-assign immediately
//        context.assignSplits(splits);
    }

    @Override
    public void addReader(int subtaskId) {
        // trigger split assignment on first reader registration
        handleSplitRequest(subtaskId, null);
    }

    @Override
    public Void snapshotState(long checkpointId) {
        return null;  // no state
    }

    @Override
    public void close() {
        // no-op
    }
}
