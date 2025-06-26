package it.uniroma2.entities.query;

public class SubTileQ2 extends TileQ1 {
    protected int depth;
    private int[][] baseValues;

    public SubTileQ2(TileQ1 parent) {
        super(parent.size, parent.seqID, parent.printID, parent.layerID, parent.tileID, parent.values, parent.saturatedPoints);
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
                // ", values=\n" + MatrixMath.matrixToString(values) +
                '}';
    }
}
