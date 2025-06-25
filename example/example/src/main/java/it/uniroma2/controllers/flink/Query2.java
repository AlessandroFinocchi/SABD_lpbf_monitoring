package it.uniroma2.controllers.flink;//package it.uniroma2.controllers.flink;
//
//import it.uniroma2.entities.query.Query1Response;
//import it.uniroma2.entities.query.Query2Response;
//import org.apache.flink.api.common.functions.FlatMapFunction;
//import org.apache.flink.streaming.api.datastream.DataStream;
//
//public class Query2 extends AbstractQuery<Query1Response> {
//
//    public Query2(DataStream<Query1Response> dataStream) {
//        super(dataStream);
//    }
//
//    public DataStream<Query2Response> run() {
//        // Process the stream to calculate the moving average of the last 10 numbers
//        DataStream<Query2Response> query2ResponseDataStream = dataStream
//                .keyBy(value -> 1)
//                .flatMap(new FlatMapFunction<Query2Response, Query2Response>() {
//
//                });
//
//        return query2ResponseDataStream;
//    }
//}
