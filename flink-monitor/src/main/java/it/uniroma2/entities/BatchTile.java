package it.uniroma2.entities;

public class BatchTile {
    public int batch_id;
    public String print_id;
    public String tile_id;
    public int layer;
    public byte[] tif; // or BufferedImage if deserialized

    public BatchTile(String restContent) {
        System.out.println(restContent);
    }
}