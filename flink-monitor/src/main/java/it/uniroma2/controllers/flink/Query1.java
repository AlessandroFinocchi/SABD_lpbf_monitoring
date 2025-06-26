package it.uniroma2.controllers.flink;

import it.uniroma2.entities.query.Tile;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;

public class Query1 extends AbstractQuery<Tile> {
    public static final int EMPTY_THRESHOLD = 5000;
    public static final int SATURATION_THRESHOLD = 65000;

    public Query1(DataStream<Tile> inputStream) {
        super(inputStream);
    }

    public DataStream<Tile> run() {
        // Process the stream to calculate the moving average of the last 10 numbers
        return inputStream.map(new analyzeSaturationMapper());
    }

    /*
    * Analyze the tile:
    * 1. Checks for points with a temperature less than EMPTY_THRESHOLD.
    *   When found, their temperature is set to 0.
    * 2. Checks for points with a temperature greater or equal than SATURATION_THRESHOLD.
    *   When found:
    *       2.1. Their temperature is set to -1. In this way they can be easily skipped for sequent queries.
    *       2.2. A counter in the tile is incremented by 1.
    * */
    private static class analyzeSaturationMapper implements MapFunction<Tile, Tile> {
        @Override
        public Tile map(Tile tile) {
            int[][] tileValues = tile.getValues();
            for (int x = 0; x < tile.getSize(); x++) {
                for (int y = 0; y < tile.getSize(); y++) {
                    if (tileValues[x][y] > 0 && tileValues[x][y] < EMPTY_THRESHOLD) {
                        tileValues[x][y] = 0;
                    } else if (tileValues[x][y] >= SATURATION_THRESHOLD) {
                        tileValues[x][y] = -1;
                        tile.incrementSaturatedPoints();
                    }
                }
            }
            return tile;
        }
    }
}
