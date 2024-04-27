package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GlassFilterExecutorMultithreaded implements FilterExecutor {

    private int distance = 40;
    private int numberOfThreads = 10;
    private AtomicInteger numberOfRowsLeft;

    public GlassFilterExecutorMultithreaded(int distance, int numberOfThreads) {
        this.distance = distance;
        this.numberOfThreads = numberOfThreads;
    }

    public GlassFilterExecutorMultithreaded() {
    }

    public int getRow() {
        return this.numberOfRowsLeft.getAndAdd(-1);
    }

    @Override
    public Image apply(Image image) {
        int numberOfRows = image.height();
        this.numberOfRowsLeft = new AtomicInteger(numberOfRows - 1);
        int numberOfColumns = image.width();

        Color[][] pixelMatrix = new Color[numberOfRows][numberOfColumns];
        
        List<Thread> threadList = new ArrayList<>();
        for(int i = 0; i < numberOfThreads; i++) {
            threadList.add(new Thread(new GlassFilterRunnable(numberOfColumns, numberOfRows, pixelMatrix, image)));
        }

        for (Thread thread : threadList) {
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new Image(pixelMatrix);
    }

    private class GlassFilterRunnable implements Runnable {

        private final Color[][] sharedOutput;
        private final Image imageToProcess;

        private final int numberOfColumns;
        private final int numberOfRows;

        private GlassFilterRunnable(int numberOfColumns, int numberOfRows, Color[][] sharedOutput, Image imageToProcess) {
            this.sharedOutput = sharedOutput;
            this.imageToProcess = imageToProcess;
            this.numberOfColumns = numberOfColumns;
            this.numberOfRows = numberOfRows;
        }

        @Override
        public void run() {
            Random rand = new Random();
            int row;
            while((row = getRow()) >= 0) {
                for (int j = 0; j < numberOfColumns; j++) {
                    int offsetI = rand.nextInt(distance) - distance * 2;
                    int offsetJ = rand.nextInt(distance) - distance * 2;

                    int randomI = Math.min(Math.max(0,row + offsetI), numberOfRows - 1);
                    int randomJ = Math.min(Math.max(0,j + offsetJ), numberOfColumns - 1);

                    Color color = imageToProcess.obtainPixel(randomI, randomJ);

                    sharedOutput[row][j]=color;
                }
            }
        }
    }
}
