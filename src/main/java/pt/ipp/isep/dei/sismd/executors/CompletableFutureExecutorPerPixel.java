package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExecutorPerPixel implements FilterExecutor {

    private final Filter filter;

    public CompletableFutureExecutorPerPixel(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Image apply(Image image) {
        final Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int x = 0; x < image.height(); x++) {
            final int finalX = x;
            for (int y = 0; y < image.width(); y++) {
                final int finalY = y;
                CompletableFuture.runAsync(() -> {
                        final Color filteredPixel = filter.apply(finalX, finalY, image);
                        pixelMatrix[finalX][finalY] = filteredPixel;
                });
            }
        }
        if(!ForkJoinPool.commonPool().awaitQuiescence(100, TimeUnit.SECONDS)){
            System.out.println("Timeout occurred.");
        }
        return new Image(pixelMatrix);
    }
}
