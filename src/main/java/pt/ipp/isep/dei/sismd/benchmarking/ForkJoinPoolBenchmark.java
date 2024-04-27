package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pt.ipp.isep.dei.sismd.Utils;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.ForkJoinExecutor;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ForkJoinPoolBenchmark {
    @Param("src/main/resources/imgs/4k/city-night-4k.png")
    private String pathToFile;

    @Param("pt.ipp.isep.dei.sismd.filters.GlassFilter")
    private String filterName;

    private Image image;

    private Filter filter;

    @Setup
    public void setup() {
        filter = (Filter) getInstance(filterName);
        image = Utils.loadImage(new File(pathToFile));
    }

    private Object getInstance(String name) {
        try {
            Class<?> executor = Class.forName(name);
            return executor.newInstance();
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not found: " + name);
            ex.printStackTrace();
        } catch (InstantiationException | IllegalAccessException ex) {
            System.out.println("Error instantiating class: " + ex.getMessage());
            ex.printStackTrace();
        }
        throw new RuntimeException("Failed to load class: " + name);
    }

    @Benchmark
    public Image threshold_5_000() {
        return new ForkJoinExecutor(filter, 5000).apply(image);
    }

    @Benchmark
    public Image threshold_10_000() {
        return new ForkJoinExecutor(filter, 10_000).apply(image);
    }

    @Benchmark
    public Image threshold_25_000() {
        return new ForkJoinExecutor(filter, 25_000).apply(image);
    }

    @Benchmark
    public Image threshold_50_000() {
        return new ForkJoinExecutor(filter, 50_000).apply(image);
    }

    @Benchmark
    public Image threshold_100_000() {
        return new ForkJoinExecutor(filter, 100_000).apply(image);
    }

    public static void main(String[] args) throws Exception {
        Options smallImage = new OptionsBuilder()
                .include(ForkJoinPoolBenchmark.class.getSimpleName())
                .param("pathToFile", "src/main/resources/imgs/turtle.jpg")
                .forks(3)
                .resultFormat(ResultFormatType.CSV)
                .result("report/benchmark_results/forkJoinPool/small_image" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(smallImage).run();

        Options bigImage = new OptionsBuilder()
                .include(ForkJoinPoolBenchmark.class.getSimpleName())
                .param("pathToFile", "src/main/resources/imgs/4k/city-night-4k.png")
                .forks(3)
                .resultFormat(ResultFormatType.CSV)
                .result("report/benchmark_results/forkJoinPool/big_image" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(bigImage).run();
    }
}
