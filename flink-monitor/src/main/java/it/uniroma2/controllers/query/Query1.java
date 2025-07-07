package it.uniroma2.controllers.query;

import it.uniroma2.controllers.MetricsRichMapFunction;
import it.uniroma2.entities.query.Tile;
import it.uniroma2.entities.query.TileQ1;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;

public class Query1 extends AbstractQuery<Tile> {
    public static final int EMPTY_THRESHOLD = 5000;
    public static final int SATURATION_THRESHOLD = 65000;

    public Query1(DataStream<Tile> inputStream, long startTs, int run) {
        super(inputStream, startTs, run);
    }

    /*
     * Checks for points with a temperature greater or equal than SATURATION_THRESHOLD.
     *   When found, a counter in the tile is increased by 1.
     * */
    public DataStream<TileQ1> run() {
        return inputStream.map(new MapFunction<Tile, TileQ1>() {
                    @Override
                    public TileQ1 map(Tile input) {
                        TileQ1 output = new TileQ1(input);

                        // Cycle through all the points and count how many exceed SATURATION_THRESHOLD
                        for (int x = 0; x < input.getSize(); x++) {
                            for (int y = 0; y < input.getSize(); y++) {
                                if (input.getValues()[x][y] >= SATURATION_THRESHOLD) {
                                    output.incrementSaturatedPoints();
                                }
                            }
                        }

                        output.setProcessingCompletionTime(System.currentTimeMillis());
                        return output;
                    }
                })
                .map(new MetricsRichMapFunction<>("q1", this.startTs, this.run))
                .name("Query1");
    }
}
