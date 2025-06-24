package it.uniroma2.entities.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.util.Iterator;

public class BatchResponse {
    private static final int WIDTH  = 500;
    private static final int HEIGHT = 500;

    @Getter private final int batchId;
    @Getter private final String printId;
    @Getter private final int tileId;
    @Getter private final int layer;
    @Getter private final byte[] tif;

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
               ", layer " + this.getLayer()   +
               ", tile "  + this.getTileId();
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
