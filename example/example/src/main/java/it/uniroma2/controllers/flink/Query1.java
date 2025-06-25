package it.uniroma2.controllers.flink;//package it.uniroma2.controllers.flink;
//
//import it.uniroma2.entities.query.Query1Response;
//import it.uniroma2.entities.rest.BatchResponse;
//import org.apache.flink.api.common.functions.FlatMapFunction;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.util.Collector;
//
//public class Query1 extends AbstractQuery<BatchResponse> {
//
//    public Query1(DataStream<BatchResponse> dataStream) {
//        super(dataStream);
//    }
//
//    public DataStream<Query1Response> run() {
//        // Process the stream to calculate the moving average of the last 10 numbers
//        DataStream<Query1Response> query1ResponseDataStream = dataStream
//                .keyBy(value -> 1)
//                .flatMap(new FlatMapFunction<Query1Response, Query1Response>() {
//
//                    @Override
//                    public void flatMap(Query1Response query1Response, Collector<Query1Response> collector) throws Exception {
//
//                    }
//                });
//
//        return query1ResponseDataStream;
//    }
//}
