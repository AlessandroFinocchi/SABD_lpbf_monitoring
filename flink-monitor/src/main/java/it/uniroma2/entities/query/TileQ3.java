package it.uniroma2.entities.query;

import java.util.ArrayList;
import java.util.List;

public class TileQ3 extends TileQ2 {

    List<Centroid> centroids;

    public List<Centroid> getCentroids() {
        return centroids;
    }

    public TileQ3(int size, String printID, int seqID, int layerID, int tileID, int[][] values, double arrivalTs, int saturatedPoints, List<Outlier> outliers, List<Centroid> centroids) {
        super(size, printID, seqID, layerID, tileID, values, arrivalTs, saturatedPoints, outliers);
        this.centroids = centroids;
    }

    public TileQ3(TileQ2 parent) {
        this(parent.getSize(),
             parent.getPrintID(),
             parent.getSeqID(),
             parent.getLayerID(),
             parent.getTileID(),
             parent.getValues(),
             parent.getArrivalTs(),
             parent.getSaturatedPoints(),
             parent.getOutliers(),
             new ArrayList<>());
    }

    public void addCentroid(Centroid centroid) {
        this.centroids.add(centroid);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < centroids.size(); i++) {
            sb.append(String.format("(%d,%d,%d)",
                                    centroids.get(i).getX(),
                                    centroids.get(i).getY(),
                                    centroids.get(i).getPoints()));
            if (i < centroids.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return String.format("Q3: {printID='%s', seqID=%4d, layerID=%3d, tileID=%2d, centroids=%s}",
                             printID, seqID, layerID, tileID, sb);
    }
}
