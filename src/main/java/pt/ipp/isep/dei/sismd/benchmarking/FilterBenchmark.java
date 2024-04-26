package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.annotations.*;
import pt.ipp.isep.dei.sismd.Utils;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.MultiThreadedExecutor;
import pt.ipp.isep.dei.sismd.executors.SequentialExecutor;
import pt.ipp.isep.dei.sismd.filter.Filter;
import pt.ipp.isep.dei.sismd.filter.grayscale.*;

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
        return new MultiThreadedExecutor(filter).apply(image);
    }
}
