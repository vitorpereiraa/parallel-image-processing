package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Image;

public interface FilterExecutor {

    Image apply(Image image);


}
