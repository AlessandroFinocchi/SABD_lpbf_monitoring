package it.uniroma2.controllers.flink;

import it.uniroma2.controllers.MetricsRichMapFunction;
import it.uniroma2.entities.matrix.Kernel;
import it.uniroma2.entities.query.Outlier;
import it.uniroma2.entities.query.SubTileQ2;
import it.uniroma2.entities.query.TileQ1;
import it.uniroma2.entities.query.TileQ2;
import it.uniroma2.utils.MatrixMath;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;


public class Query2ProcessFunction extends AbstractQuery<TileQ1> {
    public static final int DEVIATION_THRESHOLD = 6000;
    public static final int WINDOW_SIZE = 3;

    public Query2ProcessFunction(DataStream<TileQ1> inputStream, double startTs) {
        super(inputStream, startTs);
    }

    public DataStream<TileQ2> run() {
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
                 * Filter out those tiles which do not have enough layers to fill the window
                 *  */
                .filter(new FilterFunction<SubTileQ2>() {
                    @Override
                    public boolean filter(SubTileQ2 subTileQ2) throws Exception {
                        return (subTileQ2.getLayerID() + subTileQ2.getDepth() >= WINDOW_SIZE - 1);
                    }
                }).name("Q2 Filter")
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
                .process(new ApplyConvolutionKeyedProcessFunction())
                .name("Q2 Window apply")
                /*
                 *
                 *  */
                .map(new MapFunction<SubTileQ2, TileQ2>() {
                    @Override
                    public TileQ2 map(SubTileQ2 input) {
                        TileQ2 output = new TileQ2(input);
                        output.setValues(input.getBaseValues());

                        int[][] values = input.getValues();
                        int[][] baseValues = input.getBaseValues();

                        for (int x = 0; x < input.getSize(); x++) {
                            for (int y = 0; y < input.getSize(); y++) {

                                if (baseValues[x][y] <= Query1.EMPTY_THRESHOLD ||
                                        baseValues[x][y] >= Query1.SATURATION_THRESHOLD)
                                    continue;

                                if (values[x][y] >= DEVIATION_THRESHOLD) {
                                    output.addOutlier(new Outlier(x, y, values[x][y]));
                                }

                            }
                        }

                        output.setProcessingCompletionTime(System.currentTimeMillis());
                        return output;
                    }
                })
                .map(new MetricsRichMapFunction<>("q2_pf", this.startTs))
                .name("Query2_pf");

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

            int[][] convInput = input.getValues();
            double[][] convKernel = new Kernel(depth, true).getValues();
            int[][] convolutionResult = MatrixMath.convolutionPadded(convInput, convKernel);
            output.setValues(convolutionResult);

            return output;
        }
    }

    private static class ApplyConvolutionKeyedProcessFunction extends KeyedProcessFunction<Tuple3<Integer, Integer, String>, SubTileQ2, SubTileQ2> {
        private ValueState<SubTileQ2> baseTile;
        private ValueState<int[][]> outputValues;
        private ValueState<Integer> processed;

        @Override
        public void open(OpenContext openContext) throws Exception {
            baseTile = getRuntimeContext().getState(new ValueStateDescriptor<>("output", SubTileQ2.class));
            outputValues = getRuntimeContext().getState(new ValueStateDescriptor<>("outputValues", int[][].class));
            processed = getRuntimeContext().getState(new ValueStateDescriptor<>("processed", Integer.class));
        }

        @Override
        public void processElement(SubTileQ2 input, KeyedProcessFunction<Tuple3<Integer, Integer, String>, SubTileQ2, SubTileQ2>.Context context, Collector<SubTileQ2> collector) throws Exception {
            if (processed.value() == null) processed.update(0);
            processed.update(processed.value() + 1);

            if (outputValues.value() == null) {
                outputValues.update(input.getValues());
            } else {
                outputValues.update(MatrixMath.addMatrix(outputValues.value(), input.getValues()));
            }

            if (input.getDepth() == 0) {
                baseTile.update(input);
            }

            if (processed.value() == 3) {
                SubTileQ2 output = baseTile.value();
                output.setValues(outputValues.value());
                collector.collect(output);

                baseTile.clear();
                outputValues.clear();
                processed.clear();
            }
        }
    }
}
