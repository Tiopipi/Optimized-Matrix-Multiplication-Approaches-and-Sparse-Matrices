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

    private List<Long> memoryUsages;
    private SparseMatrixCSRMultiplication csrA;

    @Setup(Level.Trial)
    public void setupMatrices() throws IOException {
        String filePath = "mc2depi.mtx";
        csrA = MTXLoader.loadMatrixInCSR(filePath);

        memoryUsages = new ArrayList<>();
    }

    @Setup(Level.Invocation)
    public void setupMemoryTracking() {
        System.gc();
    }

    @Benchmark
    public void largeMatrixMultiplication() {
        csrA.multiply(csrA);
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
}
