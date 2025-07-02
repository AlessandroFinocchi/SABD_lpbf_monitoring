package it.uniroma2.entities.query;

public class TileQ1 extends Tile {
    protected int saturatedPoints;

    protected TileQ1(int size, String printID, int seqID, int layerID, int tileID, int[][] values, long arrivalTs, int saturatedPoints) {
        super(size, printID, seqID, layerID, tileID, values, arrivalTs);
        this.saturatedPoints = saturatedPoints;
    }

    public TileQ1(Tile tile) {
        super(tile.getSize(),
              tile.getPrintID(),
              tile.getSeqID(),
              tile.getLayerID(),
              tile.getTileID(),
              tile.getValues(),
              tile.getArrivalTs());
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
        return String.format("Q1: {printID='%s', seqID=%4d, layerID=%3d, tileID=%2d, saturatedPoints=%s}",
                                  printID, seqID, layerID, tileID, saturatedPoints);
    }

}
