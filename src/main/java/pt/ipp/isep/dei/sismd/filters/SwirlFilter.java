package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

public class SwirlFilter implements Filter {

    private int intensity; //in %

    public SwirlFilter(int intensity) {
        this.intensity = intensity;
    }

    public SwirlFilter() {
        this.intensity = 100;
    }


    private record ImageCoordinate(int x, int y) {
    }

    @Override
    public Color apply(int i, int j, Image image) {
        var currentCoordinate = new ImageCoordinate(i, j);
        var centerCoordinate = getCenterCoordinate(image.height(), image.width());
        var swirlCoordinate = getSwirlCoordinateOf(currentCoordinate, centerCoordinate);
        int validX = Math.max(0, Math.min(image.height() - 1, swirlCoordinate.x()));
        int validY = Math.max(0, Math.min(image.width() - 1, swirlCoordinate.y()));
        return image.obtainPixel(validX, validY);
    }

    private ImageCoordinate getCenterCoordinate(int height, int width) {
        int xCenterCoordinate = (height - 1) / 2;
        int yCenterCoordinate = (width - 1) / 2;
        return new ImageCoordinate(xCenterCoordinate, yCenterCoordinate);
    }

    private ImageCoordinate getSwirlCoordinateOf(ImageCoordinate coordinate, ImageCoordinate centerCoordinate) {
        int dx = coordinate.x() - centerCoordinate.x;
        int dy = coordinate.y() - centerCoordinate.y;
        double distance = Math.sqrt((dx * dx) + (dy * dy));
        double theta = (Math.PI / 256) * distance * (intensity/100.0);
        int xDash = (int) (dx * Math.cos(theta) - dy * Math.sin(theta) + centerCoordinate.x());
        int yDash = (int) (dx * Math.sin(theta) + dy * Math.cos(theta) + centerCoordinate.y());
        return new ImageCoordinate(xDash, yDash);
    }
}
