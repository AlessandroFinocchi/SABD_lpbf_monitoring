package it.uniroma2.controllers.flink;

import it.uniroma2.entities.query.Tile;
import it.uniroma2.entities.rest.RESTResponse;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;

public class Preprocess extends AbstractQuery<RESTResponse> {

    public Preprocess(DataStream<RESTResponse> responseStream) {
        super(responseStream);
    }

    // Transforms a stream of RESTResponse into a stream of Tile
    public DataStream<Tile> run() {
        return inputStream.map(new TilePreprocessMapper());
    }

    /*
    This inner class is needed because of:
    Exception in thread "main" org.apache.flink.api.common.InvalidProgramException:
    The implementation of the MapFunction is not serializable.
    The implementation accesses fields of its enclosing class, which is a common reason for non-serializability.
    A common solution is to make the function a proper (non-inner) class, or a static inner class.
    */
    private static class TilePreprocessMapper implements MapFunction<RESTResponse, Tile> {
        @Override
        public Tile map(RESTResponse response) throws Exception {
            return new Tile(
                    response.getSize(),
                    response.getBatchId(),
                    response.getPrintId(),
                    response.getLayer(),
                    response.getTileId(),
                    response.convertTiffToMatrix()
            );
        }
    }
}