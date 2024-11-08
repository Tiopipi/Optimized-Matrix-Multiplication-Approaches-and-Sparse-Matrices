package benchmarks;

import org.example.*;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

import static org.example.SparseMatrixCSCMultiplication.convertToCSC;
import static org.example.SparseMatrixCSRMultiplication.convertToCSR;


@BenchmarkMode({Mode.AverageTime, })
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class SparseMatrixBenchmark {
    @Param({"256", "576", "784", "1024", "1764"})
    public static int size;

    @Param({"0.3", "0.5", "0.7","0.9"})
    public static double sparsity;


    private List<Long> memoryUsages;
    private SparseMatrixCSCMultiplication cscA;
    private SparseMatrixCSCMultiplication cscB;
    private SparseMatrixCSRMultiplication csrA;
    private SparseMatrixCSRMultiplication csrB;



    @Setup(Level.Trial)
    public void setupMatrices() {

        double[][] sparseA = generateSparseMatrix(size, sparsity);
        double[][] sparseB = generateSparseMatrix(size, sparsity);
        cscA = convertToCSC(sparseA);
        cscB = convertToCSC(sparseB);
        csrA = convertToCSR(sparseA);
        csrB = convertToCSR(sparseB);

        memoryUsages = new ArrayList<>();
    }

    @Setup(Level.Invocation)
    public void setupMemoryTracking() {
        System.gc();
    }

    @Benchmark
    public void sparseMatrixCSCMultiplication() {
        cscA.multiply(cscB);
    }

    @Benchmark
    public void sparseMatrixCSRMultiplication() {
        csrA.multiply(csrB);
    }

    @TearDown(Level.Invocation)
    public void tearDownMemoryTracking() {
        long memoryUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
        memoryUsages.add(memoryUsed);
    }

    @TearDown(Level.Trial)
    public void calculateAverageMemoryUsage() {
        long totalMemoryUsed = memoryUsages.stream().mapToLong(Long::longValue).sum();
        long averageMemoryUsed = totalMemoryUsed / memoryUsages.size();
        System.out.println("\nAverage memory used: " + averageMemoryUsed + " KB");
    }

    public static double[][] generateSparseMatrix(int size, double sparsity) {
        double[][] matrix = new double[size][size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (random.nextDouble() >= sparsity) {
                    matrix[i][j] = random.nextDouble();
                } else {
                    matrix[i][j] = 0;
                }
            }
        }
        return matrix;
    }

}
