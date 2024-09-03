package de.fisch37.cwpsminimaps.gui;

import de.fisch37.clientwps.data.Waypoint;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class WaypointListComponent extends FlowLayout {
    static final int SORT_HEIGHT = 16;
    static final int ACCESS_WIDTH = 48;
    static final int NAME_WIDTH = 128;

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

        // TODO: Make this less repetitive without 500 arguments
        sortContainer = Containers.horizontalFlow(Sizing.fill(), Sizing.fixed(SORT_HEIGHT));
        sortOptions = new ArrayList<>(3);
        this.child(sortContainer);
        sortContainer.child(addSortOption(new SortOption(
                        0,
                        0,
                        ACCESS_WIDTH,
                        SORT_HEIGHT,
                        Text.translatable("gui.cwps.waypoints_menu.sort_by_access")
                ).callback(makeSortCallback(Comparator.comparing(Waypoint::access)))
        ));
        sortContainer.child(addSortOption(new SortOption(
                ACCESS_WIDTH,
                0,
                NAME_WIDTH,
                SORT_HEIGHT,
                Text.translatable("gui.cwps.waypoints_menu.sort_by_name")
        ).setActive(true)
                .callback(makeSortCallback(Comparator.comparing(waypoint -> waypoint.key().name())))
        ));
        sortContainer.child(addSortOption(new SortOption(
                        ACCESS_WIDTH + NAME_WIDTH,
                        0,
                        NAME_WIDTH,
                        SORT_HEIGHT,
                        Text.translatable("gui.cwps.waypoints_menu.sort_by_author")
                ).callback(makeSortCallback(WaypointListComponent::authorComparator))
        ));
        this.waypoints = waypoints;
        this.widgets = new ArrayList<>(waypoints.size());
        for (Waypoint waypoint : waypoints) {
            WaypointComponent component = new WaypointComponent(waypoint);
            this.widgets.add(component);
            this.child(component);
        }
        setSorting(Comparator.comparing(waypoint -> waypoint.key().name()), false);
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

    private Component addSortOption(SortOption widget) {
        sortOptions.add(widget);
        return Components.wrapVanillaWidget(widget);
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
