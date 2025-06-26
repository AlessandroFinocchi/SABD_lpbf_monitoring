package it.uniroma2.controllers.flink;

import it.uniroma2.entities.matrix.Kernel;
import it.uniroma2.entities.query.SubTileQ2;
import it.uniroma2.entities.query.Tile;
import it.uniroma2.entities.query.TileQ1;
import it.uniroma2.utils.MatrixMath;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;


public class Query2 extends AbstractQuery<TileQ1> {

    public Query2(DataStream<TileQ1> inputStream) {
        super(inputStream);
    }

    public DataStream<Tile> run() {
        DataStream<SubTileQ2> depth0subTiles = inputStream
                .map(new TileQ1SubTileQ2MapFunction(0));

        DataStream<SubTileQ2> depth1subTiles = inputStream
                .map(new TileQ1SubTileQ2MapFunction(1));

        DataStream<SubTileQ2> depth2subTiles = inputStream
                .map(new TileQ1SubTileQ2MapFunction(2));

        DataStream<SubTileQ2> subTiles = depth0subTiles
                .union(depth1subTiles)
                .union(depth2subTiles);

        // subTiles.print();

        return null;
    }


    private static class TileQ1SubTileQ2MapFunction implements MapFunction<TileQ1, SubTileQ2> {
        int depth;
        Kernel kernel;

        public TileQ1SubTileQ2MapFunction(int depth) {
            this.depth = depth;
            this.kernel = new Kernel(depth, true);
        }

        @Override
        public SubTileQ2 map(TileQ1 tileQ1) throws Exception {
            SubTileQ2 outputSQ2 = new SubTileQ2(tileQ1);
            outputSQ2.setDepth(this.depth);
            int[][] convInput = outputSQ2.getValues();
            double[][] convKernel = this.kernel.getValues();
            int[][] convolutionResult = MatrixMath.convolutionPadded(convInput, convKernel);
            outputSQ2.setValues(convolutionResult);
            return outputSQ2;
        }
    }
}
