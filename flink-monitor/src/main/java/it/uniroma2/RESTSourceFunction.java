package it.uniroma2;

import it.uniroma2.controllers.GcRestController;
import it.uniroma2.entities.rest.BatchResponse;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class RESTSourceFunction implements SourceFunction<BatchResponse> {
    private volatile boolean isRunning = true;
    private final String benchId;

    public RESTSourceFunction() {
        try {
            this.benchId = GcRestController.getBenchId();
            GcRestController.startBench(benchId);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    @Override
    public void run(SourceContext<BatchResponse> sourceContext) throws Exception {
        BatchResponse batchResponse = GcRestController.getBatch(benchId);
        sourceContext.collect(batchResponse);

        Thread.sleep(100);
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}