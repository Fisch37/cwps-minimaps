package de.fisch37.cwpsminimaps;

import de.fisch37.clientwps.packet.PacketTypes;
import de.fisch37.cwpsminimaps.integrations.IntegrationRegistry;
import de.fisch37.cwpsminimaps.integrations.JourneyIntegration;
import de.fisch37.cwpsminimaps.integrations.MinimapIntegration;
import de.fisch37.cwpsminimaps.integrations.XaeroIntegration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
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

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ClientPlayNetworking.registerGlobalReceiver(PacketTypes.WAYPOINTS, (payload, context) -> {
                LOG.info("Received waypoints payload");
            });
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (integration != null)
                integration.ifPresent(IntegrationRegistry::reloadWaypoints);
        });
    }

    public static void onNewAPI(CWPSAPIClient api) {
        CWPSMinimapsClient.api = api;
        if (integration == null)
            integration = IntegrationRegistry.startIntegration();
        else integration.ifPresent(IntegrationRegistry::reloadWaypoints);
    }
}
