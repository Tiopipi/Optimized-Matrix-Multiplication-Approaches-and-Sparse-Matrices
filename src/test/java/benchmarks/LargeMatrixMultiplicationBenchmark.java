package benchmarks;

import org.example.*;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;


@BenchmarkMode({Mode.AverageTime, })
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class LargeMatrixMultiplicationBenchmark {


    @Param({"7056"})
    private int size;

    private List<Long> memoryUsages;
    private SparseMatrixCSRMultiplication csrA;
    private SparseMatrixCSCMultiplication cscA;
    private double[][] A;
    private double[][] B;

    @Setup(Level.Trial)
    public void setupMatrices() throws IOException {
        String filePath = "mc2depi.mtx";
        csrA = MTXLoader.loadMatrixInCSR(filePath);
        cscA = MTXLoader.loadMatrixInCSC(filePath);
        A =generateMatrix(size);
        B = generateMatrix(size);

        memoryUsages = new ArrayList<>();
    }

    @Setup(Level.Invocation)
    public void setupMemoryTracking() {
        System.gc();
    }

    @Benchmark
    public void largeMatrixMultiplicationCSR() {
        csrA.multiply(csrA);
    }

    @Benchmark
    public void largeMatrixMultiplicationCSC() {cscA.multiply(cscA);}

    @Benchmark
    public void originalMatrixMultiplication() {
        MatrixMultiplication.matrixMultiplication(A, B);
    }

    @Benchmark
    public void loopUnrollingMatrixMultiplication() {
        LoopUnrollingMatrixMultiplication.loopUnrollingMatrixMultiplication(A, B);
    }

    @Benchmark
    public void blockMatrixMultiplication() {
        CacheBlockedMultiplication.blockMatrixMultiplication(A, B, (int) Math.sqrt(size));
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

    private static double[][] generateMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Math.random();
            }
        }
        return matrix;
    }
}
