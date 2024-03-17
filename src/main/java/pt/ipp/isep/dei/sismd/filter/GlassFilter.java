package pt.ipp.isep.dei.sismd.filter;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

import java.util.Random;

public class GlassFilter implements ImageFilter {

    private int distance = 5;

    public GlassFilter(int distance) {
        this.distance = distance;
    }

    public GlassFilter() {}

    @Override
    public Image apply(Image image) {
        Random rand = new Random();
        int numberOfRows = image.height();
        int numberOfColumns = image.width();

        Color[][] pixelMatrix = new Color[numberOfRows][numberOfColumns];
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                int randomDistance = rand.nextInt(distance) - distance * 2;

                int randomI = Math.min(Math.max(0,i + randomDistance), numberOfRows - 1);
                int randomJ = Math.min(Math.max(0,j + randomDistance), numberOfColumns - 1);

                Color color = image.obtainPixel(randomI, randomJ);

                pixelMatrix[i][j]=color;
            }
        }
        return new Image(pixelMatrix);
    }
}
