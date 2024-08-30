package de.fisch37.cwpsminimaps.integrations;

import de.fisch37.clientwps.data.AccessLevel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.Hashtable;

import static de.fisch37.cwpsminimaps.CWPSMinimapsClient.MOD_ID;

public class XaeroIntegration implements MinimapIntegration {
    private final Hashtable<Integer, Waypoint> xaerosCustomWaypointsHook;

    public XaeroIntegration() {
        xaerosCustomWaypointsHook = WaypointsManager.getCustomWaypoints(MOD_ID);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clearAllWaypoints());
    }

    private void clearAllWaypoints() {
        xaerosCustomWaypointsHook.clear();
    }

    private int getColour(AccessLevel level) {
        /*
         * Xaero Colour table in BGR:
         * 00: 000000
         * 01: aa0000
         * 02: 00aa00
         * 03: aaaa00
         * 04: 0000aa
         * 05: aa00aa
         * 06: 00aaff
         * 07: aaaaaa
         * 08: 555555
         * 09: ff5555
         * 10: 55ff55
         * 11: ffff55
         * 12: 0000ff
         * 13: ff55ff
         * 14: 55ffff
         * 15: ffffff
         */
        return switch (level) {
            case SECRET -> 4;
            case PRIVATE -> 6;
            case PUBLIC, OPEN -> 2;
        };
    }

    /**
     * Adds a single waypoint to the minimap or
     * does nothing if the waypoint already exists.
     * <p>
     * The semantics of this are implementation-dependent.
     *
     * @param waypoint The waypoint to add
     * @return Whether the waypoint was actually added or it already existed
     */
    @Override
    public boolean addWaypoint(de.fisch37.clientwps.data.Waypoint waypoint) {
        xaerosCustomWaypointsHook.put(
                waypoint.key().hashCode(),
                new Waypoint(
                        waypoint.position().getX(),
                        waypoint.position().getY(),
                        waypoint.position().getZ(),
                        waypoint.key().toString(),
                        Character.toString(waypoint.key().name().charAt(0)),
                        getColour(waypoint.access()),
                        0,  // Don't disappear on arrival and be visible
                        true  // Don't persist across client sessions
                )
        );
        return true;
    }
}
