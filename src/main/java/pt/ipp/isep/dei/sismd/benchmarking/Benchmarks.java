package pt.ipp.isep.dei.sismd.benchmarking;

import org.openjdk.jmh.annotations.*;
import pt.ipp.isep.dei.sismd.Utils;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.GlassFilter;
import pt.ipp.isep.dei.sismd.filter.GlassFilterMultithreaded;

import java.io.File;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Benchmarks {

    @Param({"10"})
    private int iterations;

    private String filepath = "C:/Users/domin/Desktop/Aulas/MEI_1A2S/SISMD/SISMD-2023-2024-S/src/main/resources/4k_background.jpg";

    private Image image;

    @Setup
    public void setup(){
        image = Utils.loadImage(filepath);
    }

    @Benchmark
    @Warmup(iterations = 0)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark_glass_filter_sequential() {
        for (int i = 0; i < iterations; i++) {
            Image transformedImage = new GlassFilter(20).apply(image);
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    @Warmup(iterations = 0)
    @BenchmarkMode(Mode.AverageTime)
    public void benchmark_glass_filter_multithreaded() {
        for (int i = 0; i < iterations; i++) {
            Image transformedImage = new GlassFilterMultithreaded(20, 5).apply(image);
        }
    }
}
