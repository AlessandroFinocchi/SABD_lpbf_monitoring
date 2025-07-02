package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RESTBatchResponse {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    public int batchId;
    public String printId;
    public int tileId;
    public int layer;
    public byte[] tif;
    public final double arrivalTs;

    @JsonCreator
    public RESTBatchResponse(
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
        this.arrivalTs = System.currentTimeMillis();
    }

    public int getSize() {
        return WIDTH;
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

    public double getArrivalTs() {
        return arrivalTs;
    }

    @Override
    public String toString() {
        return "Batch " + this.getBatchId() +
                ": print" + this.getPrintId() +
                ", layer " + this.getLayer() +
                ", tile " + this.getTileId();
    }

    public void saveTif() {
        String filePath =
                "layer" + this.getLayer() +
                        "tile" + this.getTileId() +
                        ".tif";
        try {
            File file = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(this.getTif());
                System.out.println("Image saved to " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    public int[][] convertTiffToMatrix() throws Exception {
        int[][] matrix = new int[HEIGHT][WIDTH];

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(this.tif));
        Raster r = image.getData();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int pixelValue = r.getSample(x, y, 0);
                matrix[y][x] = pixelValue;
            }
        }
        return matrix;
    }
}
