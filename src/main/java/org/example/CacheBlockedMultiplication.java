package org.example;

public class CacheBlockedMultiplication {
    public static void blockMatrixMultiplication(double[][] a, double[][] b,  int blockSize) {
        int n = a.length;
        double [][] c = new double[n][n];
        for(int kk = 0; kk < n; kk += blockSize){
            for(int jj = 0; jj < n; jj += blockSize){
                for(int i = 0; i < n; i++){
                    for(int j = jj; j < jj + blockSize; ++j){
                        double sum = c[i][j];
                        for (int k = kk; k < kk + blockSize; ++k) {
                            sum += a[i][k] * b[k][j];
                        }
                        c[i][j] = sum;
                    }
                }
            }
        }
    }
}
