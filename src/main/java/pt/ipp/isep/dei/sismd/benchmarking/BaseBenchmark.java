package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.annotations.*;
import pt.ipp.isep.dei.sismd.Utils;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.*;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BaseBenchmark {

    @Param("src/main/resources/imgs/turtle.jpg")
    private String pathToFile;

    @Param("pt.ipp.isep.dei.sismd.filters.BrighterFilter")
    private String filterName;

    private Filter filter;

    private Image image;

    @Setup
    public void setup() {
        filter =  (Filter) getInstance(filterName);
        image = Utils.loadImage(new File(pathToFile));
    }

    private Object getInstance(String name) {
        try{
            Class<?> executor = Class.forName(name);
            return executor.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("class not found", ex);
        }
    }

    @Benchmark
    public Image sequential() {
        return new SequentialExecutor(filter).apply(image);
    }

    @Benchmark
    public Image multithreaded() {
        return new MultithreadedExecutor(filter).apply(image);
    }

    @Benchmark
    public Image executorsPerPixel() {
        return new ExecutorsExecutorPerPixel(filter).apply(image);
    }

    @Benchmark
    public Image executorsPerLine() {
        return new ExecutorsExecutorPerLine(filter).apply(image);
    }

    @Benchmark
    public Image executorsPerSlice() {
        return new ExecutorsExecutorPerSlice(filter).apply(image);
    }

    @Benchmark
    public Image forkjoin() {
        return new ForkJoinExecutor(filter).apply(image);
    }

    @Benchmark
    public Image forkjoin_5000() {
        return new ForkJoinExecutor(filter, 5000).apply(image);
    }

    @Benchmark
    public Image forkjoin_10000() {
        return new ForkJoinExecutor(filter, 10000).apply(image);
    }

    @Benchmark
    public Image forkjoin_50000() {
        return new ForkJoinExecutor(filter, 50000).apply(image);
    }

    @Benchmark
    public Image forkjoin_100000() {
        return new ForkJoinExecutor(filter, 100000).apply(image);
    }

    @Benchmark
    public Image completableFuturePerPixel() {
        return new CompletableFutureExecutorPerPixel(filter).apply(image);
    }

    @Benchmark
    public Image completableFuturePerLine() {
        return new CompletableFutureExecutorPerLine(filter).apply(image);
    }

    @Benchmark
    public Image completableFuturePerSlice() {
        return new CompletableFutureExecutorPerSlice(filter).apply(image);
    }
}
