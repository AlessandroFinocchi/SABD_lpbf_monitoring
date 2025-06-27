package it.uniroma2.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MatrixMath {

    public static String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            for (int j = 0; j < row.length; j++) {
                sb.append(row[j]);
                if (j < row.length - 1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");  // new line after each row
        }
        return sb.toString();
    }

    public static String matrixToString(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            for (int j = 0; j < row.length; j++) {
                sb.append(String.format("%+1.4f", row[j]));
                if (j < row.length - 1) {
                    sb.append(" ");
                }
            }
            sb.append("\n");  // new line after each row
        }
        return sb.toString();
    }

    public static void saveMatrix(int[][] matrix, String filename) {
        File outFile = new File(filename);

        try (PrintWriter pw = new PrintWriter(outFile)) {
            for (int[] row : matrix) {
                for (int j = 0; j < row.length; j++) {
                    pw.print(row[j]);
                    if (j < row.length - 1) {
                        pw.print(" ");
                    }
                }
                pw.println();  // new line after each row
            }
            // pw.flush() is automatic at close
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int[][] addMatrix(int[][] a, int[][] b) {
        int[][] result = new int[a.length][a.length];
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a.length; y++) {
                result[x][y] = a[x][y] + b[x][y];
            }
        }
        return result;
    }

    public static int[][] absMatrix(int[][] a) {
        int[][] result = new int[a.length][a.length];
        for (int x = 0; x < a.length; x++) {
            for (int y = 0; y < a.length; y++) {
                result[x][y] = Math.abs(a[x][y]);
            }
        }
        return result;
    }

    public static int manhattanDistance(int ax, int ay, int bx, int by) {
        return Math.abs(ax - bx) + Math.abs(ay - by);
    }

    public static int getTotalByDistance(int distance, int maxDepth) {
        int total = 0;
        for (int i = 0; i <= maxDepth; i++) {
            int layerDistance = distance - i;
            if (layerDistance < 0) break;

            total += 4 * ((layerDistance) * (layerDistance + 1) / 2) + 1;
        }
        return total;
    }

    public static int[][] convolutionPadded(int[][] input, double[][] kernel) {
        int halfSize = kernel.length / 2;
        int outputLength = input.length;
        int[][] output = new int[outputLength][outputLength];

        for (int ix = 0; ix < input.length; ix++) {
            for (int iy = 0; iy < input.length; iy++) {
                double sum = 0;
                for (int kx = 0; kx < kernel.length; kx++) {
                    for (int ky = 0; ky < kernel.length; ky++) {
                        if (kernel[kx][ky] == 0) continue;
                        int x = ix + kx - halfSize;
                        int y = iy + ky - halfSize;
                        // Padding
                        int cell = ((x < 0) || (x >= input.length) || (y < 0) || (y >= input.length)) ? 0 : input[x][y];
                        cell = Math.max(cell, 0);
                        sum += ((double) cell * kernel[kx][ky]);
                    }
                }
                output[ix][iy] = (int) sum;
            }
        }
        return output;
    }
}
