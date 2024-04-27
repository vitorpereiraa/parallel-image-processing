package pt.ipp.isep.dei.sismd.domain;

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

    public int height() {
        return pixelMatrix.length;
    }

    public int width() {
        return pixelMatrix[0].length;
    }
}
