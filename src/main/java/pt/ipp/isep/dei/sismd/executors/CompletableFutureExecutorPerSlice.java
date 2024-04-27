package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExecutorPerSlice implements FilterExecutor {

    private final Filter filter;

    public CompletableFutureExecutorPerSlice(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Image apply(Image image) {
        final Color[][] pixelMatrix = new Color[image.height()][image.width()];
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        final int sliceHeight = image.height() / numberOfThreads;
        for (int i = 0; i < numberOfThreads; i++) {
            final int sliceStartX = i * sliceHeight;
            final int sliceEndX = (i == numberOfThreads - 1) ? image.height() : (i + 1) * sliceHeight;
            CompletableFuture.runAsync(() -> {
                for (int x = sliceStartX; x < sliceEndX; x++) {
                    for (int y = 0; y < image.width(); y++) {
                        final Color filteredPixel = filter.apply(x, y, image);
                        pixelMatrix[x][y] = filteredPixel;
                    }
                }
            });
        }
        if(!ForkJoinPool.commonPool().awaitQuiescence(100, TimeUnit.SECONDS)){
            System.out.println("Timeout occurred.");
        }
        return new Image(pixelMatrix);
    }
}
