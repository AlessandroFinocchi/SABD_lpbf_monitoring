package it.uniroma2.entities.query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TileQ2 extends TileQ1 {

    protected List<Outlier> outliers;

    public TileQ2(int size, String printID, int seqID, int layerID, int tileID, int[][] values, long arrivalTs, int saturatedPoints, List<Outlier> outliers) {
        super(size, printID, seqID, layerID, tileID, values, arrivalTs, saturatedPoints);
        this.outliers = outliers;
    }

    public TileQ2(SubTileQ2 input) {
        this(input.getSize(),
             input.getPrintID(),
             input.getSeqID(),
             input.getLayerID(),
             input.getTileID(),
             input.getValues(),
             input.getArrivalTs(),
             input.getSaturatedPoints(),
             new ArrayList<>());
    }

    public List<Outlier> getOutliers() {
        return outliers;
    }

    public List<Outlier> getOrderedOutliers(int maxPoints) {
        int points = Math.min(maxPoints, this.outliers.size());
        List<Outlier> ordered = new ArrayList<>();
        this.outliers.sort(Comparator.comparingInt(o -> -o.getValue()));
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
        List<Outlier> ordered = this.getOrderedOutliers(5);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5 && i < ordered.size(); i++) {
            sb.append(String.format("%d,%d",
                                    (int) ordered.get(i).getX(),
                                    ordered.get(i).getValue()));
            if (i < ordered.size() - 1) {
                sb.append(",");
            }
        }
        return String.format("%d,%s,%d,%s",
                             seqID, printID, tileID, sb);
    }
}
