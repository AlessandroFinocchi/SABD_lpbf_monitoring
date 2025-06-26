package it.uniroma2;

import it.uniroma2.boundaries.RESTSource;
import it.uniroma2.controllers.flink.Preprocess;
import it.uniroma2.controllers.flink.Query1;
import it.uniroma2.controllers.flink.Query2;
import it.uniroma2.controllers.flink.Query2Naive;
import it.uniroma2.entities.query.Tile;
import it.uniroma2.entities.query.TileQ1;
import it.uniroma2.entities.query.TileQ2;
import it.uniroma2.entities.rest.RESTResponse;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main {

    public static void main(String[] args) throws Exception {
        // Set up the Flink streaming environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        executeQueries(env);
    }

    private static void testQueries() throws Exception {
        // Set up the Flink streaming environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        RESTSource httpSource = new RESTSource();
        DataStream<RESTResponse> batches = env.fromSource(
                        httpSource,
                        WatermarkStrategy.noWatermarks(),
                        "REST-Batches-Source"
                )
                .setParallelism(1)
                .uid("HttpIntegerSourceUID");

        batches.print();

        env.execute("L-PBF Monitoring Job");
    }

    private static void executeQueries(StreamExecutionEnvironment env) throws Exception {
        // Get initial DataStream
        RESTSource httpSource = new RESTSource();
        DataStream<RESTResponse> batches = env.fromSource(
                        httpSource,
                        WatermarkStrategy.noWatermarks(),
                        "REST-Batches-Source"
                )
                .setParallelism(1)
                .uid("HttpIntegerSourceUID");

        // Preprocess
        Preprocess preprocess = new Preprocess(batches);
        DataStream<Tile> tiles = preprocess.run();

        // Query 1
        Query1 query1 = new Query1(tiles);
        DataStream<TileQ1> saturationTiles = query1.run();

        // saturationTiles.print();

        // Query 2
        Query2 query2 = new Query2(saturationTiles);
        DataStream<TileQ2> query2ResponseDataStream = query2.run();


        env.execute("Flink L-PBF job");
    }
}