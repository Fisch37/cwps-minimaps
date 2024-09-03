package de.fisch37.cwpsminimaps.gui;

import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;

public class AdvancedScrollContainer<C extends Component> extends ScrollContainer<C> {
    protected AdvancedScrollContainer(ScrollDirection direction, Sizing horizontalSizing, Sizing verticalSizing, C child) {
        super(direction, horizontalSizing, verticalSizing, child);
    }

    public int getMaxScroll() {
        return maxScroll;
    }

    public double getScrollPosition() {
        return currentScrollPosition;
    }

    public int getChildHeight() {
        return childSize;
    }

    public static <C extends Component> AdvancedScrollContainer<C> vertical(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        return new AdvancedScrollContainer<C>(ScrollDirection.VERTICAL, horizontalSizing, verticalSizing, child);
    }

    public static <C extends Component> AdvancedScrollContainer<C> horizontal(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        return new AdvancedScrollContainer<C>(ScrollDirection.HORIZONTAL, horizontalSizing, verticalSizing, child);
    }
}
