package it.uniroma2.entities.matrix;

import java.io.Serializable;

import static it.uniroma2.utils.MatrixMath.getTotalByDistance;
import static it.uniroma2.utils.MatrixMath.manhattanDistance;

public class Kernel implements Serializable {
    public static int NEAR_DISTANCE_AT_0 = 2;
    public static int FAR_DISTANCE_AT_0 = 4;
    // Starting from depth 0
    public static int MAX_DEPTH = 2;

    public static int TOTAL_NEAR_NEIGHBOURS = getTotalByDistance(NEAR_DISTANCE_AT_0, MAX_DEPTH);
    public static int TOTAL_FAR_NEIGHBOURS = getTotalByDistance(FAR_DISTANCE_AT_0, MAX_DEPTH);

    double[][] values;
    int halfSize;

    public Kernel(double[][] values) {
        this.values = values;
    }

    public Kernel(int depth, boolean normalize) {
        // Compute the distance of the near and the far neighbors at a given depth
        int nearDistance = NEAR_DISTANCE_AT_0 - depth;
        int farDistance = FAR_DISTANCE_AT_0 - depth;

        // The "radius" of the kernel is the max between nearDistance and farDistance
        this.halfSize = Math.max(nearDistance, farDistance);
        this.values = new double[halfSize * 2 + 1][halfSize * 2 + 1];

        // Cycle through all the points in the kernel
        int xCenter = halfSize, yCenter = halfSize;
        for (int x = 0; x < this.values.length; x++) {
            for (int y = 0; y < this.values[x].length; y++) {
                // Compute Manhattan distance
                int dist = manhattanDistance(xCenter, yCenter, x, y);
                // The value of the kernel can be already normalized by counting how many
                // neighbors there will be, considering the distance at depth 0 and how many
                // layers in depth it can go
                if (dist <= nearDistance) {
                    this.values[x][y] = 1.0 / (normalize ? TOTAL_NEAR_NEIGHBOURS : 1);
                } else if (dist <= farDistance) {
                    this.values[x][y] = -1.0 / (normalize ? TOTAL_FAR_NEIGHBOURS : 1);
                } else {
                    this.values[x][y] = 0;
                }
            }
        }
    }

    public double[][] getValues() {
        return values;
    }
}
