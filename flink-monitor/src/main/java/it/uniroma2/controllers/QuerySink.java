package it.uniroma2.controllers;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.time.Duration;

import static it.uniroma2.QueryExecutor.PARALLELISM_LEV;

public class QuerySink<T> {
    private final String filename;

    public QuerySink(String filename) {
        this.filename = filename;
    }

    public void send(DataStream<T> strings) {

        final FileSink<T> sink = FileSink
                .forRowFormat(new Path("/results/out_" + filename), new SimpleStringEncoder<T>("UTF-8"))
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Duration.ofMinutes(15))
                                .withInactivityInterval(Duration.ofMinutes(5))
                                .withMaxPartSize(MemorySize.ofMebiBytes(1024))
                                .build())
                .build();

        strings.sinkTo(sink).setParallelism(PARALLELISM_LEV);
    }
}
