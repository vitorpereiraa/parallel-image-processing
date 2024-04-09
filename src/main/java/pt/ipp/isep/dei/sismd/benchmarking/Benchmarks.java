package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.annotations.*;
import pt.ipp.isep.dei.sismd.domain.Image;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Benchmarks {

    @Param({"10"})
    private int iterations;

    private final String filepath = "src/main/resources/imgs/4k/city-night-4k.png";

    private Image image;

    @Setup
    public void setup() {
//        TODO: Load image on setup
    }

    @Benchmark
    @Warmup(iterations = 0)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark_sequential() {
//        TODO: Implement benchmark
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Warmup(iterations = 0)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark_multithreaded() {
//        TODO: Implement benchmark
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Warmup(iterations = 0)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark_executor() {
//        TODO: Implement benchmark
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Warmup(iterations = 0)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark_fork_join_pool() {
//        TODO: Implement benchmark
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Warmup(iterations = 0)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark_completable_futures() {
//        TODO: Implement benchmark
    }
}
