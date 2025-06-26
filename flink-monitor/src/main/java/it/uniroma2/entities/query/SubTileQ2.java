package it.uniroma2.entities.query;

import it.uniroma2.utils.MatrixMath;

public class SubTileQ2 extends TileQ1 {
    protected int depth;

    public SubTileQ2(TileQ1 parent) {
        super(parent.size, parent.seqID, parent.printID, parent.layerID, parent.tileID, parent.values, parent.saturatedPoints);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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
