package it.uniroma2;

import it.uniroma2.boundaries.RESTSource;
import it.uniroma2.controllers.GcRestController;
import it.uniroma2.controllers.QuerySink;
import it.uniroma2.controllers.flink.Preprocess;
import it.uniroma2.controllers.flink.Query1;
import it.uniroma2.controllers.flink.Query2ProcessFunction;
import it.uniroma2.controllers.flink.Query3;
import it.uniroma2.entities.query.Tile;
import it.uniroma2.entities.query.TileQ1;
import it.uniroma2.entities.query.TileQ2;
import it.uniroma2.entities.query.TileQ3;
import it.uniroma2.entities.rest.RESTBatchResponse;
import it.uniroma2.entities.rest.RESTEndResponse;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class QueryExecutor {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(16);

        executeQueries(env, 0);
    }

    /***
     * Executes the flink job
     * @param env to execute the job
     * @param run the number of run, if it's not equal to 0 Sinks are disabled
     */
    public static void executeQueries(StreamExecutionEnvironment env, int run) throws Exception {
        // Get initial DataStream
        long startTs = System.currentTimeMillis();
        String benchId = GcRestController.getBenchId();
        RESTSource httpSource = new RESTSource(benchId);
        DataStream<RESTBatchResponse> batches = env.fromSource(
                        httpSource,
                        WatermarkStrategy.noWatermarks(),
                        "REST-Batches-Source"
                )
                .setParallelism(1)
                .uid("HttpIntegerSourceUID");

        // Preprocess
        Preprocess preprocess = new Preprocess(batches, startTs, run);
        DataStream<Tile> tiles = preprocess.run();

        // Query 1
        Query1 query1 = new Query1(tiles, startTs, run);
        DataStream<TileQ1> saturationTiles = query1.run();

        if(run == 0) {
            QuerySink<TileQ1> q1sink = new QuerySink<>("q1");
            q1sink.send(saturationTiles);
        }

        // // Query 2
        // Query2 query2 = new Query2(saturationTiles, startTs, run);
        // Query2Naive query2 = new Query2Naive(saturationTiles, startTs, run);
        Query2ProcessFunction query2 = new Query2ProcessFunction(saturationTiles, startTs, run);
        // Query2NaiveProcessFunction query2 = new Query2NaiveProcessFunction(saturationTiles, startTs, run);
        DataStream<TileQ2> outlierTiles = query2.run();

        if(run == 0) {
            QuerySink<TileQ2> q2sink = new QuerySink<>("q2");
            q2sink.send(outlierTiles);
        }

        // // Query 3
        Query3 query3 = new Query3(outlierTiles, startTs, run);
        DataStream<TileQ3> centroidTiles = query3.run();

        centroidTiles.map(new MapFunction<TileQ3, TileQ3>() {
            @Override
            public TileQ3 map(TileQ3 tileQ3) throws Exception {
                GcRestController.postResult(tileQ3, 0, benchId);
                return tileQ3;
            }
        });

        if(run == 0) {
            QuerySink<TileQ3> q3sink = new QuerySink<>("q3");
            q3sink.send(centroidTiles);
        }

        env.execute("Flink L-PBF job");

        RESTEndResponse challengerMetrics = GcRestController.endBench(benchId);
        //todo PRINT THEM
        System.out.println("Challenger metrics: " + challengerMetrics);
    }
}