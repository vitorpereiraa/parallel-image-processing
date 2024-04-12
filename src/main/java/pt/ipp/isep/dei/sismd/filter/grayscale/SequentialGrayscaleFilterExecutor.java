package pt.ipp.isep.dei.sismd.filter.grayscale;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.filter.FilterExecutor;

public class SequentialGrayscaleFilterExecutor implements FilterExecutor, GrayscaleFilter {

    @Override
    public Image apply(Image image) {
        //Prefere unmodifiable objects over mutable objects, otherwise, lockess is impossible! If nothing less, this is one of the benefits of functional programming
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int i = 0; i < image.width(); i++) {
            for (int j = 0; j < image.height(); j++) {
                final Color pixel = image.obtainPixel(i,j);
                final Color grayscalePixel = filter(i,j,image);
                pixelMatrix[i][j]=grayscalePixel;
            }
        }
        return new Image(pixelMatrix);
    }
}
