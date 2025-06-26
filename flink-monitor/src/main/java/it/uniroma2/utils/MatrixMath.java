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
}
