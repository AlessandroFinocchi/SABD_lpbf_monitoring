package it.uniroma2.entities.query;

public class SubTileQ2 extends TileQ1 {
    protected int depth;
    protected int[][] baseValues;

    public SubTileQ2(int size, String printID, int seqID, int layerID, int tileID, int[][] values, int saturatedPoints, int depth, int[][] baseValues) {
        super(size, printID, seqID, layerID, tileID, values, saturatedPoints);
        this.depth = depth;
        this.baseValues = baseValues;
    }

    public SubTileQ2(TileQ1 parent, int depth, int[][] baseValues) {
        this(parent.getSize(),
             parent.getPrintID(),
             parent.getSeqID(),
             parent.getLayerID(),
             parent.getTileID(),
             parent.getValues(),
             parent.getSaturatedPoints(),
             depth,
             baseValues);
    }

    public SubTileQ2(SubTileQ2 input) {
        this(input.getSize(), input.getPrintID(), input.getSeqID(), input.getLayerID(), input.getTileID(), input.getValues(), input.getSaturatedPoints(), input.getDepth(), input.getBaseValues());
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
