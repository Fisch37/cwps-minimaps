package de.fisch37.cwpsminimaps;

import de.fisch37.clientwps.ClientFeatures;
import de.fisch37.clientwps.api.WPSClientInitializer;
import de.fisch37.clientwps.api.WPSSender;
import de.fisch37.clientwps.data.Waypoint;
import de.fisch37.clientwps.data.WaypointKey;
import de.fisch37.clientwps.packet.waypoints.WaypointInfo;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
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
    private final EventStream<WaypointsListener> onWaypoints = WaypointsListener.newStream();
    private final EventStream<UpdateListener> onUpdate = UpdateListener.newStream();

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
        onWaypoints.sink().onData(waypoints);
    }

    @Override
    public void onWaypointUpdate(WaypointInfo waypoint) {
        addWaypoint(waypoint);
        onUpdate.sink().onData(waypoint);
    }

    public EventSource<WaypointsListener> onWaypoints() {
        return onWaypoints.source();
    }

    public EventSource<UpdateListener> onUpdate() {
        return onUpdate.source();
    }

    @FunctionalInterface
    public interface WaypointsListener {
        void onData(List<WaypointInfo> waypoints);

        static EventStream<WaypointsListener> newStream() {
            return new EventStream<>(subscribers -> waypoints -> {
                for (WaypointsListener subscriber : subscribers) {
                    subscriber.onData(waypoints);
                }
            });
        }
    }

    @FunctionalInterface
    public interface UpdateListener {
        void onData(WaypointInfo waypoint);

        static EventStream<UpdateListener> newStream() {
            return new EventStream<>(subscribers -> waypoint -> {
                for (UpdateListener subscriber : subscribers) {
                    subscriber.onData(waypoint);
                }
            });
        }
    }
}
