package pt.ipp.isep.dei.sismd.lixo.swirl;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.FilterExecutor;
import pt.ipp.isep.dei.sismd.filters.SwirlFilter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorSwirlFilter extends SwirlFilter implements FilterExecutor {

    @Override
    public Image apply(Image image) {
        final Color[][] pixelMatrix = new Color[image.height()][image.width()];
        final ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int x = 0; x < image.height(); x++) {
            final int finalX = x;
            threadPool.submit(() -> {
                for (int y = 0; y < image.width() ; y++) {
                    final Color swirlFilter = apply(finalX,y,image);
                    pixelMatrix[finalX][y] = swirlFilter;
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
