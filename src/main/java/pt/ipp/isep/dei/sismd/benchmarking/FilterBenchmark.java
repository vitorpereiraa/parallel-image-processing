package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pt.ipp.isep.dei.sismd.Utils;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.MultithreadedExecutor;
import pt.ipp.isep.dei.sismd.executors.SequentialExecutor;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class FilterBenchmark {

    @Param("src/main/resources/imgs/4k/city-night-4k.png")
    private String pathToFile;

    @Param("pt.ipp.isep.dei.sismd.filter.grayscale.GrayscaleFilter")
    private String filterName;

    private Image image;

    private Filter filter;

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
            throw new RuntimeException("class not found");
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

    public static void main(String[] args) throws RunnerException {
        Options smallImageG1GC = new OptionsBuilder()
                .include(FilterBenchmark.class.getSimpleName())
                .param("pathToFile", "turtle.jpg")
                .forks(3)
                .jvmArgs("-XX:+UseG1GC")
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark_results/grayscale/small_image_g1gc_" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(smallImageG1GC).run();

        Options bigImageG1GC = new OptionsBuilder()
                .include(FilterBenchmark.class.getSimpleName())
                .param("pathToFile", "src/main/resources/imgs/4k/city-night-4k.png")
                .forks(3)
                .jvmArgs("-XX:+UseG1GC")
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark_results/grayscale/big_image_g1gc_" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(bigImageG1GC).run();
    }
}
