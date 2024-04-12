package pt.ipp.isep.dei.sismd.filter.swirl;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.FilterExecutor;

public class SequentialSwirlFilterExecutor implements FilterExecutor, SwirlFilter {

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        final ImageCoordinate centerCoordinate = getCenterCoordinate(image.width(), image.height());

        for (int x = 0; x < image.width(); x++) {
            for (int y = 0; y < image.height(); y++) {
                final ImageCoordinate currentCoordinate = new ImageCoordinate(x, y);
                final ImageCoordinate swirlCoordinate = getSwirlCoordinateOf(currentCoordinate, centerCoordinate);
                if(swirlCoordinate.x() >= 0 && swirlCoordinate.x() < image.width() && swirlCoordinate.y() >= 0 && swirlCoordinate.y() < image.height()) {
                    final Color pixel = image.obtainPixel(swirlCoordinate.x(), swirlCoordinate.y());
                    pixelMatrix[x][y] = pixel;
                }
            }
        }
        return new Image(pixelMatrix);
    }
}
