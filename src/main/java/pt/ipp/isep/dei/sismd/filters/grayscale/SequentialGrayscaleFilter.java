package pt.ipp.isep.dei.sismd.filters.grayscale;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.filters.FilterExecutor;

public class SequentialGrayscaleFilter implements FilterExecutor, GrayscaleFilter {

    @Override
    public Image apply(Image image) {
        final Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int i = 0; i < image.height(); i++) {
            for (int j = 0; j < image.width(); j++) {
                final Color grayscalePixel = filter(i,j,image);
                pixelMatrix[i][j] = grayscalePixel;
            }
        }
        return new Image(pixelMatrix);
    }
}
