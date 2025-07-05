package it.uniroma2.controllers;

import it.uniroma2.entities.PerformanceElement;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichMapFunction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static it.uniroma2.entities.GeneralConfig.RESULT_METRICS_DIR;
import static it.uniroma2.entities.GeneralConfig.RESULT_METRICS_FILENAME_PREFIX;

public class MetricsRichMapFunction<T> extends RichMapFunction<T, T> {
    private transient PrintWriter writer;
    private final String pipelineStep;
    private final long startTs;
    private final int run;

    /***
     *
     * @param pipelineStep: the part of pipeline we are computing metrics for (a query, an operator, a window)
     * @param startTs: the initial timestamp of the flink job
     * @param run: the number of run for benchmarking
     */
    public MetricsRichMapFunction(String pipelineStep, long startTs, int run) {
        super();
        this.pipelineStep = pipelineStep;
        this.startTs = startTs;
        this.run = run;
    }

    @Override
    public void open(OpenContext parameters) throws Exception {
        super.open(parameters);
        System.out.println("OPEN METRICS FOR " + this.pipelineStep);

        try {
            String metricsPath = RESULT_METRICS_DIR +
                    RESULT_METRICS_FILENAME_PREFIX +
                    String.format("%s_run_%d.csv", this.pipelineStep, this.run);
            File metricsFile = new File(metricsPath);
            FileWriter fileWriter = new FileWriter(metricsFile, true);
            writer = new PrintWriter(fileWriter);
            System.out.println("Metrics file created/opened successfully for " + this.pipelineStep +
                    " in directory " + metricsFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error opening the metrics_" + this.pipelineStep + ".csv file: " + e.getMessage()); }
    }

    @Override
    public T map(T response) {

        if (writer != null) {
            PerformanceElement tile = (PerformanceElement) response;
            double batchId = tile.getSeqID();
            long arrivalTs = tile.getArrivalTs();
            long processingCompletionTime = tile.getProcessingCompletionTime();
            if (processingCompletionTime == 0) throw new RuntimeException("Value processingCompletionTime not set");
//            System.out.println("Processing element #" + batchId + " for " + this.pipelineStep);

            long arrivalTsTrimmed = arrivalTs - this.startTs;
            double processingInterval = processingCompletionTime - startTs;
            double throughput = 1000f * batchId / processingInterval;
            long latency = processingCompletionTime - arrivalTs;

            writer.println(String.format("%d, %d, %.6f, %d", (int) batchId, arrivalTsTrimmed, throughput, latency));
            writer.flush();
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