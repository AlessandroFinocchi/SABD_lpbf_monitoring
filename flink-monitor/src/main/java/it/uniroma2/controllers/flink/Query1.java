package it.uniroma2.controllers.flink;

import it.uniroma2.entities.query.Tile;
import it.uniroma2.entities.query.TileQ1;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;

public class Query1 extends AbstractQuery<Tile> {
    public static final int EMPTY_THRESHOLD = 5000;
    public static final int SATURATION_THRESHOLD = 65000;

    public Query1(DataStream<Tile> inputStream) {
        super(inputStream);
    }

    public DataStream<TileQ1> run() {
        // Process the stream to calculate the moving average of the last 10 numbers
        return inputStream.map(new analyzeSaturationMapper());
    }

    /*
     * Checks for points with a temperature greater or equal than SATURATION_THRESHOLD.
     *   When found:
     *       2.2. A counter in the tile is incremented by 1.
     * */
    private static class analyzeSaturationMapper implements MapFunction<Tile, TileQ1> {
        @Override
        public TileQ1 map(Tile tile) {
            TileQ1 tileQ1 = new TileQ1(tile);
            int[][] tileValues = tileQ1.getValues();

            for (int x = 0; x < tile.getSize(); x++) {
                for (int y = 0; y < tile.getSize(); y++) {
                    if (tileValues[x][y] >= SATURATION_THRESHOLD) {
                        tileQ1.incrementSaturatedPoints();
                    }
                }
            }

            return tileQ1;
        }
    }
}
