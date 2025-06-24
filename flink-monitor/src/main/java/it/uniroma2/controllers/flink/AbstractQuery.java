package it.uniroma2.controllers.flink;

import org.apache.flink.streaming.api.datastream.DataStream;

public abstract class AbstractQuery<T> {
    DataStream<T> dataStream;

    public AbstractQuery(DataStream<T> dataStream) {
        this.dataStream = dataStream;
    }
}
