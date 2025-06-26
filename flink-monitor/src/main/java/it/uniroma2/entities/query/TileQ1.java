package it.uniroma2.entities.query;

public class TileQ1 extends Tile {
    protected int saturatedPoints;

    public TileQ1(int size, int seqID, String printID, int layerID, int tileID, int[][] values) {
        super(size, seqID, printID, layerID, tileID, values);
    }

    public TileQ1(Tile tile) {
        super(tile.size,
              tile.seqID,
              tile.printID,
              tile.layerID,
              tile.tileID,
              tile.values);
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
