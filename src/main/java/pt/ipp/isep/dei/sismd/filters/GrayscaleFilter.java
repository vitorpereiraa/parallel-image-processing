package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

public class GrayscaleFilter implements Filter {

    @Override
    public Color apply(int i, int j, Image image) {
        Color pixel = image.obtainPixel(i, j);
        int r = pixel.red();
        int g = pixel.green();
        int b = pixel.blue();

        int sum = r + g + b;
        int avg = sum / 3;

        return new Color(avg, avg, avg);
    }
}
