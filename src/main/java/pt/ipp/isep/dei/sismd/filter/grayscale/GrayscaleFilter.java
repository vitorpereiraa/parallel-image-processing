package pt.ipp.isep.dei.sismd.filter.grayscale;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.Filter;

public interface GrayscaleFilter extends Filter {

    @Override
    default Color filter(int i, int j, Image image) {
        Color pixel = image.obtainPixel(i, j);
        int r = pixel.red();
        int g = pixel.green();
        int b = pixel.blue();

        int sum = r + g + b;
        int avg = sum / 3;

        return new Color(avg, avg, avg);
    }
}
