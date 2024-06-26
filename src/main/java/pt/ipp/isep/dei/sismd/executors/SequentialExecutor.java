package pt.ipp.isep.dei.sismd.executors;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filters.Filter;

public class SequentialExecutor implements FilterExecutor {
    
    private final Filter filter;

    public SequentialExecutor(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Image apply(Image image) {
        Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int i = 0; i < image.height(); i++) {
            for (int j = 0; j < image.width(); j++) {
                pixelMatrix[i][j] = filter.apply(i, j,image);
            }
        }
        return new Image(pixelMatrix);
    }
}
