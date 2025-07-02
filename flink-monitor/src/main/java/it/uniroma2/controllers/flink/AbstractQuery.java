package it.uniroma2.controllers.flink;

import org.apache.flink.streaming.api.datastream.DataStream;

public abstract class AbstractQuery<T> {
    DataStream<T> inputStream;
    double startTs;

    public AbstractQuery(DataStream<T> inputStream, double startTs) {
        this.inputStream = inputStream;
        this.startTs = startTs;
    }
}
