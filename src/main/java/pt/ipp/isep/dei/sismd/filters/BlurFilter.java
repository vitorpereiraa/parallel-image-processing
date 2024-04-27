package pt.ipp.isep.dei.sismd.filters;

import pt.ipp.isep.dei.sismd.domain.Color;

import java.util.function.Predicate;

public class BlurFilter extends ConditionalBlurFilter {

    private static Predicate<Color> DEFAULT_FILTER = color -> true;

    public BlurFilter(int blurEffect) {
        super(blurEffect, DEFAULT_FILTER);
    }

    public BlurFilter() {
        super(DEFAULT_FILTER);
    }
}
