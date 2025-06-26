package it.uniroma2.entities.query;

public class TileQ1 extends Tile {
    protected int saturatedPoints;

    protected TileQ1(int size, int seqID, String printID, int layerID, int tileID, int[][] values, int saturatedPoints) {
        super(size, seqID, printID, layerID, tileID, values);
        this.saturatedPoints = saturatedPoints;
    }

    public TileQ1(Tile tile) {
        super(tile.size,
              tile.seqID,
              tile.printID,
              tile.layerID,
              tile.tileID,
              tile.values);
        this.saturatedPoints = 0;
    }

    public int getSaturatedPoints() {
        return saturatedPoints;
    }

    public void setSaturatedPoints(int saturatedPoints) {
        this.saturatedPoints = saturatedPoints;
    }

    public void incrementSaturatedPoints() {
        this.saturatedPoints++;
    }

    @Override
    public String toString() {
        String sb = "Tile{" + "size=" + size +
                ", seqID=" + seqID +
                ", printID='" + printID + '\'' +
                ", layerID=" + layerID +
                ", tileID=" + tileID +
                ", saturatedPoints=" + saturatedPoints +
                '}';
        return sb;
    }

}
