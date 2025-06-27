package it.uniroma2.controllers.flink;

import it.uniroma2.entities.query.Centroid;
import it.uniroma2.entities.query.Outlier;
import it.uniroma2.entities.query.TileQ2;
import it.uniroma2.entities.query.TileQ3;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.util.List;

public class Query3 extends AbstractQuery<TileQ2> {
    public static int EPS = 20;
    public static int MIN_POINTS = 4;

    public Query3(DataStream<TileQ2> inputStream) {
        super(inputStream);
    }

    public DataStream<TileQ3> run() {
        return inputStream
                .map(new MapFunction<TileQ2, TileQ3>() {
                    @Override
                    public TileQ3 map(TileQ2 tileQ2) throws Exception {
                        TileQ3 tileQ3 = new TileQ3(tileQ2);

                        DBSCANClusterer<Outlier> clusterer = new DBSCANClusterer<>(EPS, MIN_POINTS);

                        List<Cluster<Outlier>> clusters = clusterer.cluster(tileQ2.getOutliers());

                        for (Cluster<Outlier> cluster : clusters) {
                            int xCentroid = 0, yCentroid = 0, pointsCentroid = 0;
                            for (Outlier outlier : cluster.getPoints()) {
                                xCentroid += outlier.getX();
                                yCentroid += outlier.getY();
                                pointsCentroid++;
                            }
                            xCentroid /= pointsCentroid;
                            yCentroid /= pointsCentroid;
                            tileQ3.addCentroid(new Centroid(xCentroid, yCentroid, pointsCentroid));
                        }

                        // StringBuilder sb = new StringBuilder();
                        // sb.append("SeqID: ").append(tileQ2.getSeqID()).append(" ");
                        // sb.append("LayerID: ").append(tileQ2.getLayerID()).append(" ");
                        // sb.append("TileID: ").append(tileQ2.getTileID()).append(" ");
                        // sb.append("Centroids: ");
                        // for (Centroid centroid : tileQ3.getCentroids()) {
                        //     sb.append("(").append(centroid.getX()).append(", ").append(centroid.getY()).append(", ").append(centroid.getPoints()).append(") ");
                        // }
                        // System.out.println(sb);

                        return tileQ3;
                    }
                });
    }
}
