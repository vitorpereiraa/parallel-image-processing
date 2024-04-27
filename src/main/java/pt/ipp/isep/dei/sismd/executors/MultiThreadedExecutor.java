package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;
import pt.ipp.isep.dei.sismd.filters.FilterExecutor;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedExecutor implements FilterExecutor {

    private final int numberOfThreads;

    private Filter filterAlgorithm;

    public MultiThreadedExecutor(int numberOfThreads, Filter filterAlgorithm) {
        this.numberOfThreads = numberOfThreads;
        this.filterAlgorithm = filterAlgorithm;
    }

    public MultiThreadedExecutor(Filter filterAlgorithm) {
        this(Runtime.getRuntime().availableProcessors()-1, filterAlgorithm);
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        ThreadGroup group = new ThreadGroup("MultithreadedBlurFilter");
        List<Thread> threads = createThreads(group, image, pixelMatrix);
        threads.forEach(Thread::start);
        while (!threads.isEmpty()) {
            try {
                threads.getFirst().join();
                threads.removeFirst();
            } catch (InterruptedException e) {
                System.err.println("[WARNING] Error while applying blur...");
                System.err.println(e);
                System.out.println("Continuing waiting...");
            }
        }
        return new Image(pixelMatrix);
    }

    private class AlgorithmRunner implements Runnable {

        private final int lowerWidthBound;
        private final int higherWidthBound;

        private final Color[][] sharedOutput;

        private final Image imageToProcess;

        private final Filter filter;

        public AlgorithmRunner(int lowerWidthBound, int higherWidthBound,
                               Color[][] sharedOutput, Image imageToProcess, Filter filter) {
            this.lowerWidthBound = lowerWidthBound;
            this.higherWidthBound = higherWidthBound;
            this.sharedOutput = sharedOutput;
            this.imageToProcess = imageToProcess;
            this.filter = filter;
        }

        @Override
        public void run() {
            for (int i = 0; i < imageToProcess.height(); i++) {
                for (int j = lowerWidthBound; j < higherWidthBound; j++) {
                    sharedOutput[i][j] = this.filter.filter(i, j,imageToProcess);
                }
            }
        }
    }

    private List<Thread> createThreads(ThreadGroup localGroup,
                                       Image image,
                                       Color[][] sharedMatrix) {
        List<Thread> result = new ArrayList<>(numberOfThreads);
        int range = image.width() / numberOfThreads;

        int lower = 0;
        int higher = range;

        for (int i = 0; i < numberOfThreads; i++) {
            if (i==numberOfThreads-1) higher= image.width();
            result.add(new Thread(localGroup, new AlgorithmRunner(lower, higher, sharedMatrix, image,filterAlgorithm)));
            lower = higher;
            higher += range;
        }
        return result;
    }


}
