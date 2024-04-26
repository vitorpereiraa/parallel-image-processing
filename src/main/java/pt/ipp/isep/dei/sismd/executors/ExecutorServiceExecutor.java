package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.Filter;
import pt.ipp.isep.dei.sismd.filter.FilterExecutor;

import java.util.concurrent.*;

public class ExecutorServiceExecutor implements FilterExecutor {

    private int numThreds;
    private ExecutorService service;

    private Filter filter;

    public ExecutorServiceExecutor(int numThreds, Filter filter) {
        this.numThreds = numThreds;
        this.service = Executors.newCachedThreadPool();
        this.filter = filter;
    }

    public ExecutorServiceExecutor(Filter filter) {
        this(Runtime.getRuntime().availableProcessors(), filter);
    }

    private class Task implements Runnable {

        private int row;

        private Image image;

        private Filter filter;

        private Color[][] sharedMatrix;


        public Task(int row, Filter filter, Image image, Color[][] sharedMatrix) {
            this.row = row;
            this.image = image;
            this.filter = filter;
            this.sharedMatrix = sharedMatrix;
        }

        @Override
        public void run() {
            Color[] row = sharedMatrix[this.row];
            for (int j = 0; j < image.width(); j++) {
                row[j] = filter.filter(this.row, j, image);
            }
        }
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int i = 0; i < image.height(); i++) {
            try {
               service.submit(new Task(i, filter, image, pixelMatrix));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        while (true) {
            try {

                boolean r = service.awaitTermination(5, TimeUnit.MINUTES);
                if (!r) System.out.println("Oh oh! It did not stop!");
                break;
            } catch (InterruptedException e) {
                System.out.println("NO!");
            }
        }
        return new Image(pixelMatrix);
    }
}
