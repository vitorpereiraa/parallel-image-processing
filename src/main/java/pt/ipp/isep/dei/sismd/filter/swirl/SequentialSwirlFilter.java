package pt.ipp.isep.dei.sismd.filter.swirl;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.ImageFilter;

public class SequentialSwirlFilter implements ImageFilter, SwirlFilter {

    @Override
    public Image apply(Image image) {
        Image result = Image.copyOf(image);
        final ImageCoordinate centerCoordinate = getCenterCoordinate(image.width(), image.height());

        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                final ImageCoordinate currentCoordinate = new ImageCoordinate(x, y);
                final ImageCoordinate swirlCoordinate = getSwirlCoordinateOf(currentCoordinate, centerCoordinate);
                if(swirlCoordinate.x() >= 0 && swirlCoordinate.x() < image.width() && swirlCoordinate.y() >= 0 && swirlCoordinate.y() < image.height()) {
                    final Color pixel = image.obtainPixel(swirlCoordinate.x(), swirlCoordinate.y());
                    result.updatePixel(x,y, pixel);
                }
            }
        }
        return result;
    }
}
