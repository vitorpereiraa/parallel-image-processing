package pt.ipp.isep.dei.sismd.filter.grayscale;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.FilterExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class CompletableFutureGrayscaleFilter implements FilterExecutor, GrayscaleFilter {

    @Override
    public Image apply(Image image) {
        final Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int x = 0; x < image.height(); x++) {
            final int finalX = x;
            CompletableFuture.runAsync(() -> {
                for (int y = 0; y < image.width(); y++) {
                    final Color grayscalePixel = filter(finalX, y, image);
                    pixelMatrix[finalX][y] = grayscalePixel;
                }
            });
        }
        if(!ForkJoinPool.commonPool().awaitQuiescence(100, TimeUnit.SECONDS)){
            System.out.println("Timeout occurred.");
        }
        return new Image(pixelMatrix);
    }
}
