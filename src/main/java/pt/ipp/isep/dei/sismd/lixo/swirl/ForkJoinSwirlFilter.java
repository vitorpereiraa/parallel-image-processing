package pt.ipp.isep.dei.sismd.lixo.swirl;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.FilterExecutor;
import pt.ipp.isep.dei.sismd.filters.SwirlFilter;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class ForkJoinSwirlFilter implements FilterExecutor, SwirlFilter {

    @Override
    public Image apply(Image image) {
        final Color[][] pixelMatrix = new Color[image.height()][image.width()];
        final ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new ImageRecursiveAction(0, image.height(), 0, image.width(), image, pixelMatrix));
        forkJoinPool.shutdown();
        try {
            if (!forkJoinPool.awaitTermination(100, TimeUnit.SECONDS)){
                System.out.println("Timeout occurred.");
            }
        } catch (InterruptedException ignored) { }
        return new Image(pixelMatrix);
    }

    private class ImageRecursiveAction extends RecursiveAction {

        final int startX;
        final int endX;
        final int startY;
        final int endY;
        final Image originalImage;
        final Color[][] pixelMatrix;

        public ImageRecursiveAction(int startX, int endX, int startY, int endY, Image originalImage, Color[][] pixelMatrix){
            this.startX = startX;
            this.endX = endX;
            this.startY = startY;
            this.endY = endY;
            this.originalImage = originalImage;
            this.pixelMatrix = pixelMatrix;
        }

        @Override
        protected void compute() {
            if(startX <= originalImage.height()/16 || endX <= originalImage.height()/16) {
                for (int x = startX; x < endX; x++) {
                    for (int y = startY; y < endY; y++) {
                        pixelMatrix[x][y] = apply(x, y, originalImage);
                    }
                }
            } else {
                var q1 = new ImageRecursiveAction(startX, endX/2, startY, endY/2, originalImage, pixelMatrix);
                q1.fork();
                var q2 = new ImageRecursiveAction(startX, endX/2, startY/2, endY, originalImage, pixelMatrix);
                q2.fork();
                var q3 = new ImageRecursiveAction(startX/2, endX, startY, endY/2, originalImage, pixelMatrix);
                q3.fork();
                var q4 = new ImageRecursiveAction(startX/2, endX, startY/2, endY, originalImage, pixelMatrix);
                q4.fork();
            }
        }
    }
}
