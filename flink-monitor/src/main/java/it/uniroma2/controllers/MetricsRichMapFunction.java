package it.uniroma2.controllers;

import it.uniroma2.entities.PerformanceElement;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MetricsRichMapFunction<T> extends RichMapFunction<T, T> {
    private final transient double startTs;
    private transient PrintWriter writer;
    private final String pipelineStep;

    /***
     *
     * @param pipelineStep: the part of pipeline we are computing metrics for (a query, an operator, a window)
     */
    public MetricsRichMapFunction(String pipelineStep, double startTs) {
        super();
        this.pipelineStep = pipelineStep;
        this.startTs = startTs;
    }

    @Override
    public void open(OpenContext parameters) throws Exception {
        super.open(parameters);
        System.out.println("OPEN METRICS FOR " + this.pipelineStep);

        try {
            File metricsFile = new File("metrics_" + this.pipelineStep + ".csv");
            FileWriter fileWriter = new FileWriter(metricsFile, true);
            writer = new PrintWriter(fileWriter);
            System.out.println("Metrics file created/opened successfully for " + this.pipelineStep +
                    " in directory " + metricsFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error opening the metrics_" + this.pipelineStep + ".csv file: " + e.getMessage()); }
    }

    @Override
    public T map(T response) {

        PerformanceElement tile = (PerformanceElement) response;
        double batchId = tile.getSeqID();
        double arrivalTs = tile.getArrivalTs();
        double processingCompletionTime = tile.getProcessingCompletionTime();
        if (processingCompletionTime == 0) throw new RuntimeException("Value processingCompletionTime not set");
        System.out.println("Processing element #" + batchId + " for " + this.pipelineStep);

        double throughput = batchId / (processingCompletionTime - startTs);
        double latency = processingCompletionTime - arrivalTs;

        if (writer != null) {
            // %d,%.6f,%.6f,%.6f
//            writer.println(String.format("%d, %f, %f, %d", (int) batchId, arrivalTs, throughput, latency));
            writer.println((int)batchId + "," + arrivalTs + "," + throughput + "," + latency);
            writer.flush();
            System.out.println("Metrics for " + this.pipelineStep + " written to file.");
        }
        else {
            System.out.println("Writer null");
        }

        return response;
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
            System.out.println("Metrics file closed for " + this.pipelineStep);
        }

        super.close();
    }
}