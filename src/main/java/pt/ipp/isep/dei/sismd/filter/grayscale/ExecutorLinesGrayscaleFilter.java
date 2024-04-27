package pt.ipp.isep.dei.sismd.filter.grayscale;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.FilterExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorPixelGrayscaleFilter implements FilterExecutor, GrayscaleFilter {

    @Override
    public Image apply(Image image) {
        final Color[][] pixelMatrix = new Color[image.height()][image.width()];
        final ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int x = 0; x < image.height(); x++) {
            for (int y = 0; y < image.width() ; y++) {
                final int finalX = x;
                final int finalY = y;
                threadPool.submit(() -> {
                        final Color grayscalePixel = filter(finalX,finalY,image);
                        pixelMatrix[finalX][finalY] = grayscalePixel;
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
