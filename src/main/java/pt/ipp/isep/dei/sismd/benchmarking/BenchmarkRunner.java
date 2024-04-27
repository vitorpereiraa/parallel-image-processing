package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
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

        Options smallImageZGC = new OptionsBuilder()
                .include(FilterBenchmark.class.getSimpleName())
                .param("pathToFile", "turtle.jpg")
                .forks(3)
                .jvmArgs("-XX:+UseZGC")
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark_results/grayscale/small_image_zgc_" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(smallImageZGC).run();

        Options bigImageZGC = new OptionsBuilder()
                .include(FilterBenchmark.class.getSimpleName())
                .param("pathToFile", "src/main/resources/imgs/4k/city-night-4k.png")
                .forks(3)
                .jvmArgs("-XX:+UseZGC")
                .resultFormat(ResultFormatType.CSV)
                .result("benchmark_results/grayscale/big_image_zgc_" + System.currentTimeMillis() + ".csv")
                .build();

        new Runner(bigImageZGC).run();
    }
}
