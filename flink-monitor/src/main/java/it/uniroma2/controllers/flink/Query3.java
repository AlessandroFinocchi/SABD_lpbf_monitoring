package it.uniroma2.controllers.flink;

import it.uniroma2.controllers.MetricsRichMapFunction;
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

    public Query3(DataStream<TileQ2> inputStream, long startTs) {
        super(inputStream, startTs);
    }

    public DataStream<TileQ3> run() {
        return inputStream
                .map(new MapFunction<TileQ2, TileQ3>() {
                    @Override
                    public TileQ3 map(TileQ2 input) {
                        TileQ3 output = new TileQ3(input);

                        DBSCANClusterer<Outlier> clusterer = new DBSCANClusterer<>(EPS, MIN_POINTS);
                        List<Cluster<Outlier>> clusters = clusterer.cluster(input.getOutliers());

                        // Compute centroids
                        for (Cluster<Outlier> cluster : clusters) {
                            double xCentroid = 0;
                            double yCentroid = 0;
                            int pointsCentroid = 0;
                            for (Outlier outlier : cluster.getPoints()) {
                                xCentroid += outlier.getX();
                                yCentroid += outlier.getY();
                                pointsCentroid++;
                            }
                            xCentroid /= pointsCentroid;
                            yCentroid /= pointsCentroid;
                            output.addCentroid(new Centroid(xCentroid, yCentroid, pointsCentroid));
                        }

                        // StringBuilder sb = new StringBuilder();
                        // sb.append("SeqID: ").append(input.getSeqID()).append(" ");
                        // sb.append("LayerID: ").append(input.getLayerID()).append(" ");
                        // sb.append("TileID: ").append(input.getTileID()).append(" ");
                        // sb.append("Centroids: ");
                        // for (Centroid centroid : output.getCentroids()) {
                        //     sb.append("(").append(centroid.getX()).append(", ").append(centroid.getY()).append(", ").append(centroid.getPoints()).append(") ");
                        // }
                        // System.out.println(sb);

                        output.setProcessingCompletionTime(System.currentTimeMillis());
                        return output;
                    }
                })
                .map(new MetricsRichMapFunction<>("q3", this.startTs))
                .name("Query3");
    }
}
