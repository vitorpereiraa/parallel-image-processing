package pt.ipp.isep.dei.sismd.filter;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

public class BrighterFilter implements ImageFilter {

    public static final int MAX_HUE_VALUE = 255;
    private final int brightness;

    public BrighterFilter(int brightness) {
        this.brightness = brightness;
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int i = 0; i < image.height(); i++) {
            for (int j = 0; j < image.width(); j++) {
                pixelMatrix[i][j] = bright(i, j, image);
            }
        }
        return new Image(pixelMatrix);
    }


    protected Color bright(int i, int j, Image image) {
        Color color = image.obtainPixel(i, j);
        return new Color(Math.min(color.red() + brightness, MAX_HUE_VALUE),
                Math.min(color.green() + brightness, MAX_HUE_VALUE),
                Math.min(color.blue() + brightness, MAX_HUE_VALUE));
    }


}
