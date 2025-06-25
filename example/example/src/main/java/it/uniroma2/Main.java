package it.uniroma2;

import it.uniroma2.boundaries.RESTSource;
import it.uniroma2.controllers.GcRestController;
import it.uniroma2.entities.rest.BatchResponse;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {

    public static void testFlink() throws Exception {
        // Set up the Flink streaming environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        RESTSource httpSource = new RESTSource();
        DataStream<Integer> batches = env.fromSource(
                        httpSource,
                        WatermarkStrategy.noWatermarks(),
                        "HTTP-Integer-Source"
                )
                .setParallelism(1)
                .uid("HttpIntegerSourceUID");

        batches.print();

        env.execute("L-PBF Monitoring Job");
    }

    public static void testRest() throws Exception {
        String benchId = GcRestController.getBenchId();
        GcRestController.startBench(benchId);

        BatchResponse b = GcRestController.getBatch(benchId);
        System.out.println(b);
    }

    public static void main(String[] args) throws Exception {
        testFlink();
    }
}