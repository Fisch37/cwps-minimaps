package de.fisch37.cwpsminimaps.integrations;

import de.fisch37.clientwps.data.Waypoint;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import journeymap.api.v2.common.waypoint.WaypointGroup;

import static de.fisch37.cwpsminimaps.CWPSMinimapsClient.MOD_ID;
import static de.fisch37.cwpsminimaps.CWPSMinimapsClient.LOG;

public class JourneyIntegration implements MinimapIntegration {
    private WaypointGroup group;

    public class JourneyMapInner implements IClientPlugin {
        @Override
        public void initialize(IClientAPI jmClientApi) {
            group = WaypointFactory.createWaypointGroup(MOD_ID, "cwps");
            LOG.info("Added Journey Map Integration!");
        }

        @Override
        public String getModId() {
            return MOD_ID;
        }
    }

    private journeymap.api.v2.common.waypoint.Waypoint convert(Waypoint waypoint) {
        return WaypointFactory.createClientWaypoint(
                MOD_ID,
                waypoint.position(),
                waypoint.key().toString(),
                waypoint.dimension(),
                false
        );
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
    public boolean addWaypoint(Waypoint waypoint) {
        return group.addWaypoint(convert(waypoint));
    }

    @Override
    public void updateWaypoint(Waypoint waypoint) {
        addWaypoint(waypoint);
    }
}
