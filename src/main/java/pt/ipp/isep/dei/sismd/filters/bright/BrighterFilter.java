package pt.ipp.isep.dei.sismd.filters.bright;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

public class BrighterFilter implements Filter {

    public static final int MAX_HUE_VALUE = 255;
    private final int brightness;

    public BrighterFilter(int brightness) {
        this.brightness = brightness;
    }


    protected Color bright(int i, int j, Image image) {
        Color color = image.obtainPixel(i, j);
        return new Color(Math.min(color.red() + brightness, MAX_HUE_VALUE),
                Math.min(color.green() + brightness, MAX_HUE_VALUE),
                Math.min(color.blue() + brightness, MAX_HUE_VALUE));
    }


    @Override
    public Color filter(int i, int j, Image image) {
        Color color = image.obtainPixel(i, j);
        return new Color(Math.min(color.red() + brightness, MAX_HUE_VALUE),
                Math.min(color.green() + brightness, MAX_HUE_VALUE),
                Math.min(color.blue() + brightness, MAX_HUE_VALUE));
    }
}
