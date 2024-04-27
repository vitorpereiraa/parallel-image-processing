package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

import java.util.function.Predicate;


public class ConditionalBlurFilter implements Filter {


    private int blurEffect;
    private Predicate<Color> filterCondition;

    public ConditionalBlurFilter(int blurEffect, Predicate<Color> filterCondition) {
        this.blurEffect = blurEffect;
        this.filterCondition = filterCondition;
    }

    public ConditionalBlurFilter(Predicate<Color> filterCondition) {
        blurEffect = 1;
        this.filterCondition = filterCondition;
    }

    public ConditionalBlurFilter() {
        blurEffect = 1;
        this.filterCondition = color -> color.red() > color.blue() && color.red() > color.green();
    }


    @Override
    public Color apply(int i, int j, Image image) {

        if (!filterCondition.test(image.obtainPixel(i, j))) return image.obtainPixel(i, j);


        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;
        int totalPixels = 0;

        for (int h = Math.max(i - this.blurEffect, 0); h <= Math.min(i + this.blurEffect, image.height() - 1); h++) {
            for (int w = Math.max(j - this.blurEffect, 0); w <= Math.min(j + this.blurEffect, image.width() - 1); w++) {
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