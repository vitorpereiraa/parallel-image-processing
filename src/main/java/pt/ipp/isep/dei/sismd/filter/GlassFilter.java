package pt.ipp.isep.dei.sismd.filter;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

import java.util.Random;

public class GlassFilter implements Filter {

    private final int distance;
    private final Random rand = new Random();
    private final Image imageToProcess;
    int numberOfRows;
    int numberOfColumns;

    public GlassFilter(int distance, Image imageToProcess) {
        this.distance = distance;
        this.imageToProcess = imageToProcess;
        this.numberOfColumns = imageToProcess.width();
        this.numberOfRows = imageToProcess.height();
    }

    @Override
    public Color filter(int i, int j){
        int offsetI = rand.nextInt(distance) - distance * 2;
        int offsetJ = rand.nextInt(distance) - distance * 2;

        int randomI = Math.min(Math.max(0,i + offsetI), numberOfRows - 1);
        int randomJ = Math.min(Math.max(0,j + offsetJ), numberOfColumns - 1);

        return imageToProcess.obtainPixel(randomI, randomJ);
    }
}
