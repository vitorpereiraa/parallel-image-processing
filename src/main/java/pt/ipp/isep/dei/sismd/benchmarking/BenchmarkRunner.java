package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.runner.RunnerException;
import pt.ipp.isep.dei.sismd.filters.*;

public class BenchmarkRunner extends BaseBenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
//        runBaseBenchmark(BrighterFilter.class.getName(), true);
//        runBaseBenchmark(GrayscaleFilter.class.getName(), true);
//        runBaseBenchmark(SwirlFilter.class.getName(), true);
        runBaseBenchmark(ConditionalBlurFilter.class.getName(), true);
//        runBaseBenchmark(BlurFilter.class.getName(), true);
    }
}
