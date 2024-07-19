package de.fisch37.cwpsminimaps.integrations;

import de.fisch37.cwpsminimaps.network.packet.WaypointsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static de.fisch37.cwpsminimaps.CWPSMinimapsClient.LOG;

public abstract class IntegrationRegistry {
    private static final Map<String, Class<? extends MinimapIntegration>> INTEGRATIONS = new LinkedHashMap<>();

    /**
     * @param targetMod   The id of the mod this integration supports
     * @param integration The integration class.
     *                    This class will be instantiated <em>if and only if</em> the integration is loaded.
     * @param <T>         The integration type
     * @return <code>true</code> if there was already an integration for the target mod, <code>false</code> otherwise.
     */
    public static <T extends MinimapIntegration> boolean register(String targetMod, Class<T> integration) {
        return INTEGRATIONS.put(targetMod, integration) != null;
    }

    /**
     * Registers the integration only if there isn't an integration for this mod already.
     *
     * @param targetMod   The id of the mod this integration supports
     * @param integration The integration class.
     *                    This class will be instantiated <em>if and only if</em> the integration is loaded.
     * @param <T>         The integration type
     * @return <code>true</code> if there was already an integration for the target mod, <code>false</code> otherwise.
     */
    public static <T extends MinimapIntegration> boolean registerIfAbsent(String targetMod, Class<T> integration) {
        return INTEGRATIONS.putIfAbsent(targetMod, integration) != null;
    }

    // TODO WTF is this typing?
    private static Optional<? extends Class<? extends MinimapIntegration>> getFirstAvailableIntegration() {
        FabricLoader loader = FabricLoader.getInstance();
        return INTEGRATIONS
                .entrySet()
                .stream()
                .filter(entry -> loader.isModLoaded(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private static Optional<MinimapIntegration> makeAvailableIntegration() {
        return getFirstAvailableIntegration().map(clazz -> {
            try {
                return clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                // Concatenation is necessary here because we want the Throwable version of error.
                LOG.error("Failed to instantiate minimap integration " + clazz, e);
                return null;
            }
        });
    }

    public static Optional<MinimapIntegration> startIntegration() {
        Optional<MinimapIntegration> integration = makeAvailableIntegration();
        if (integration.isPresent()) {
            MinimapIntegration integrationValue = integration.get();
            ClientPlayNetworking.registerGlobalReceiver(WaypointsPayload.ID, (payload, context) ->
                    payload.waypoints().forEach(integrationValue::addWaypoint));

            LOG.info("Registered minimap integration {}", integrationValue.getClass());
        } else {
            LOG.info("No minimap Integration found, none started");
        }
        return integration;
    }
}
