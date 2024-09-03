package de.fisch37.cwpsminimaps.gui;

import de.fisch37.clientwps.data.Waypoint;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class WaypointScreen extends BaseOwoScreen<FlowLayout> {
    private final List<Waypoint> waypoints;

    public WaypointScreen(Stream<Waypoint> waypoints) {
        this.waypoints = waypoints.toList();
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::horizontalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.TOP);

        var searchBox = Components.textBox(Sizing.fill());
        var waypointList = new WaypointListComponent(
                Sizing.fill(), Sizing.expand(),
                waypoints
        );
        searchBox.horizontalSizing(Sizing.fill())
                .id("search");
        searchBox.setPlaceholder(Text.translatable("gui.cwps.waypoints.search_placeholder"));
        searchBox.onChanged().subscribe(waypointList::updateSearch);

        rootComponent
                .child(Containers.horizontalFlow(Sizing.fill(), Sizing.fill())
                        .child(Containers.verticalFlow(Sizing.fill(15), Sizing.fill())
                                .child(Components.button(
                                        Text.translatable("gui.cwps.waypoints_menu.waypoints"),
                                        button -> {}
                                ).horizontalSizing(Sizing.fill()))
                                .margins(Insets.right(5))
                        )

                        .child(Containers.verticalFlow(Sizing.expand(), Sizing.fill())
                                .child(searchBox)
                                .child(waypointList)
                        )
                        .padding(Insets.of(10, 10, 5, 10))
                );
    }
}
