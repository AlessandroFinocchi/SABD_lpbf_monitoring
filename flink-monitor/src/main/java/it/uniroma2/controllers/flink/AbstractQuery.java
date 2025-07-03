package it.uniroma2.controllers.flink;

import org.apache.flink.streaming.api.datastream.DataStream;

public abstract class AbstractQuery<T> {
    protected DataStream<T> inputStream;
    protected long startTs;
    protected int run;

    public AbstractQuery(DataStream<T> inputStream, long startTs, int run) {
        this.inputStream = inputStream;
        this.startTs = startTs;
        this.run = run;
    }
}
