package de.fisch37.cwpsminimaps.integrations;

import de.fisch37.clientwps.data.Waypoint;

public interface MinimapIntegration {
    /**
     * Adds a single waypoint to the minimap or
     * does nothing if the waypoint already exists.
     * <p>
     * The semantics of this are implementation-dependent.
     *
     * @param waypoint The waypoint to add
     * @return Whether the waypoint was actually added or it already existed
     */
    boolean addWaypoint(Waypoint waypoint);
}
