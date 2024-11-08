package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SparseMatrixCSRMultiplication {
    double[] values;
    int[] columnIndices;
    int[] rowPointers;

    int rows, cols;

    SparseMatrixCSRMultiplication(double[] values, int[] columnIndices, int[] rowPointers, int rows, int cols) {
        this.values = values;
        this.columnIndices = columnIndices;
        this.rowPointers = rowPointers;
        this.rows = rows;
        this.cols = cols;
    }

    public SparseMatrixCSRMultiplication multiply(SparseMatrixCSRMultiplication B) {
        if (this.cols != B.rows) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }

        List<Double> resultValues = new ArrayList<>();
        List<Integer> resultColumnIndices = new ArrayList<>();
        List<Integer> resultRowPointers = new ArrayList<>();
        resultRowPointers.add(0);

        double[] rowResult = new double[B.cols];

        for (int i = 0; i < this.rows; i++) {
            Arrays.fill(rowResult, 0.0);

            for (int j = this.rowPointers[i]; j < this.rowPointers[i + 1]; j++) {
                int colA = this.columnIndices[j];
                double valA = this.values[j];

                for (int k = B.rowPointers[colA]; k < B.rowPointers[colA + 1]; k++) {
                    int colB = B.columnIndices[k];
                    double valB = B.values[k];
                    rowResult[colB] += valA * valB;
                }
            }

            int nonZeroCount = 0;
            for (int j = 0; j < B.cols; j++) {
                if (rowResult[j] != 0.0) {
                    resultValues.add(rowResult[j]);
                    resultColumnIndices.add(j);
                    nonZeroCount++;
                }
            }

            resultRowPointers.add(resultRowPointers.get(resultRowPointers.size() - 1) + nonZeroCount);
        }

        double[] resultValuesArray = resultValues.stream().mapToDouble(Double::doubleValue).toArray();
        int[] resultColumnIndicesArray = resultColumnIndices.stream().mapToInt(Integer::intValue).toArray();
        int[] resultRowPointersArray = resultRowPointers.stream().mapToInt(Integer::intValue).toArray();

        return new SparseMatrixCSRMultiplication(resultValuesArray, resultColumnIndicesArray, resultRowPointersArray, this.rows, B.cols);
    }

    public static SparseMatrixCSRMultiplication convertToCSR(double[][] matrix) {
        List<Double> valuesList = new ArrayList<>();
        List<Integer> columnIndicesList = new ArrayList<>();
        List<Integer> rowPointersList = new ArrayList<>();

        int rows = matrix.length;
        int cols = matrix[0].length;

        rowPointersList.add(0);

        for (int i = 0; i < rows; i++) {
            int nonZeroCount = 0;
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    valuesList.add(matrix[i][j]);
                    columnIndicesList.add(j);
                    nonZeroCount++;
                }
            }
            rowPointersList.add(rowPointersList.get(rowPointersList.size() - 1) + nonZeroCount);
        }

        double[] values = valuesList.stream().mapToDouble(Double::doubleValue).toArray();
        int[] columnIndices = columnIndicesList.stream().mapToInt(Integer::intValue).toArray();
        int[] rowPointers = rowPointersList.stream().mapToInt(Integer::intValue).toArray();

        return new SparseMatrixCSRMultiplication(values, columnIndices, rowPointers, rows, cols);
    }
}
