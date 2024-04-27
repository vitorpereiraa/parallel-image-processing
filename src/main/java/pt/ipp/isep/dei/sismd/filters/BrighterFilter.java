package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

public class BrighterFilter implements Filter {

    public static final int MAX_HUE_VALUE = 255;
    private final int brightness;

    public BrighterFilter(int brightness) {
        this.brightness = brightness;
    }

    public BrighterFilter() {
        this.brightness = 100;
    }

    protected Color bright(int i, int j, Image image) {
        Color color = image.obtainPixel(i, j);
        return new Color(Math.min(color.red() + brightness, MAX_HUE_VALUE),
                Math.min(color.green() + brightness, MAX_HUE_VALUE),
                Math.min(color.blue() + brightness, MAX_HUE_VALUE));
    }


    @Override
    public Color apply(int i, int j, Image image) {
        Color color = image.obtainPixel(i, j);
        return new Color(Math.min(color.red() + brightness, MAX_HUE_VALUE),
                Math.min(color.green() + brightness, MAX_HUE_VALUE),
                Math.min(color.blue() + brightness, MAX_HUE_VALUE));
    }
}
