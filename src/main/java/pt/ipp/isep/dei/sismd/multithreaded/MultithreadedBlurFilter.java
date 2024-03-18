package pt.ipp.isep.dei.sismd.multithreaded;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.BluerFilter;

import java.util.ArrayList;
import java.util.List;

public class MultithreadedBlurFilter extends BluerFilter {

    private final int numberOfThreads;

    public MultithreadedBlurFilter(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    private class AlgorithmRunner implements Runnable {

        private final int lowerWidthBound;
        private final int higherWidthBound;

        private final Color[][] sharedOutput;

        private final Image imageToProcess;

        public AlgorithmRunner(int lowerWidthBound, int higherWidthBound,
                               Color[][] sharedOutput, Image imageToProcess) {
            this.lowerWidthBound = lowerWidthBound;
            this.higherWidthBound = higherWidthBound;
            this.sharedOutput = sharedOutput;
            this.imageToProcess = imageToProcess;
        }

        @Override
        public void run() {
            for (int i = 0; i < imageToProcess.height(); i++) {
                for (int j = lowerWidthBound; j < higherWidthBound; j++) {
                    sharedOutput[i][j] = calculateBlur(i, j, imageToProcess);
                }
            }
        }
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


    private List<Thread> createThreads(ThreadGroup localGroup,
                                       Image image,
                                       Color[][] sharedMatrix) {
        List<Thread> result = new ArrayList<>(numberOfThreads);
        int range = image.width() / numberOfThreads;

        int lower = 0;
        int higher = range;

        for (int i = 0; i < numberOfThreads; i++) {
            if (i==numberOfThreads-1) higher= image.width();
            result.add(new Thread(localGroup, new AlgorithmRunner(lower, higher, sharedMatrix, image)));
            lower = higher;
            higher += range;
        }
        return result;
    }


}
