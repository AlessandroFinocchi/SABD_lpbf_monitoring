package it.uniroma2.controllers;

import it.uniroma2.entities.rest.RESTResponse;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.time.Duration;

public class QuerySink {
    public static void send(DataStream<RESTResponse> strings) {

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
}
