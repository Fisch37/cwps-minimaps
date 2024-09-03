package de.fisch37.cwpsminimaps.gui;

import de.fisch37.clientwps.data.Waypoint;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class WaypointListComponent extends FlowLayout {
    static final int SORT_HEIGHT = 16;

    private final TextBoxComponent box;
    private final List<Waypoint> waypoints;
    private final List<WaypointComponent> widgets;
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
        box = Components.textBox(Sizing.fill(), "");
        box.setPlaceholder(Text.translatable("gui.cwps.waypoints.search_placeholder"));
        box.onChanged().subscribe(this::updateSearch);

        sortContainer = Containers.horizontalFlow(Sizing.fill(), Sizing.fixed(SORT_HEIGHT));
        sortOptions = new ArrayList<>(3);
        this.child(sortContainer);
        final Comparator<Waypoint> nameComparator = Comparator.comparing(waypoint -> waypoint.key().name());
        var accessSortOption = addSortOption(
                "gui.cwps.waypoints_menu.sort_by_access",
                Comparator.comparing(Waypoint::access)
        );
        addSortOption(
                "gui.cwps.waypoints_menu.sort_by_name",
                nameComparator
        );
        addSortOption(
                "gui.cwps.waypoints_menu.sort_by_author",
                WaypointListComponent::authorComparator
        ).positioning(Positioning.across(70, 0));

        int accessLevelColWidth = accessSortOption.getPreCalcWidth();

        this.waypoints = waypoints;
        this.widgets = new ArrayList<>(waypoints.size());
        for (Waypoint waypoint : waypoints) {
            WaypointComponent component = new WaypointComponent(waypoint, accessLevelColWidth);
            component.horizontalSizing(Sizing.fill());
            this.widgets.add(component);
            this.child(component);
        }

        setSorting(nameComparator, false);
    }

    public void updateSearch(String search) {
        this.clearChildren();
        this.child(this.sortContainer);
        for (WaypointComponent component : widgets) {
            if (component.getWaypoint().key().name().contains(search)) {
                this.child(component);
            }
        }
    }

    public void setSorting(Comparator<Waypoint> comparator, boolean inverted) {
        Comparator<WaypointComponent> derived = (a, b) -> comparator.compare(a.getWaypoint(), b.getWaypoint());
        if (inverted) {
            final var normal = derived;
            derived = (a,b) -> normal.compare(b,a);
        }

        this.widgets.forEach(this::removeChild);
        this.widgets.sort(derived);
        this.widgets.forEach(this::child);
    }

    private SortOption addSortOption(String key, Comparator<Waypoint> comparator) {
        SortOption sortOption = new SortOption(Text.translatable(key))
                .callback(makeSortCallback(comparator));
        sortContainer.child(sortOption);
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
}
