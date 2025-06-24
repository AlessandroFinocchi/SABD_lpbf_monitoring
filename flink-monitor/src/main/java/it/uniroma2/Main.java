package it.uniroma2;

import it.uniroma2.entities.rest.BatchResponse;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws Exception {
        // Set up the Flink streaming environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Create the data stream from the custom source
        DataStream<BatchResponse> batches = env.addSource(new RESTSourceFunction());

        test(env, batches);
//        executeQueries(env, batches);
    }

    private static void executeQueries(StreamExecutionEnvironment env, DataStream<BatchResponse> batches) throws Exception {
//        // Query 1
//        Query1 query1 = new Query1(batches);
//        DataStream<Query1Response> query1ResponseDataStream =query1.run();
//
//        // Query 2
//        Query2 query2 = new Query2(query1ResponseDataStream);
//        DataStream<Query2Response> query2ResponseDataStream =query2.run();
//
//        env.execute("Flink L-PBF job");
    }

    private static void test(StreamExecutionEnvironment env, DataStream<BatchResponse> batches) throws Exception {
        // A queue to keep track of the last 10 values
        SingleOutputStreamOperator<Integer> movingAverageStream = batches
                .map((MapFunction<BatchResponse, Integer>) value -> value.getLayer()*16+value.getTileId())
                .countWindowAll(10)
                .reduce((value1, value2) -> {
                    // Keep the last 10 values in the queue
                    Queue<Integer> valuesQueue = new LinkedList<>();
                    valuesQueue.add(value1);
                    valuesQueue.add(value2);

                    // Calculate average of the last 10 values
                    double sum = 0;
                    for (Integer val : valuesQueue) {
                        sum += val;
                    }
                    return (int) (sum / valuesQueue.size());
                });

        // Print the computed average to the console
        movingAverageStream.print();

        // Execute the Flink job
        env.execute("Flink Moving Average Application");
    }
}