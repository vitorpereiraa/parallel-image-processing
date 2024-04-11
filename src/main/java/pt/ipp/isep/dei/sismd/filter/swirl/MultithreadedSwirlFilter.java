package pt.ipp.isep.dei.sismd.filter.swirl;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.ImageFilter;

import java.util.concurrent.CountDownLatch;

public class MultithreadedSwirlFilter implements ImageFilter, SwirlFilter {

    @Override
    public Image apply(Image image) {
        Image result = Image.copyOf(image);
        final int numberOfThreads = Runtime.getRuntime().availableProcessors();
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        final ImageCoordinate centerCoordinate = getCenterCoordinate(image.width(), image.height());
        final int sliceWidth = image.width() / numberOfThreads;
        final int sliceHeight = image.height();

        for (int i = 0; i < numberOfThreads ; i++) {
            final int sliceStartX = i * sliceWidth;
            final int sliceEndX = (i == numberOfThreads - 1) ? image.width() : (i + 1) * sliceWidth;
            final Thread threadToProcessSlice = new Thread(() -> {
                for (int x = sliceStartX; x < sliceEndX ; x++) {
                    for (int y = 0; y < sliceHeight; y++) {
                        final ImageCoordinate currentCoordinate = new ImageCoordinate(x, y);
                        final ImageCoordinate swirlCoordinate = getSwirlCoordinateOf(currentCoordinate, centerCoordinate);
                        if(swirlCoordinate.x() >= 0 && swirlCoordinate.x() < image.width() && swirlCoordinate.y() >= 0 && swirlCoordinate.y() < image.height()) {
                            final Color pixel = image.obtainPixel(swirlCoordinate.x(), swirlCoordinate.y());
                            result.updatePixel(x,y, pixel);
                        }
                    }
                }
                countDownLatch.countDown();
            });
            threadToProcessSlice.start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e); // todo: is it relevant to be ready for interruptions?
        }
        return result;
    }
}
