package pt.ipp.isep.dei.sismd.domain;

import pt.ipp.isep.dei.sismd.Utils;

public class Image {
    private final Color[][] pixelMatrix;

    public Image(Color[][] pixelMatrix) {
        this.pixelMatrix = pixelMatrix;
    }

    public Color[][] getPixelMatrix() {
        return pixelMatrix;
    }

    public Color obtainPixel(int i, int j) {
        return pixelMatrix[i][j];
    }

    public void updatePixel(int i, int j, Color color) {
        pixelMatrix[i][j] = color;
    }

    public int height() {
        return pixelMatrix.length;
    }

    public int width() {
        return pixelMatrix[0].length;
    }

    public static Image copyOf(Image image) {
        return new Image(Utils.copyImage(image.getPixelMatrix()));
    }
}
