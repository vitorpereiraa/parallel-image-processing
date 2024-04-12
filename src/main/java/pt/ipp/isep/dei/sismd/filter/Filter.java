package pt.ipp.isep.dei.sismd.filter;

import pt.ipp.isep.dei.sismd.domain.Color;
import pt.ipp.isep.dei.sismd.domain.Image;

public interface Filter {

    Color filter(int i, int j, Image image);

}
