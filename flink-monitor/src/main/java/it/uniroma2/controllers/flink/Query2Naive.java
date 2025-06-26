package it.uniroma2.controllers.flink;

import it.uniroma2.entities.matrix.Kernel;
import it.uniroma2.entities.query.Outlier;
import it.uniroma2.entities.query.SubTileQ2;
import it.uniroma2.entities.query.TileQ1;
import it.uniroma2.entities.query.TileQ2;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;


public class Query2Naive extends AbstractQuery<TileQ1> {
    public static final int DEVIATION_THRESHOLD = 6000;
    private static final int WINDOW_SIZE = 3;

    public Query2Naive(DataStream<TileQ1> inputStream) {
        super(inputStream);
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
                .filter((FilterFunction<SubTileQ2>) subTileQ2 -> {
                    return (subTileQ2.getLayerID() + subTileQ2.getDepth() >= WINDOW_SIZE - 1);
                })
                .keyBy(new KeySelector<SubTileQ2, Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> getKey(SubTileQ2 subTileQ2) {
                        return new Tuple2<>(subTileQ2.getLayerID() + subTileQ2.getDepth(), subTileQ2.getTileID());
                    }
                })
                .window(GlobalWindows.create())
                .trigger(CountTrigger.of(WINDOW_SIZE))
                .apply(new WindowFunction<SubTileQ2, TileQ2, Tuple2<Integer, Integer>, GlobalWindow>() {
                    @Override
                    public void apply(Tuple2<Integer, Integer> key, GlobalWindow globalWindow, Iterable<SubTileQ2> iterable, Collector<TileQ2> collector) {
                        TileQ2 result = null;
                        int[][][] stacked = null;
                        int[][] values = null;

                        for (SubTileQ2 subTileQ2 : iterable) {
                            // System.out.println(String.format("Processing key %s: tile %d, layer %d, depth %d",
                            //                                  key,
                            //                                  subTileQ2.getTileID(),
                            //                                  subTileQ2.getLayerID(),
                            //                                  subTileQ2.getDepth()));

                            if (result == null) {
                                result = new TileQ2(subTileQ2.getSize(), subTileQ2.getPrintID(), subTileQ2.getTileID());
                                stacked = new int[subTileQ2.getSize()][subTileQ2.getSize()][WINDOW_SIZE];
                                values = new int[subTileQ2.getSize()][subTileQ2.getSize()];
                            }

                            if (subTileQ2.getDepth() == 0) {
                                result.setSeqID(subTileQ2.getSeqID());
                                result.setLayerID(subTileQ2.getLayerID());
                                result.setSaturatedPoints(subTileQ2.getSaturatedPoints());
                            }

                            for (int x = 0; x < subTileQ2.getSize(); x++) {
                                for (int y = 0; y < subTileQ2.getSize(); y++) {
                                    stacked[x][y][subTileQ2.getDepth()] = subTileQ2.getValues()[x][y];
                                }
                            }
                        }

                        for (int x = 0; x < result.getSize(); x++) {
                            for (int y = 0; y < result.getSize(); y++) {

                                if (stacked[x][y][0] <= Query1.EMPTY_THRESHOLD || stacked[x][y][0] >= Query1.SATURATION_THRESHOLD)
                                    continue;

                                double sumNear = 0;
                                int sumFar = 0;
                                double countNear = 0;
                                int countFar = 0;
                                for (int dx = -Kernel.FAR_DISTANCE_AT_0; dx <= Kernel.FAR_DISTANCE_AT_0; dx++) {
                                    for (int dy = -Kernel.FAR_DISTANCE_AT_0; dy <= Kernel.FAR_DISTANCE_AT_0; dy++) {
                                        for (int dz = 0; dz < WINDOW_SIZE; dz++) {
                                            int target;
                                            if (x + dx < 0 || x + dx >= stacked.length || y + dy < 0 || y + dy >= stacked.length) {
                                                target = 0; // Padding
                                            } else {
                                                target = Math.max(stacked[x + dx][y + dy][dz], 0); // Masking
                                            }

                                            int distance = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                                            if (distance <= Kernel.NEAR_DISTANCE_AT_0) {
                                                sumNear += target;
                                                countNear++;
                                            } else if (distance <= Kernel.FAR_DISTANCE_AT_0) {
                                                sumFar += target;
                                                countFar++;
                                            }
                                        }
                                    }
                                }
                                values[x][y] = (int) Math.abs(sumNear / countNear - sumFar / countFar);
                            }
                        }
                        result.setValues(values);
                        collector.collect(result);
                    }
                })
                .map(new MapFunction<TileQ2, TileQ2>() {
                    @Override
                    public TileQ2 map(TileQ2 tileQ2) {
                        int[][] values = tileQ2.getValues();
                        for (int x = 0; x < tileQ2.getSize(); x++) {
                            for (int y = 0; y < tileQ2.getSize(); y++) {
                                if (values[x][y] >= DEVIATION_THRESHOLD) {
                                    tileQ2.addOutlier(new Outlier(x, y, values[x][y]));
                                }
                            }
                        }
                        return tileQ2;
                    }
                });

        combinedTiles.print();

        return combinedTiles;
    }


    private static class TileQ1SubTileQ2MapFunction implements MapFunction<TileQ1, SubTileQ2> {
        int depth;

        public TileQ1SubTileQ2MapFunction(int depth) {
            this.depth = depth;
        }

        @Override
        public SubTileQ2 map(TileQ1 tileQ1) {
            SubTileQ2 outputSQ2 = new SubTileQ2(tileQ1);
            outputSQ2.setDepth(this.depth);
            return outputSQ2;
        }
    }
}
