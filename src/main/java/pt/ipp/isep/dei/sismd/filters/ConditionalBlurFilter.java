package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.executors.FilterExecutor;

import java.util.ArrayList;
import java.util.List;


public class ConditionalBlurFilter implements FilterExecutor {

    int m = 1;
    @Override
    public Image apply(Image image) {
       Color[][] pixelMatrix = new Color[image.height()][image.width()];
        for (int i = 0; i < image.height()-1; i++) {
            for (int j = 0; j < image.width()-1; j++) {
                Color color = image.obtainPixel(i, j);
                List<Color> neighboursAndSelfValues = getNeighboursAndSelfValues(image, i, j, m);
                int r = neighboursAndSelfValues.stream().map(Color::red).reduce(0, Integer::sum) / neighboursAndSelfValues.size();
                int g = neighboursAndSelfValues.stream().map(Color::green).reduce(0, Integer::sum) / neighboursAndSelfValues.size();
                int b = neighboursAndSelfValues.stream().map(Color::blue).reduce(0, Integer::sum) / neighboursAndSelfValues.size();

                if(r > 200) {
                    pixelMatrix[i][j] = new Color(r, g, b);
                } else {
                    pixelMatrix[i][j] = new Color(color.red(), color.green(), color.blue());
                }
            }
        }

        return new Image(pixelMatrix);
    }

    private List<Color> getNeighboursAndSelfValues(Image image, int i, int j, int m) {

        List<Color> neighboursAndSelfValues = new ArrayList<>();
        for (int k = i - m; k <= i + m; k++) {
            for (int l = j - m; l <= j + m; l++) {
                if (k >= 0 && k < image.height() && l >= 0 && l < image.width() && (k != i || l != j)) {
                    neighboursAndSelfValues.add(image.obtainPixel(k, l));
                }
            }
        }
        return neighboursAndSelfValues;
    }
}