package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorsExecutorPerSlice implements FilterExecutor {

    private final ExecutorService threadPool;

    private final Filter filter;

    public ExecutorsExecutorPerSlice(Filter filter) {
        this.threadPool = Executors.newCachedThreadPool();
        this.filter = filter;
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        final int sliceHeight = image.height() / numberOfThreads;
        for (int i = 0; i < numberOfThreads; i++) {
            final int sliceStartX = i * sliceHeight;
            final int sliceEndX = (i == numberOfThreads - 1) ? image.height() : (i + 1) * sliceHeight;
            threadPool.submit(() -> {
                for (int x = sliceStartX; x < sliceEndX; x++) {
                    for (int y = 0; y < image.width(); y++) {
                        final Color filteredPixel = filter.apply(x, y, image);
                        pixelMatrix[x][y] = filteredPixel;
                    }
                }
            });
        }
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(100, TimeUnit.SECONDS)){
                System.out.println("Timeout occurred.");
            }
        } catch (InterruptedException ignored) { }

        return new Image(pixelMatrix);
    }
}
