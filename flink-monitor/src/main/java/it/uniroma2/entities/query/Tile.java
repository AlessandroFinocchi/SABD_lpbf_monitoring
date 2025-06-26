package it.uniroma2.entities.query;

import it.uniroma2.utils.MatrixMath;

public class Tile {
    protected final int size;
    protected final int seqID;
    protected final String printID;
    protected final int layerID;
    protected final int tileID;
    protected int[][] values;

    public Tile(int size, int seqID, String printID, int layerID, int tileID, int[][] values) {
        this.size = size;
        this.seqID = seqID;
        this.printID = printID;
        this.layerID = layerID;
        this.tileID = tileID;
        this.values = values;
    }

    public int getSize() {
        return size;
    }

    public int[][] getValues() {
        return values;
    }

    public void setValues(int[][] values) {
        this.values = values;
    }

    @Override
    public String toString() {
        String sb = "Tile{" + "size=" + size +
                ", seqID=" + seqID +
                ", printID='" + printID + '\'' +
                ", layerID=" + layerID +
                ", tileID=" + tileID +
                ", values=\n" + MatrixMath.matrixToString(values) +
                '}';
        // MatrixMath.saveMatrix(this.values, sb.toString());
        return sb;
    }
}
