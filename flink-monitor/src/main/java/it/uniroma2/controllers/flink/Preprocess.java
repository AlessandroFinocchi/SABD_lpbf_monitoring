package it.uniroma2.controllers.flink;

import it.uniroma2.entities.query.Tile;
import it.uniroma2.entities.rest.RESTBatchResponse;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;

public class Preprocess extends AbstractQuery<RESTBatchResponse> {

    public Preprocess(DataStream<RESTBatchResponse> inputStream) {
        super(inputStream);
    }

    // Transforms a stream of RESTBatchResponse into a stream of Tile
    public DataStream<Tile> run() {
        return inputStream.map(new TilePreprocessMapper()).name("Preprocess");
    }

    /*
    This inner class is needed because of:
    Exception in thread "main" org.apache.flink.api.common.InvalidProgramException:
    The implementation of the MapFunction is not serializable.
    The implementation accesses fields of its enclosing class, which is a common reason for non-serializability.
    A common solution is to make the function a proper (non-inner) class, or a static inner class.
    */
    private static class TilePreprocessMapper implements MapFunction<RESTBatchResponse, Tile> {
        @Override
        public Tile map(RESTBatchResponse response) throws Exception {
            return new Tile(
                    response.getSize(),
                    response.getPrintId(),
                    response.getBatchId(),
                    response.getLayer(),
                    response.getTileId(),
                    response.convertTiffToMatrix()
            );
        }
    }
}