package pt.ipp.isep.dei.sismd.filter.swirl;

public interface SwirlFilter {

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
}
