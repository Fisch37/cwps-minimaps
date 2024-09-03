package de.fisch37.cwpsminimaps.gui;

import de.fisch37.clientwps.data.Waypoint;
import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WaypointComponent extends BaseComponent {
    private static final int LEVEL_TEXTURE_SIZE = WaypointListComponent.SORT_HEIGHT;

    private final WaypointListComponent listComponent;
    private final Waypoint waypoint;
    private final Identifier accessTexture;
    private final int accessWidth;
    private boolean focused;

    public WaypointComponent(Waypoint waypoint, int accessWidth, WaypointListComponent listComponent) {
        super();
        this.waypoint = waypoint;
        this.accessTexture = TextureIdentifier.fromAccess(waypoint.access());
        this.accessWidth = accessWidth;
        this.sizing(Sizing.fill(), Sizing.fixed(WaypointListComponent.SORT_HEIGHT));
        // This might cause a memory leak, but honestly I don't caring
        this.listComponent = listComponent;
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
        final float TEXT_SCALE = 1;
        final int TEXT_COLOR = 0xFFFFFF;
        int penX = x + 1;
        int penY = y;


        context.drawGuiTexture(accessTexture, penX, penY, LEVEL_TEXTURE_SIZE, LEVEL_TEXTURE_SIZE);
        penX += accessWidth + SortOption.ICON_SIZE;
        context.drawText(
                Text.literal(waypoint.key().name()),
                penX, penY + 4,
                TEXT_SCALE,
                TEXT_COLOR
        );
        if (waypoint.key().ownerName() != null)
            context.drawText(
                    Text.literal(waypoint.key().ownerName()),
                    x + (int)(width * 0.7) + SortOption.ICON_SIZE, penY + 4,
                    TEXT_SCALE,
                    TEXT_COLOR
            );


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

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        return listComponent.onKeyPress(keyCode, scanCode, modifiers);
    }
}
