package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.Filter;
import pt.ipp.isep.dei.sismd.filter.FilterExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolExecutor implements FilterExecutor {

    private int numThreds;
    private ExecutorService service;

    private Filter filter;

    public ThreadPoolExecutor(int numThreds, Filter filter) {
        this.numThreds = numThreds;
        this.service = new ForkJoinPool(numThreds);
        this.filter = filter;
    }

    public ThreadPoolExecutor(Filter filter) {
        this(Runtime.getRuntime().availableProcessors(), filter);
    }

    private class Task implements Callable<Color[]> {

        private int row;

        private Image image;

        private Filter filter;


        public Task(int row, Filter filter, Image image) {
            this.row = row;
            this.image = image;
            this.filter = filter;
        }


        @Override
        public Color[] call() {
            Color[] row = new Color[image.width()];
            for (int j = 0; j < image.width(); j++) {
                row[j] = filter.filter(this.row, j, image);
            }
            return row;
        }
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        Map<Integer, Future<Color[]>> scheduler = new HashMap<>();
        for (int i = 0; i < image.height(); i++) {
            try {
                scheduler.put(i, service.submit(new Task(i, filter, image)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (Map.Entry<Integer, Future<Color[]>> integerFutureEntry : scheduler.entrySet()) {
            try {
                pixelMatrix[integerFutureEntry.getKey()] = integerFutureEntry.getValue().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return new Image(pixelMatrix);
    }
}
