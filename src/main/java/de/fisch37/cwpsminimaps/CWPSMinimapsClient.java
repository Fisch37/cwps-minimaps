package de.fisch37.cwpsminimaps;

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
        IntegrationRegistry.registerIfAbsent("journeymap", JourneyIntegration.class);
        IntegrationRegistry.registerIfAbsent("xaerominimap", XaeroIntegration.class);
    }

    public static void onNewAPI(CWPSAPIClient api) {
        CWPSMinimapsClient.api = api;
        if (integration == null)
            integration = IntegrationRegistry.startIntegration();
    }
}
