package pt.ipp.isep.dei.sismd.lixo.multithreaded;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.BrighterFilter;

import java.util.ArrayList;
import java.util.List;

public class MultithreadedBrighterFilter extends BrighterFilter {
    private final int numberOfThreads;

    public MultithreadedBrighterFilter(int brightness, int numberOfThreads) {
        super(brightness);
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
                    sharedOutput[i][j] = bright(i, j, imageToProcess);
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
            if (i==7) higher= image.width();
            result.add(new Thread(localGroup, new AlgorithmRunner(lower, higher, sharedMatrix, image)));
            lower = higher;
            higher += range;
        }
        return result;
    }



}
