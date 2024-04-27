package pt.ipp.isep.dei.sismd.lixo.multithreaded;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.lixo.BlurFilterExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ExecutorBlurFilterExecutor extends BlurFilterExecutor {

    private class BlurTask implements Callable<Color[]> {

        private int i;

        private Image image;


        public BlurTask(int i, Image image) {
            this.i = i;
            this.image = image;
        }


        @Override
        public Color[] call() {
            Color[] row = new Color[image.width()];
            for (int j = 0; j < image.width(); j++) {
                row[j] = calculateBlur(i, j, image);
            }
            return row;
        }
    }

    private int numThreds;
    private ExecutorService service;


    public ExecutorBlurFilterExecutor() {
        this(Runtime.getRuntime().availableProcessors()-1);
    }

    public ExecutorBlurFilterExecutor(int numThreds) {
        this.numThreds = numThreds;
        this.service = new ForkJoinPool(numThreds);
    }


    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        Map<Integer, Future<Color[]>> scheduler = new HashMap<>();
        for (int i = 0; i < image.height(); i++) {
            try {
                scheduler.put(i, service.submit(new BlurTask(i, image)));
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
