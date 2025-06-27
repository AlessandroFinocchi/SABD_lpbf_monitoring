package it.uniroma2.entities.query;

import java.util.ArrayList;
import java.util.List;

public class TileQ3 extends TileQ2 {

    List<Centroid> centroids;

    public TileQ3(TileQ2 parent) {
        super(parent);
        this.centroids = new ArrayList<>();
    }

    public void addCentroid(Centroid centroid) {
        this.centroids.add(centroid);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Centroid centroid : this.centroids) {
            sb.append(String.format("(%d,%d,%d) ", centroid.x, centroid.y, centroid.points));
        }
        return "TileQ3{" +
                "seqID=" + this.getSeqID() +
                ", layerID=" + this.getLayerID() +
                ", tileID=" + this.getTileID() +
                ", centroids=[" + sb + "]" +
                '}';
    }
}
