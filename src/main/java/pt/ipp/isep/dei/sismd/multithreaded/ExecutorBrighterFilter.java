package pt.ipp.isep.dei.sismd.multithreaded;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.bright.BrighterFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ExecutorBrighterFilter extends BrighterFilter {


    private class BrightTask implements Callable<Color[]> {

        private int i;

        private Image image;


        public BrightTask(int i, Image image) {
            this.i = i;
            this.image = image;
        }


        @Override
        public Color[] call() {
            Color[] row = new Color[image.width()];
            for (int j = 0; j < image.width(); j++) {
                row[j] = bright(i, j, image);
            }
            return row;
        }
    }

    private ExecutorService service;

    public ExecutorBrighterFilter(int brightness) {
        this(brightness, Runtime.getRuntime().availableProcessors() - 1);
    }

    public ExecutorBrighterFilter(int brightness, int numberOfThreads) {
        super(brightness);
        this.service = new ForkJoinPool(numberOfThreads);
    }
}
