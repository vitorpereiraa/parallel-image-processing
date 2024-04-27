package pt.ipp.isep.dei.sismd.executors;

import org.junit.jupiter.api.Test;
import pt.ipp.isep.dei.sismd.Utils;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.BrighterFilter;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CompletableFutureExecutorPerSliceTest {

    public static final String fileName = "turtle.jpg";
    public static final String filePath = "src/main/resources/imgs/" + fileName;

    @Test
    void apply() {
        Image image = Utils.loadImage(new File(filePath));
        Filter brighter= new BrighterFilter(20);
        Image sequentialResult = new SequentialExecutor(brighter).apply(image);
        Image completableFutureResult = new CompletableFutureExecutorPerSlice(brighter).apply(image);
        assertTrue(Arrays.deepEquals(sequentialResult.getPixelMatrix(), completableFutureResult.getPixelMatrix()));
    }
}