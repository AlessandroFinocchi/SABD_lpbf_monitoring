package it.uniroma2.controllers.flink;

import org.apache.flink.streaming.api.datastream.DataStream;

public abstract class AbstractQuery<T> {
    protected DataStream<T> inputStream;
    protected long startTs;

    public AbstractQuery(DataStream<T> inputStream, long startTs) {
        this.inputStream = inputStream;
        this.startTs = startTs;
    }
}
