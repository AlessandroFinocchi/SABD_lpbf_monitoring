package it.uniroma2.entities.query;

import it.uniroma2.utils.MatrixMath;

public class Tile {
    protected final int size;
    protected String printID;
    protected int seqID;
    protected int layerID;
    protected int tileID;
    protected int[][] values;

    public Tile(int size, String printID, int seqID, int layerID, int tileID, int[][] values) {
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

    public int getSeqID() {
        return seqID;
    }

    public String getPrintID() {
        return printID;
    }

    public int getLayerID() {
        return layerID;
    }

    public int getTileID() {
        return tileID;
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
        return sb;
    }
}
