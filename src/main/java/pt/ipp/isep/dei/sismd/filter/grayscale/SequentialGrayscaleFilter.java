package pt.ipp.isep.dei.sismd.filter.grayscale;

import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.filter.ImageFilter;

public class SequentialGrayscaleFilter implements ImageFilter, GrayscaleFilter {

    @Override
    public Image apply(Image image) {
        Image result = Image.copyOf(image);
        for (int i = 0; i < image.width(); i++) {
            for (int j = 0; j < image.height(); j++) {
                final Color pixel = image.obtainPixel(i,j);
                final Color grayscalePixel = applyGrayscale(pixel);
                result.updatePixel(i, j, grayscalePixel);
            }
        }
        return result;
    }
}
