package it.uniroma2.controllers;

import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.metrics.Gauge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MetricsRichMapFunction<T> extends RichMapFunction<T, T> {
    private transient double throughput = 0;
    private transient double latency = 0;
    private transient long counter = 0;
    private transient double start;
    private transient PrintWriter writer;
    private final String pipelinePart;

    /***
     *
     * @param pipelinePart: the part of pipeline we are computing metrics for (a query, an operator, a window)
     */
    public MetricsRichMapFunction(String pipelinePart) {
        super();
        this.pipelinePart = pipelinePart;
    }

    @Override
    public void open(OpenContext parameters) throws Exception {
        super.open(parameters);

        System.out.println("OPEN METRICS FOR " + this.pipelinePart);

        getRuntimeContext().getMetricGroup().gauge("throughput-" + this.pipelinePart, (Gauge<Double>) () -> this.throughput);
        getRuntimeContext().getMetricGroup().gauge("latency-" + this.pipelinePart, (Gauge<Double>) () -> this.latency);
        this.start = System.currentTimeMillis();

        try {
            File metricsFile = new File("metrics_" + this.pipelinePart + ".txt");
            FileWriter fileWriter = new FileWriter(metricsFile, true);
            writer = new PrintWriter(fileWriter);
            System.out.println("Metrics file created/opened successfully for " + this.pipelinePart +
                    " in directory " + metricsFile.getAbsolutePath());
        } catch (IOException e) { System.err.println("Error opening the metrics_" + this.pipelinePart +
                ".txt file: " + e.getMessage()); }
    }

    @Override
    public T map(T response) {
        this.counter++;
        double elapsed_millis = System.currentTimeMillis() - this.start;
        double elapsed_sec = elapsed_millis / 1000;
        this.throughput = this.counter / elapsed_sec; // tuple / s
        this.latency = elapsed_millis / this.counter; // ms / tuple

        System.out.println("Processing element #" + this.counter + " for " + this.pipelinePart);

        if (writer != null) {
            writer.println("Throughput: " + this.throughput + " tuples/s");
            writer.println("Latency: " + this.latency + " ms/tuple");
            writer.flush();
            System.out.println("Metrics for " + this.pipelinePart + " written to file.");
        }

        return response;
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
            System.out.println("Metrics file closed for " + this.pipelinePart);
        }

        super.close();
    }
}