package de.fisch37.cwpsminimaps;

import de.fisch37.clientwps.ClientFeatures;
import de.fisch37.clientwps.api.WPSClientInitializer;
import de.fisch37.clientwps.api.WPSSender;
import de.fisch37.clientwps.data.Waypoint;
import de.fisch37.clientwps.data.WaypointKey;
import de.fisch37.clientwps.packet.waypoints.WaypointInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

public class CWPSAPIClient implements WPSClientInitializer {
    private static CWPSAPIClient instance;
    private final TreeMap<WaypointKey, WaypointInfo> waypoints = new TreeMap<>();
    private WPSSender sender;

    public static Optional<CWPSAPIClient> getInstance() {
        return Optional.ofNullable(instance);
    }

    @Override
    public void onCWPSInitialize(WPSSender sender) {
        instance = this;
        this.sender = sender;
        CWPSMinimapsClient.onNewAPI(this);
    }

    @Override
    public ClientFeatures getFeatures() {
        return new ClientFeatures.Builder()
                .withWaypoints()
                .build()
                ;
    }

    private void addWaypoint(WaypointInfo info) {
        waypoints.put(info.waypoint().key(), info);
    }

    public @Nullable WaypointInfo getWaypoint(WaypointKey key) {
        return waypoints.get(key);
    }

    public Collection<WaypointInfo> getWaypoints() {
        return waypoints.values();
    }

    public Stream<Waypoint> getAccessibleWaypoints() {
        return waypoints.values()
                .stream()
                .filter(WaypointInfo::accessible)
                .map(WaypointInfo::waypoint)
                ;
    }

    @Override
    public void onWaypoints(List<WaypointInfo> waypoints) {
        waypoints.forEach(this::addWaypoint);
    }

    @Override
    public void onWaypointUpdate(WaypointInfo waypoint) {
        addWaypoint(waypoint);
    }
}
