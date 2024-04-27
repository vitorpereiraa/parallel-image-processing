package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinExecutor implements FilterExecutor {

    private final Filter filterAlgorithm;

    private final int threshold;

    public ForkJoinExecutor(Filter filterAlgorithm, int threshold) {
        this.filterAlgorithm = filterAlgorithm;
        this.threshold = threshold;
    }

    public ForkJoinExecutor(Filter filterAlgorithm) {
        this.filterAlgorithm = filterAlgorithm;
        this.threshold = 100;
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new FilterTask(0, image.height(), 0, image.width(), pixelMatrix, image, filterAlgorithm));
        pool.shutdown();
        return new Image(pixelMatrix);
    }

    private class FilterTask extends RecursiveAction {
        private final int startRow;
        private final int endRow;
        private final int startCol;
        private final int endCol;
        private final Color[][] sharedOutput;
        private final Image imageToProcess;
        private final Filter filter;

        public FilterTask(int startRow, int endRow, int startCol, int endCol,
                          Color[][] sharedOutput, Image imageToProcess, Filter filter) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.startCol = startCol;
            this.endCol = endCol;
            this.sharedOutput = sharedOutput;
            this.imageToProcess = imageToProcess;
            this.filter = filter;
        }

        @Override
        protected void compute() {
            if ((endRow - startRow) * (endCol - startCol) <= threshold) {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = startCol; j < endCol; j++) {
                        sharedOutput[i][j] = filter.apply(i, j, imageToProcess);
                    }
                }
            } else {
                int midRow = (startRow + endRow) / 2;
                int midCol = (startCol + endCol) / 2;

                invokeAll(
                        new FilterTask(startRow, midRow, startCol, midCol, sharedOutput, imageToProcess, filter),
                        new FilterTask(startRow, midRow, midCol, endCol, sharedOutput, imageToProcess, filter),
                        new FilterTask(midRow, endRow, startCol, midCol, sharedOutput, imageToProcess, filter),
                        new FilterTask(midRow, endRow, midCol, endCol, sharedOutput, imageToProcess, filter)
                );
            }
        }
    }
}
