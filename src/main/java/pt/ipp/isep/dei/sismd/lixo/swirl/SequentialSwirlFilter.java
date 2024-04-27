package pt.ipp.isep.dei.sismd.lixo.swirl;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.FilterExecutor;
import pt.ipp.isep.dei.sismd.filters.SwirlFilter;

public class SequentialSwirlFilter implements FilterExecutor, SwirlFilter {

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int x = 0; x < image.height(); x++) {
            for (int y = 0; y < image.width(); y++) {
                final Color swirlPixel = apply(x, y, image);
                pixelMatrix[x][y] = swirlPixel;
            }
        }
        return new Image(pixelMatrix);
    }
}
