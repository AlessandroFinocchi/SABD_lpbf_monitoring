package it.uniroma2;

import it.uniroma2.boundaries.RESTSource;
import it.uniroma2.controllers.MetricsRichMapFunction;
import it.uniroma2.entities.rest.RESTResponse;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.time.Duration;

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

        DataStream<RESTResponse> strings = batches.map(new MetricsRichMapFunction<>())
                .name("RESTResponseToStringWithMetrics");;

        testSink(strings);

        batches.print();

        env.execute("L-PBF Monitoring Job");
    }

    private static void testSink(DataStream<RESTResponse> strings) {

        final FileSink<RESTResponse> sink = FileSink
                .forRowFormat(new Path("/results/out"), new SimpleStringEncoder<RESTResponse>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Duration.ofMinutes(15))
                                .withInactivityInterval(Duration.ofMinutes(5))
                                .withMaxPartSize(MemorySize.ofMebiBytes(1024))
                                .build())
                .build();

        strings.sinkTo(sink).setParallelism(1);
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