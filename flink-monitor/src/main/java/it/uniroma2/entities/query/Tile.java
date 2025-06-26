package it.uniroma2.entities.query;

public class Tile {
    private final int size;
    private final int seqID;
    private final String printID;
    private final int layerID;
    private final int tileID;
    private final Integer depth;
    private final int[][] values;

    private int saturatedPoints;

    public Tile(int size, int seqID, String printID, int layerID, int tileID, int[][] values) {
        this.size = size;
        this.seqID = seqID;
        this.printID = printID;
        this.layerID = layerID;
        this.tileID = tileID;
        this.depth = null;
        this.values = values;
        this.saturatedPoints = 0;
    }

    public int getSize() {
        return size;
    }

    public void incrementSaturatedPoints() {
        this.saturatedPoints++;
    }



    public int[][] getValues() {
        return values;
    }

    @Override
    public String toString() {
        String sb = "Tile{" + "size=" + size +
                ", seqID=" + seqID +
                ", printID='" + printID + '\'' +
                ", layerID=" + layerID +
                ", tileID=" + tileID +
                ", depth=" + depth +
                ", saturatedPoints=" + saturatedPoints +
                // ", values=\n" + MatrixMath.matrixToString(values) +
                '}';
        // MatrixMath.saveMatrix(this.values, sb.toString());
        return sb;
    }
}
