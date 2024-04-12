package pt.ipp.isep.dei.sismd.filter.grayscale;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.filter.FilterExecutor;

import java.util.concurrent.CountDownLatch;

public class MultithreadedGrayscaleFilterExecutor implements FilterExecutor, GrayscaleFilter {

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        final int sliceWidth = image.width() / numberOfThreads;
        final int sliceHeight = image.height();
        for (int i = 0; i < numberOfThreads ; i++) {
            final int sliceStartX = i * sliceWidth;
            final int sliceEndX = (i == numberOfThreads - 1) ? image.width() : (i + 1) * sliceWidth;
            final Thread threadToProcessSlice = new Thread(() -> {
                for (int x = sliceStartX; x < sliceEndX ; x++) {
                    for (int y = 0; y < sliceHeight; y++) {
                        final Color grayscalePixel = filter(y,x,image);
                        pixelMatrix[y][x]=grayscalePixel;
                    }
                }
                countDownLatch.countDown();
            });
            threadToProcessSlice.start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e); // todo: is it relevant to be ready for interruptions? Yes! If pixel is interrupted mid operations because of an error or low memory, prompting the garbage collector to activate, the filter must go on
        }
        return new Image(pixelMatrix);
    }
}
