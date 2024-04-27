package pt.ipp.isep.dei.sismd.lixo.grayscale;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.FilterExecutor;
import pt.ipp.isep.dei.sismd.filters.GrayscaleFilter;

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
                    final Color grayscalePixel = apply(finalX, y, image);
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
