package de.fisch37.cwpsminimaps.gui;

import de.fisch37.clientwps.data.Waypoint;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class WaypointListComponent extends FlowLayout {
    static final int SORT_HEIGHT = 16;

    private final AdvancedScrollContainer<FlowLayout> waypointScrollable;
    private final FlowLayout waypointContainer;
    private final List<WaypointComponent> waypointComponents;
    private final List<SortOption> sortOptions;
    private final FlowLayout sortContainer;

    private static int authorComparator(Waypoint a, Waypoint b) {
        String aName = a.key().ownerName();
        String bName = b.key().ownerName();
        return Comparator.nullsFirst(String::compareTo).compare(aName, bName);
    }

    public WaypointListComponent(
            Sizing horizontalSizing,
            Sizing verticalSizing,
            List<Waypoint> waypoints
    ) {
        super(horizontalSizing, verticalSizing, Algorithm.VERTICAL);

        sortContainer = Containers.horizontalFlow(Sizing.fill(), Sizing.fixed(SORT_HEIGHT));
        sortContainer.id("sort_container");
        sortOptions = new ArrayList<>(3);
        this.child(sortContainer);
        final Comparator<Waypoint> nameComparator = Comparator.comparing(waypoint -> waypoint.key().name());
        // This looks like bullshit and it is, but there's a layouting bug in OwO
        // that means positioned objects are not counted by expand
        var accessSortOption = addSortOption(
                "gui.cwps.waypoints_menu.sort_by_access",
                Comparator.comparing(Waypoint::access)
        );
        accessSortOption.id("sort_by_access");
        sortContainer.child(Containers.horizontalFlow(Sizing.fill(70), Sizing.fill())
                .child(accessSortOption)
                .child(addSortOption(
                        "gui.cwps.waypoints_menu.sort_by_name",
                        nameComparator
                ).setActive(true).horizontalSizing(Sizing.expand())
                        .id("sort_by_name"))
                .id("sort_subcontainer")
        );
        sortContainer.child(addSortOption(
                "gui.cwps.waypoints_menu.sort_by_author",
                WaypointListComponent::authorComparator
        ).horizontalSizing(Sizing.expand()).id("sort_by_author"));

        int accessLevelColWidth = accessSortOption.getPreCalcWidth();

        waypointContainer = Containers.verticalFlow(Sizing.fill(), Sizing.content());
        waypointContainer.id("waypoint_container");
        waypointScrollable = AdvancedScrollContainer.vertical(Sizing.expand(), Sizing.expand(), waypointContainer);
        waypointScrollable.scrollbar(ScrollContainer.Scrollbar.vanillaFlat());
        this.child(waypointScrollable);
        waypointComponents = new ArrayList<>(waypoints.size());
        for (Waypoint waypoint : waypoints) {
            WaypointComponent component = new WaypointComponent(waypoint, accessLevelColWidth, this);
            component.horizontalSizing(Sizing.fill());
            waypointComponents.add(component);
            waypointContainer.child(component);
        }

        setSorting(nameComparator, false);
    }

    public void updateSearch(String search) {
        waypointContainer.clearChildren();
        for (WaypointComponent component : waypointComponents) {
            if (component.getWaypoint().key().name().toLowerCase().startsWith(search.toLowerCase())) {
                waypointContainer.child(component);
            }
        }
    }

    public void setSorting(Comparator<Waypoint> comparator, boolean inverted) {
        Comparator<WaypointComponent> derived = (a, b) -> comparator.compare(a.getWaypoint(), b.getWaypoint());
        if (inverted) {
            final var normal = derived;
            derived = (a,b) -> normal.compare(b,a);
        }

        this.waypointComponents.forEach(waypointContainer::removeChild);
        this.waypointComponents.sort(derived);
        this.waypointComponents.forEach(waypointContainer::child);
    }

    private SortOption addSortOption(String key, Comparator<Waypoint> comparator) {
        SortOption sortOption = new SortOption(Text.translatable(key))
                .callback(makeSortCallback(comparator));
        sortOptions.add(sortOption);
        return sortOption;
    }

    private BiConsumer<SortOption, Boolean> makeSortCallback(Comparator<Waypoint> comparator) {
        return (widget, inverted) -> {
            for (SortOption w : sortOptions) {
                if (w == widget) continue;
                w.setActive(false);
            }
            setSorting(comparator, inverted);
        };
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        var focusHandler = focusHandler();
        if (focusHandler == null)
            return false;
        focusHandler.moveFocus(keyCode);
        var focused = focusHandler.focused();
        if (focused instanceof WaypointComponent) {
            waypointScrollable.scrollTo(
                    waypointScrollable.getScrollPosition()/waypointScrollable.getMaxScroll()
                            + scrollByToShow(focused)
            );
        }
        return true;
    }

    /**
     * Determines the least amount of scrolling required for the component to be fully visible
     * @param component The component to evaluate for
     * @return The amount to scroll by relative to the maximum scrolling value
     */
    private @Range(from = -1, to = 1) double scrollByToShow(Component component) {
        double visibleTop = waypointScrollable.y();
        double visibleBottom = visibleTop + waypointScrollable.height();

        double distanceToTop = component.y() - visibleTop;
        if (distanceToTop < 0) {
            return distanceToTop / waypointScrollable.getChildHeight();
        }

        double distanceToBottom = component.y() + component.height() - visibleBottom;
        if (distanceToBottom > 0) {
            return distanceToBottom / waypointScrollable.getChildHeight();
        }

        return 0;
    }
}
