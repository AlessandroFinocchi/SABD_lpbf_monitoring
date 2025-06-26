package it.uniroma2.entities.query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TileQ2 extends TileQ1 {

    protected int seqID;
    protected int layerID;
    protected List<Outlier> outliers;

    // Copy constructor pattern
    // public TileQ2(TileQ1 parent) {
    //     super(parent.size, parent.seqID, parent.printID, parent.layerID, parent.tileID, parent.values, parent.saturatedPoints);
    //     this.outliers = new ArrayList<>();
    // }

    public TileQ2(int size, String printID, int tileID) {
        super(size, -1, printID, -1, tileID, new int[size][size], -1);
        this.outliers = new ArrayList<>();
    }

    public void setSeqID(int seqID) {
        this.seqID = seqID;
    }

    public void setLayerID(int layerID) {
        this.layerID = layerID;
    }

    public List<Outlier> getOutliers() {
        return outliers;
    }

    public List<Outlier> getOrderedOutliers(int maxPoints) {
        int points = Math.min(maxPoints, this.outliers.size());
        List<Outlier> ordered = new ArrayList<>();
        this.outliers.sort(Comparator.comparingInt(o -> -o.value));
        for (int i = 0; i < points && i < this.outliers.size(); i++) {
            ordered.add(this.outliers.get(i));
        }
        return ordered;
    }

    public void addOutlier(Outlier outlier) {
        this.outliers.add(outlier);
    }

    @Override
    public String toString() {
        String firstOrderedOutliers = "";
        for (Outlier outlier : this.getOrderedOutliers(5)) {
            firstOrderedOutliers += String.format("(%d,%d,%d) ", outlier.x, outlier.y, outlier.value);
        }
        String result = "TileQ2{" +
                "printID='" + printID + '\'' +
                ", seqID=" + seqID +
                ", layerID=" + layerID +
                ", saturatedPoints=" + saturatedPoints +
                ", size=" + size +
                ", seqID=" + seqID +
                ", layerID=" + layerID +
                ", tileID=" + tileID +
                ", firstOrderedOutliers=" + firstOrderedOutliers +
                '}';
        // MatrixMath.saveMatrix(values, result);
        return result;
    }
}
