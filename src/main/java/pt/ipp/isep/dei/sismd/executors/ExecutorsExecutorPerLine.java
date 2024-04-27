package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.util.concurrent.*;

public class ExecutorsExecutorPerLine implements FilterExecutor {

    private final ExecutorService threadPool;

    private final Filter filter;

    public ExecutorsExecutorPerLine(Filter filter) {
        this.threadPool = Executors.newCachedThreadPool();
        this.filter = filter;
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int x = 0; x < image.height(); x++) {
            final int finalX = x;
            threadPool.submit(() -> {
                for (int y = 0; y < image.width() ; y++) {
                    final Color filteredPixel = filter.apply(finalX,y,image);
                    pixelMatrix[finalX][y] = filteredPixel ;
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
