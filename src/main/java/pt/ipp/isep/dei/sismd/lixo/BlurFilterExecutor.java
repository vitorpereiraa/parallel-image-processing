package pt.ipp.isep.dei.sismd.lixo;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.FilterExecutor;

public class BlurFilterExecutor implements FilterExecutor {


    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];

        for (int i = 0; i < image.height(); i++) {
            for (int j = 0; j < image.width(); j++) {
                pixelMatrix[i][j] = calculateBlur(i, j, image);
            }
        }

        return new Image(pixelMatrix);
    }

    protected Color calculateBlur(int i, int j, Image image) {
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        int totalPixels = 0;

        for (int h = Math.max(i - 1, 0); h <= Math.min(i + 1, image.height()-1); h++) {
            for (int w = Math.max(j - 1, 0); w <= Math.min(j + 1, image.width()-1); w++) {
                redSum += image.obtainPixel(h, w).red();
                greenSum += image.obtainPixel(h, w).green();
                blueSum += image.obtainPixel(h, w).blue();
                totalPixels++;
            }
        }
        Color result = new Color(redSum / totalPixels, greenSum / totalPixels, blueSum / totalPixels);
        return result;
    }
}
