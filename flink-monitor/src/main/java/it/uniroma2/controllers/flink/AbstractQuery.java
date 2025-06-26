package it.uniroma2.controllers.flink;

import org.apache.flink.streaming.api.datastream.DataStream;

public abstract class AbstractQuery<T> {
    DataStream<T> inputStream;

    public AbstractQuery(DataStream<T> inputStream) {
        this.inputStream = inputStream;
    }
}
