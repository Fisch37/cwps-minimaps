package de.fisch37.cwpsminimaps;

import de.fisch37.cwpsminimaps.gui.WaypointScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

import static de.fisch37.cwpsminimaps.CWPSMinimapsClient.api;

public abstract class InputHandler {
    private static final String CATEGORY = "key.cwps-minimaps.category";
    private static final KeyBinding WAYPOINTS = binding("key.cwps.waypoints", 78);

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (WAYPOINTS.wasPressed() && client.player != null) {
                client.setScreen(new WaypointScreen(api.getAccessibleWaypoints()));
                // client.setScreen(new Test());
            }
        });
    }

    private static KeyBinding binding(String name, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(name, code, CATEGORY));
    }
}
