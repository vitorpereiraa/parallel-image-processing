package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

import java.util.Random;

public class GlassFilter implements Filter {

    private int distance = 20;
    private final Random rand = new Random();
    int numberOfRows;
    int numberOfColumns;

    public GlassFilter(){};

    public GlassFilter(int distance) {
        this.distance = distance;
    }

    @Override
    public Color apply(int i, int j, Image imageToProcess){
        this.numberOfColumns = imageToProcess.width();
        this.numberOfRows = imageToProcess.height();
        int offsetI = rand.nextInt(distance) - distance * 2;
        int offsetJ = rand.nextInt(distance) - distance * 2;

        int randomI = Math.min(Math.max(0,i + offsetI), numberOfRows - 1);
        int randomJ = Math.min(Math.max(0,j + offsetJ), numberOfColumns - 1);

        return imageToProcess.obtainPixel(randomI, randomJ);
    }
}
