package pt.ipp.isep.dei.sismd.lixo.grayscale;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.executors.FilterExecutor;
import pt.ipp.isep.dei.sismd.filters.GrayscaleFilter;

import java.util.concurrent.CountDownLatch;

public class MultithreadedGrayscaleFilter extends GrayscaleFilter implements FilterExecutor {

    @Override
    public Image apply(Image image) {
        final Color[][] pixelMatrix = new Color[image.height()][image.width()];
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        final int sliceHeight = image.height() / numberOfThreads;
        for (int i = 0; i < numberOfThreads ; i++) {
            final int sliceStartX = i * sliceHeight;
            final int sliceEndX = (i == numberOfThreads - 1) ? image.height() : (i + 1) * sliceHeight;
            final Thread threadToProcessSlice = new Thread(() -> {
                for (int x = sliceStartX; x < sliceEndX ; x++) {
                    for (int y = 0; y < image.width(); y++) {
                        final Color grayscalePixel = apply(x,y,image);
                        pixelMatrix[x][y] = grayscalePixel;
                    }
                }
                countDownLatch.countDown();
            });
            threadToProcessSlice.start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException ignored){}

        return new Image(pixelMatrix);
    }
}
