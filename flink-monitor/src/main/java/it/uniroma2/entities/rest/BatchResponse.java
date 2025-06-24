package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BatchResponse {
    private final int batchId;
    private final String printId;
    private final int tileId;
    private final int layer;
    private final byte[] tif;

    @JsonCreator
    public BatchResponse(
            @JsonProperty("batch_id") int batchId,
            @JsonProperty("print_id") String printId,
            @JsonProperty("tile_id") int tileId,
            @JsonProperty("layer") int layer,
            @JsonProperty("tif") byte[] tif) {
        this.batchId = batchId;
        this.printId = printId;
        this.tileId = tileId;
        this.layer = layer;
        this.tif = tif;
    }

    public int getBatchId() {
        return batchId;
    }

    public String getPrintId() {
        return printId;
    }

    public int getTileId() {
        return tileId;
    }

    public int getLayer() {
        return layer;
    }

    public byte[] getTif() {
        return tif;
    }

    public void saveTif() {
        String filePath =
                        "layer" + this.getLayer() +
                        "tile"  + this.getTileId()+
                        ".tif";
        try {
            File file = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(this.getTif());
                System.out.println("Image saved to " + filePath);
            }
        } catch (IOException e) { System.err.println("Error saving image: " + e.getMessage()); }
    }

    @Override
    public String toString() {
        return "Batch "   + this.getBatchId() +
               ": print"  + this.getPrintId() +
               ", layer " + this.getLayer() +
               ", tile "  + this.getTileId();
    }
}
