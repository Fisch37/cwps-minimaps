package de.fisch37.cwpsminimaps.gui;

import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import org.jetbrains.annotations.Range;

public class AdvancedScrollContainer<C extends Component> extends ScrollContainer<C> {
    protected AdvancedScrollContainer(ScrollDirection direction, Sizing horizontalSizing, Sizing verticalSizing, C child) {
        super(direction, horizontalSizing, verticalSizing, child);
    }

    /**
     * Scrolls the container <em>just</em> enough so that {@code component} is fully visible
     * @param component The component to scroll to.
     * @implNote If the passed component is not a descendant of this container, the behavior is undefined.
     */
    public void scrollIntoView(Component component) {
        scrollTo(
                currentScrollPosition/maxScroll
                        + scrollByToShow(component)
        );
    }

    /**
     * Determines the least amount of scrolling required for the component to be fully visible
     * @param component The component to evaluate for
     * @return The amount to scroll by relative to the maximum scrolling value
     */
    private @Range(from = -1, to = 1) double scrollByToShow(Component component) {
        double visibleTop = y();
        double visibleBottom = visibleTop + height();

        double distanceToTop = component.y() - visibleTop;
        if (distanceToTop < 0) {
            return distanceToTop / childSize;
        }

        double distanceToBottom = component.y() + component.height() - visibleBottom;
        if (distanceToBottom > 0) {
            return distanceToBottom / childSize;
        }

        return 0;
    }

    public static <C extends Component> AdvancedScrollContainer<C> vertical(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        return new AdvancedScrollContainer<C>(ScrollDirection.VERTICAL, horizontalSizing, verticalSizing, child);
    }

    public static <C extends Component> AdvancedScrollContainer<C> horizontal(Sizing horizontalSizing, Sizing verticalSizing, C child) {
        return new AdvancedScrollContainer<C>(ScrollDirection.HORIZONTAL, horizontalSizing, verticalSizing, child);
    }
}
