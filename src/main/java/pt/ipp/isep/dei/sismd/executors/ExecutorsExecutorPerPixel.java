package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorsExecutorPerPixel implements FilterExecutor {

    private final ExecutorService threadPool;

    private final Filter filter;

    public ExecutorsExecutorPerPixel(Filter filter) {
        this.threadPool = Executors.newCachedThreadPool();
        this.filter = filter;
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int x = 0; x < image.height(); x++) {
            final int finalX = x;
            for (int y = 0; y < image.width() ; y++) {
                final int finalY = y;
                threadPool.submit(() -> {
                        final Color filteredPixel = filter.apply(finalX,finalY,image);
                        pixelMatrix[finalX][finalY] = filteredPixel ;
                });
            }
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
