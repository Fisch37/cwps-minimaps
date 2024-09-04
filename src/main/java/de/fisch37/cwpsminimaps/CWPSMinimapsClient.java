package de.fisch37.cwpsminimaps;

import de.fisch37.clientwps.packet.waypoints.WaypointInfo;
import de.fisch37.cwpsminimaps.integrations.IntegrationRegistry;
import de.fisch37.cwpsminimaps.integrations.JourneyIntegration;
import de.fisch37.cwpsminimaps.integrations.MinimapIntegration;
import de.fisch37.cwpsminimaps.integrations.XaeroIntegration;
import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CWPSMinimapsClient implements ClientModInitializer {
    public static final String MOD_ID = "cwps-client";
    public static final Logger LOG = LoggerFactory.getLogger(CWPSMinimapsClient.class);
    public static CWPSAPIClient api;
    public static @Nullable Optional<MinimapIntegration> integration;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        InputHandler.register();

        IntegrationRegistry.registerIfAbsent("journeymap", JourneyIntegration.class);
        IntegrationRegistry.registerIfAbsent("xaerominimap", XaeroIntegration.class);
    }

    public static void onNewAPI(CWPSAPIClient api) {
        CWPSMinimapsClient.api = api;
        if (integration == null)
            integration = IntegrationRegistry.startIntegration();
        api.onWaypoints()
            .subscribe(waypoints -> integration.ifPresent(minimap ->
                waypoints.stream()
                .filter(WaypointInfo::accessible)
                .map(WaypointInfo::waypoint)
                .forEach(minimap::addWaypoint)
            ));
        api.onUpdate()
            .subscribe(waypoint -> integration.ifPresent(minimap -> {
                if (waypoint.accessible())
                    minimap.updateWaypoint(waypoint.waypoint());
            }));
    }
}
