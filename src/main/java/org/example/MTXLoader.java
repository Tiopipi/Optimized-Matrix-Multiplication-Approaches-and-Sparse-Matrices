package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MTXLoader {


    public static SparseMatrixCSRMultiplication loadMatrixInCSR(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int rows = 0, cols = 0, nnz = 0;
        List<Double> valuesList = new ArrayList<>();
        List<Integer> columnIndicesList = new ArrayList<>();
        List<Integer> rowPointersList = new ArrayList<>();
        rowPointersList.add(0);

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("%")) continue;
            String[] dimensions = line.trim().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            nnz = Integer.parseInt(dimensions[2]);
            break;
        }

        int currentRow = 0;
        int nonZeroCount = 0;

        while ((line = reader.readLine()) != null) {
            String[] values = line.trim().split(" ");
            int row = Integer.parseInt(values[0]) - 1;
            int col = Integer.parseInt(values[1]) - 1;
            double value = Double.parseDouble(values[2]);

            while (currentRow < row) {
                rowPointersList.add(nonZeroCount);
                currentRow++;
            }

            valuesList.add(value);
            columnIndicesList.add(col);
            nonZeroCount++;
        }

        rowPointersList.add(nonZeroCount);

        reader.close();

        double[] valuesArray = valuesList.stream().mapToDouble(Double::doubleValue).toArray();
        int[] columnIndicesArray = columnIndicesList.stream().mapToInt(Integer::intValue).toArray();
        int[] rowPointersArray = rowPointersList.stream().mapToInt(Integer::intValue).toArray();

        return new SparseMatrixCSRMultiplication(valuesArray, columnIndicesArray, rowPointersArray, rows, cols);
    }

    public static SparseMatrixCSCMultiplication loadMatrixInCSC(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        int rows = 0, cols = 0, nnz = 0;
        List<Double> valuesList = new ArrayList<>();
        List<Integer> rowIndicesList = new ArrayList<>();
        List<Integer> colPointersList = new ArrayList<>();

        // Leer dimensiones y número de elementos no nulos
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("%")) continue;  // Saltar comentarios
            String[] dimensions = line.trim().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            nnz = Integer.parseInt(dimensions[2]);
            break;
        }

        // Inicializar colPointers con el tamaño de columnas + 1
        colPointersList = new ArrayList<>(Collections.nCopies(cols + 1, 0));

        // Leer los datos no nulos y llenar valores y filas
        while ((line = reader.readLine()) != null) {
            String[] values = line.trim().split(" ");
            int row = Integer.parseInt(values[0]) - 1;
            int col = Integer.parseInt(values[1]) - 1;
            double value = Double.parseDouble(values[2]);

            valuesList.add(value);
            rowIndicesList.add(row);
            colPointersList.set(col + 1, colPointersList.get(col + 1) + 1);
        }

        reader.close();

        // Construir colPointers sumando acumulativamente
        for (int i = 1; i < colPointersList.size(); i++) {
            colPointersList.set(i, colPointersList.get(i) + colPointersList.get(i - 1));
        }

        double[] valuesArray = valuesList.stream().mapToDouble(Double::doubleValue).toArray();
        int[] rowIndicesArray = rowIndicesList.stream().mapToInt(Integer::intValue).toArray();
        int[] colPointersArray = colPointersList.stream().mapToInt(Integer::intValue).toArray();

        return new SparseMatrixCSCMultiplication(valuesArray, rowIndicesArray, colPointersArray, rows, cols);
    }
}


