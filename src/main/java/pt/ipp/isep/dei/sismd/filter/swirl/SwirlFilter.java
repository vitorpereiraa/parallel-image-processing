package pt.ipp.isep.dei.sismd.filter.swirl;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;
import pt.ipp.isep.dei.sismd.filter.Filter;

public interface SwirlFilter extends Filter {



    @Override
    default Color filter(int i, int j, Image image){
        ImageCoordinate centerCoordinate = getCenterCoordinate(image.width(), image.height());
        ImageCoordinate originalCoordinate = getOriginalCoordinate(new ImageCoordinate(j, i), centerCoordinate);
        int originalX = Math.max(0, Math.min(image.width() - 1, originalCoordinate.x()));
        int originalY = Math.max(0, Math.min(image.height() - 1, originalCoordinate.y()));
        return image.getPixelMatrix()[originalY][originalX];
    }


    record ImageCoordinate(int x, int y){}

    default ImageCoordinate getCenterCoordinate(int width, int height){
        int xCenterCoordinate = (width - 1)/2;
        int yCenterCoordinate = (height - 1)/2;
        return new ImageCoordinate(xCenterCoordinate, yCenterCoordinate);
    }

    default ImageCoordinate getSwirlCoordinateOf(ImageCoordinate coordinate, ImageCoordinate centerCoordinate) {
        int dx = coordinate.x() - centerCoordinate.x;
        int dy = coordinate.y() - centerCoordinate.y;
        double distance = Math.sqrt((dx*dx) + (dy*dy));
        double theta = (Math.PI/256) * distance;
        int xDash = (int) (dx * Math.cos(theta) - dy * Math.sin(theta) + centerCoordinate.x());
        int yDash = (int) (dx * Math.sin(theta) + dy * Math.cos(theta) + centerCoordinate.y());
        return new ImageCoordinate(xDash, yDash);
    }

    default ImageCoordinate getOriginalCoordinate(ImageCoordinate swirlCoordinate, ImageCoordinate centerCoordinate) {
        // Calculate the reverse transformation to find the original pixel coordinate
        int dxDash = swirlCoordinate.x() - centerCoordinate.x();
        int dyDash = swirlCoordinate.y() - centerCoordinate.y();
        double distance = Math.sqrt((dxDash*dxDash) + (dyDash*dyDash));
        double theta = -(Math.PI/256) * distance; // Negative angle for reverse transformation
        int x = (int) (dxDash * Math.cos(theta) - dyDash * Math.sin(theta) + centerCoordinate.x());
        int y = (int) (dxDash * Math.sin(theta) + dyDash * Math.cos(theta) + centerCoordinate.y());
        return new ImageCoordinate(x, y);
    }

}
