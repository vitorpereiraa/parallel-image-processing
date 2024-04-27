package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

public interface Filter {

    Color apply(int i, int j, Image image);

}
