package it.uniroma2.controllers.flink;

import it.uniroma2.entities.matrix.Kernel;
import it.uniroma2.entities.query.Outlier;
import it.uniroma2.entities.query.SubTileQ2;
import it.uniroma2.entities.query.TileQ1;
import it.uniroma2.entities.query.TileQ2;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;


public class Query2NaiveProcessFunction extends AbstractQuery<TileQ1> {
    public static final int DEVIATION_THRESHOLD = 6000;
    private static final int WINDOW_SIZE = 3;

    public Query2NaiveProcessFunction(DataStream<TileQ1> inputStream, long startTs) {
        super(inputStream, startTs);
    }

    public DataStream<TileQ2> run() {
        /*
         * Every tile will be used in 3 (WINDOW_SIZE) windows.
         * So, produce a unified stream in which each tile is copied 3 times but with a different value 'depth',
         *   representing the depth that the tile will have in the corresponding window.
         * In particular, in each window, the tile with 'depth' 0 has the information that has to be carried to the output.
         *  */
        DataStream<SubTileQ2> depth0subTiles = inputStream
                .map(new TileQ1SubTileQ2MapFunction(0));

        DataStream<SubTileQ2> depth1subTiles = inputStream
                .map(new TileQ1SubTileQ2MapFunction(1));

        DataStream<SubTileQ2> depth2subTiles = inputStream
                .map(new TileQ1SubTileQ2MapFunction(2));

        DataStream<SubTileQ2> subTiles = depth0subTiles
                .union(depth1subTiles)
                .union(depth2subTiles);

        DataStream<TileQ2> combinedTiles = subTiles
                /*
                 * Divide the stream by a composite key, composed of:
                 *   1. layerID + depth: this combination allows combining the tiles of a layer with the same tile of previous layers,
                 *                       for example: (L:5, D:0), (L:4, D:1), (L:3, D:2)
                 *   2. tileID
                 *   3. printID
                 *  */
                .keyBy(new KeySelector<SubTileQ2, Tuple3<Integer, Integer, String>>() {
                    @Override
                    public Tuple3<Integer, Integer, String> getKey(SubTileQ2 subTileQ2) {
                        return new Tuple3<>(
                                subTileQ2.getLayerID() + subTileQ2.getDepth(),
                                subTileQ2.getTileID(),
                                subTileQ2.getPrintID());
                    }
                })
                .process(new SumAndCountKeyedProcessFunction())
                /*
                 * Cycle through the computed values and find the outliers.
                 *  */
                .map(new MapFunction<SubTileQ2, TileQ2>() {
                    @Override
                    public TileQ2 map(SubTileQ2 input) {
                        TileQ2 output = new TileQ2(input);
                        output.setValues(input.getBaseValues());

                        for (int x = 0; x < input.getSize(); x++) {
                            for (int y = 0; y < input.getSize(); y++) {
                                if (input.getValues()[x][y] >= DEVIATION_THRESHOLD) {
                                    output.addOutlier(new Outlier(x, y, input.getValues()[x][y]));
                                }
                            }
                        }

                        output.setProcessingCompletionTime(System.currentTimeMillis());
                        return output;
                    }
                });

        return combinedTiles;
    }


    private static class TileQ1SubTileQ2MapFunction implements MapFunction<TileQ1, SubTileQ2> {
        int depth;

        public TileQ1SubTileQ2MapFunction(int depth) {
            this.depth = depth;
        }

        @Override
        public SubTileQ2 map(TileQ1 input) {
            SubTileQ2 output = new SubTileQ2(input, this.depth, null);
            if (this.depth == 0) output.setBaseValues(input.getValues());
            return output;
        }
    }

    private static class SumAndCountKeyedProcessFunction extends KeyedProcessFunction<Tuple3<Integer, Integer, String>, SubTileQ2, SubTileQ2> {
        private ValueState<SubTileQ2> baseTile;
        private ValueState<int[][][]> stacked;
        private ValueState<Integer> processed;

