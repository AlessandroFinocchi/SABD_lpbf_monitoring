package it.uniroma2;

import it.uniroma2.boundaries.RESTSource;
import it.uniroma2.controllers.MetricsRichMapFunction;
import it.uniroma2.controllers.QuerySink;
import it.uniroma2.entities.rest.RESTResponse;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {

    public static void main(String[] args) throws Exception {
        testQueries();
    }

    private static void testQueries() throws Exception {
        // Set up the Flink streaming environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//        // End-to-end latency tracking - Negative impact on performance
//        ExecutionConfig cfg = env.getConfig();
//        cfg.setLatencyTrackingInterval(50L); // In ms

        env.setParallelism(1);
        RESTSource httpSource = new RESTSource();
        System.out.println("Prova 2 - STRINGHE");
        DataStream<RESTResponse> batches = env.fromSource(
                        httpSource,
                        WatermarkStrategy.noWatermarks(),
                        "REST-Batches-Source"
                )
                .setParallelism(1);

        DataStream<RESTResponse> strings = batches.map(new MetricsRichMapFunction<RESTResponse>("query_test"))
                .name("RESTResponseToStringWithMetrics");

        QuerySink.send(strings);

        batches.print();

        env.execute("L-PBF Monitoring Job");
    }

    private static void executeQueries() throws Exception {

//        // Set up the Flink streaming environment
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        // Create the data stream from the custom source
//        DataStream<RESTResponse> batches = env.addSource(new RESTSourceFunction());
//
//        test(env, batches);
//        executeQueries(env, batches);
//        // Query 1
//        Query1 query1 = new Query1(batches);
//        DataStream<Query1Response> query1ResponseDataStream =query1.run();
//
//        // Query 2
//        Query2 query2 = new Query2(query1ResponseDataStream);
//        DataStream<Query2Response> query2ResponseDataStream = query2.run();
//
//        env.execute("Flink L-PBF job");
    }
}