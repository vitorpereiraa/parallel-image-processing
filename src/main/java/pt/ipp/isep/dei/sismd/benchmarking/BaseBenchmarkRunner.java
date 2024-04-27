package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BaseBenchmarkRunner {
    public enum ImageSize {
        huge,
        big,
        medium,
        small
    }

    public enum GCType {
        SerialGC,
        ParallelGC,
        G1GC,
        ShenandoahGC,
        ZGC
    }

    public static Options buildOptionsForFilterTesting(String filter, String imageName, ImageSize size){
        return new OptionsBuilder()
                .include(BaseBenchmark.class.getSimpleName())
                .param("pathToFile", "src/main/resources/imgs/"+ size.name() + "/" + imageName + ".jpg")
                .param("filterName", filter)
                .warmupIterations(3)
                .forks(1)
                .resultFormat(ResultFormatType.CSV)
                .result("report/benchmark_results/"+ getSimpleFilterName(filter) + "/" + size.name() + "/" + imageName + "_" + System.currentTimeMillis() + ".csv")
                .build();
    }

    public static Options buildOptionsForGcTesting(String filter, GCType gc){
        return new OptionsBuilder()
                .include(BaseBenchmark.class.getSimpleName())
                .param("pathToFile", "src/main/resources/imgs/"+ ImageSize.big.name() + "/4k_background.jpg")
                .param("filterName", filter)
                .jvmArgs("-XX:+Use" + gc.name())
                .warmupIterations(3)
                .forks(1)
                .resultFormat(ResultFormatType.CSV)
                .result("report/benchmark_results/"+ getSimpleFilterName(filter) + "/" + gc.name().toLowerCase().replace("gc", "") + "/" + System.currentTimeMillis() + ".csv")
                .build();
    }

    private static String getSimpleFilterName(String fqn){
        return fqn
            .replace("pt.ipp.isep.dei.sismd.filters.", "")
            .replace("Filter", "")
            .toLowerCase();
    }

    public static void runBaseBenchmark(String filter, boolean testGC) throws RunnerException {
        Options smallImage = buildOptionsForFilterTesting(
                filter,
                "turtle",
                ImageSize.small);
        new Runner(smallImage).run();

        Options bigImage = buildOptionsForFilterTesting(
                filter,
                "4k_background",
                ImageSize.big);
        new Runner(bigImage).run();

        Options hugeImage = buildOptionsForFilterTesting(
                filter,
                "8k",
                ImageSize.huge);
        new Runner(hugeImage).run();

        if(testGC) {
            Options serialGC = buildOptionsForGcTesting(filter, GCType.SerialGC);
            new Runner(serialGC).run();

            Options parallelGC = buildOptionsForGcTesting(filter, GCType.ParallelGC);
            new Runner(parallelGC).run();

            Options g1GC = buildOptionsForGcTesting(filter, GCType.G1GC);
            new Runner(g1GC).run();

            Options shenandoahGC = buildOptionsForGcTesting(filter, GCType.ShenandoahGC);
            new Runner(shenandoahGC).run();

            Options zGC = buildOptionsForGcTesting(filter, GCType.ZGC);
            new Runner(zGC).run();
        }
    }
}

