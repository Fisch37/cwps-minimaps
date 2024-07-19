package de.fisch37.cwpsminimaps.integrations;

import de.fisch37.cwpsminimaps.network.Waypoint;
import de.fisch37.cwpsminimaps.network.packet.WaypointInfo;
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

    /**
     * Adds a single waypoint to the minimap or
     * does nothing if the waypoint already exists.
     * <p>
     * The semantics of this are implementation-dependent.
     *
     * @param wpsInfo The waypoint to add
     * @return Whether the waypoint was actually added or it already existed
     */
    @Override
    public boolean addWaypoint(WaypointInfo wpsInfo) {
        Waypoint waypoint = wpsInfo.waypoint();
        journeymap.api.v2.common.waypoint.Waypoint jwaypoint = WaypointFactory.createClientWaypoint(
                MOD_ID,
                waypoint.pos(),
                waypoint.key().toString(),
                waypoint.world(),
                false
        );
        return group.addWaypoint(jwaypoint);
    }
}
