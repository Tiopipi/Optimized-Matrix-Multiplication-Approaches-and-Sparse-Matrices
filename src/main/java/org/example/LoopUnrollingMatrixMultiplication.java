package org.example;


public class LoopUnrollingMatrixMultiplication {
    public static void loopUnrollingMatrixMultiplication(double[][] a, double[][] b) {
        int n = a.length;
        double[][] result = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0;
                int k = 0;
                int limit = n - 8;

                for (; k < limit; k += 8) {
                    sum += a[i][k] * b[k][j]
                            + a[i][k + 1] * b[k + 1][j]
                            + a[i][k + 2] * b[k + 2][j]
                            + a[i][k + 3] * b[k + 3][j]
                            + a[i][k + 4] * b[k + 4][j]
                            + a[i][k + 5] * b[k + 5][j]
                            + a[i][k + 6] * b[k + 6][j]
                            + a[i][k + 7] * b[k + 7][j];
                }

                for (; k < n; k++) {
                    sum += a[i][k] * b[k][j];
                }

                result[i][j] = sum;
            }
        }
    }
}


