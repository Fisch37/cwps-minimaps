package de.fisch37.cwpsminimaps.gui;

import de.fisch37.clientwps.data.Waypoint;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WaypointComponent extends BaseComponent {
    private final Waypoint waypoint;
    private final Identifier accessTexture;
    private boolean focused;

    public WaypointComponent(Waypoint waypoint) {
        super();
        this.waypoint = waypoint;
        this.accessTexture = TextureIdentifier.fromAccess(waypoint.access());
        this.sizing(Sizing.fill(), Sizing.fixed(WaypointListComponent.SORT_HEIGHT));
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }

    @Override
    public void onFocusGained(FocusSource source) {
        focused = true;
    }

    @Override
    public void onFocusLost() {
        focused = false;
    }

    @Override
    public void draw(
            io.wispforest.owo.ui.core.OwoUIDrawContext context,
            int mouseX,
            int mouseY,
            float partialTicks,
            float delta
    ) {
        final int LEVEL_TEXTURE_SIZE = WaypointListComponent.SORT_HEIGHT;
        final float TEXT_SCALE = 1;
        final int TEXT_COLOR = 0xFFFFFF;
        int penX = x + 1;
        int penY = y;


        context.drawGuiTexture(accessTexture, penX, penY, LEVEL_TEXTURE_SIZE, LEVEL_TEXTURE_SIZE);
        penX += WaypointListComponent.ACCESS_WIDTH;
        context.drawText(Text.literal(waypoint.key().name()), penX, penY + 4, TEXT_SCALE, TEXT_COLOR);
        penX += WaypointListComponent.NAME_WIDTH;
        if (waypoint.key().ownerName() != null)
            context.drawText(Text.literal(waypoint.key().ownerName()), penX, penY + 4, TEXT_SCALE, TEXT_COLOR);


        if (focused)
            super.drawFocusHighlight(context, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public void drawFocusHighlight(
            io.wispforest.owo.ui.core.OwoUIDrawContext context,
            int mouseX,
            int mouseY,
            float partialTicks,
            float delta
    ) {
        // Removes double-draw when focused via keyboard
    }
}