        public void open(OpenContext openContext) throws Exception {
            baseTile = getRuntimeContext().getState(new ValueStateDescriptor<>("baseTile", SubTileQ2.class));
            stacked = getRuntimeContext().getState(new ValueStateDescriptor<>("stacked", int[][][].class));
            processed = getRuntimeContext().getState(new ValueStateDescriptor<>("processed", Integer.class));
        }

        @Override
        public void processElement(SubTileQ2 input, KeyedProcessFunction<Tuple3<Integer, Integer, String>, SubTileQ2, SubTileQ2>.Context context, Collector<SubTileQ2> collector) throws Exception {
            // Compute the expected tiles for each window
            // e.g.: the first two layers will have smaller windows
            int expectedTiles = Math.min(WINDOW_SIZE, context.getCurrentKey().f0 + 1);

            // Initialize the processed variable, used to count how many tiles have been processed
            if (processed.value() == null) {
                processed.update(0);
            }

            // If the window is big enough, then update the 3D matrix.
            if (expectedTiles == WINDOW_SIZE) {
                int[][][] stackedValues;
                if (processed.value() == 0) {
                    stackedValues = new int[WINDOW_SIZE][input.getSize()][input.getSize()];
                } else {
                    stackedValues = stacked.value();
                }
                stackedValues[input.getDepth()] = input.getValues();
                stacked.update(stackedValues);
            }

            // When processing the tile with depth 0, then set it as the base tile for the output tile
            if (input.getDepth() == 0) {
                baseTile.update(input);
            }

            // Increment the processed counter
            processed.update(processed.value() + 1);

            // When the window is fully processed or when the base tile has been processed for smaller windows,
            // then collect the output and clear the state
            if (processed.value() == expectedTiles) {
                SubTileQ2 output = baseTile.value();

                if (expectedTiles == WINDOW_SIZE) {
                    output.setValues(computeValues(input, stacked.value()));
                } else {
                    output.setValues(new int[output.getSize()][output.getSize()]);
                }
                collector.collect(output);

                baseTile.clear();
                stacked.clear();
                processed.clear();
            }
        }

        private int[][] computeValues(SubTileQ2 input, int[][][] stacked) {
            int[][] values = new int[input.getSize()][input.getSize()];
            for (int x = 0; x < input.getSize(); x++) {
                for (int y = 0; y < input.getSize(); y++) {

                    if (stacked[0][x][y] <= Query1.EMPTY_THRESHOLD || stacked[0][x][y] >= Query1.SATURATION_THRESHOLD) {
                        continue;
                    }

                    int sumNear = 0;
                    int sumFar = 0;
                    int countNear = 0;
                    int countFar = 0;
                    for (int dx = -Kernel.FAR_DISTANCE_AT_0; dx <= Kernel.FAR_DISTANCE_AT_0; dx++) {
                        for (int dy = -Kernel.FAR_DISTANCE_AT_0; dy <= Kernel.FAR_DISTANCE_AT_0; dy++) {
                            for (int dz = 0; dz < WINDOW_SIZE; dz++) {
                                int target;
                                // Padding
                                if (x + dx < 0 || x + dx >= input.getSize() || y + dy < 0 || y + dy >= input.getSize()) {
                                    target = 0;
                                } else {
                                    target = stacked[dz][x + dx][y + dy];
                                }

                                int distance = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                                if (distance <= Kernel.NEAR_DISTANCE_AT_0) {
                                    sumNear += target;
                                    countNear++;
                                }
                                if (distance > Kernel.NEAR_DISTANCE_AT_0 && distance <= Kernel.FAR_DISTANCE_AT_0) {
                                    sumFar += target;
                                    countFar++;
                                }
                            }
                        }
                    }
                    values[x][y] = Math.abs((sumNear / countNear) - (sumFar / countFar));
                }
            }
            return values;
        }
    }
}
