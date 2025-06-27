package it.uniroma2.entities.query;

public class SubTileQ2 extends TileQ1 {
    protected int depth;
    protected int[][] baseValues;

    public SubTileQ2(int size, String printID, int seqID, int layerID, int tileID, int[][] values, int saturatedPoints, int depth, int[][] baseValues) {
        super(size, printID, seqID, layerID, tileID, values, saturatedPoints);
        this.depth = depth;
        this.baseValues = baseValues;
    }

    public SubTileQ2(TileQ1 parent) {
        super(parent.size, parent.printID, parent.seqID, parent.layerID, parent.tileID, parent.values, parent.saturatedPoints);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int[][] getBaseValues() {
        return baseValues;
    }

    public void setBaseValues(int[][] baseValues) {
        this.baseValues = baseValues;
    }

    @Override
    public String toString() {
        return "SubTileQ2{" +
                "depth=" + depth +
                ", seqID=" + seqID +
                ", layerID=" + layerID +
                ", tileID=" + tileID +
                '}';
    }
}
