package pt.ipp.isep.dei.sismd.filters.grayscale;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import pt.ipp.isep.dei.sismd.Utils;
import pt.ipp.isep.dei.sismd.domain.Image;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GrayscaleBenchmarks {

    @Param("turtle.jpg")
    private String pathToFile;

    private Image image;

    @Setup
    public void setup() {
        image = Utils.loadImage(new File(pathToFile));
    }

    @Benchmark
    public Image sequential() {
        return new SequentialGrayscaleFilter().apply(image);
    }

    @Benchmark
    public Image multithreaded() {
        return new MultithreadedGrayscaleFilter().apply(image);
    }

    @Benchmark
    public Image executor() {
        return new ExecutorGrayscaleFilter().apply(image);
    }

    @Benchmark
    public Image forkJoin() {
        return new ForkJoinGrayscaleFilter().apply(image);
    }

    @Benchmark
    public Image completableFuture() {
        return new CompletableFutureGrayscaleFilter().apply(image);
    }

    public static void main(String[] args) throws RunnerException {
        Options smallImageG1GC = new OptionsBuilder()
                .include(GrayscaleBenchmarks.class.getSimpleName())
                .param("pathToFile", "turtle.jpg")
                .forks(3)
                .jvmArgs("-XX:+UseG1GC")
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark_results/grayscale/small_image_g1gc_" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(smallImageG1GC).run();

        Options bigImageG1GC = new OptionsBuilder()
                .include(GrayscaleBenchmarks.class.getSimpleName())
                .param("pathToFile", "src/main/resources/imgs/4k/city-night-4k.png")
                .forks(3)
                .jvmArgs("-XX:+UseG1GC")
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark_results/grayscale/big_image_g1gc_" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(bigImageG1GC).run();

        Options smallImageZGC = new OptionsBuilder()
                .include(GrayscaleBenchmarks.class.getSimpleName())
                .param("pathToFile", "turtle.jpg")
                .forks(3)
                .jvmArgs("-XX:+UseZGC")
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark_results/grayscale/small_image_zgc_" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(smallImageZGC).run();

        Options bigImageZGC = new OptionsBuilder()
                .include(GrayscaleBenchmarks.class.getSimpleName())
                .param("pathToFile", "src/main/resources/imgs/4k/city-night-4k.png")
                .forks(3)
                .jvmArgs("-XX:+UseZGC")
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark_results/grayscale/big_image_zgc_" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(bigImageZGC).run();
    }
}
