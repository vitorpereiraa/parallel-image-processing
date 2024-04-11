package pt.ipp.isep.dei.sismd.filter.grayscale;

import pt.ipp.isep.dei.sismd.domain.Color;

public interface GrayscaleFilter {

    default Color applyGrayscale(Color pixel) {
        int r = pixel.red();
        int g = pixel.green();
        int b = pixel.blue();

        int sum = r + g + b;
        int avg = sum/3;

        return new Color(avg, avg, avg);
    }
}
