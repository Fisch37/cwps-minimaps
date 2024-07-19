package de.fisch37.cwpsminimaps.network.packet;

public abstract class PacketTypes {
    public static final String MOD_ID = "cimple-waypoint-system";

    public static void register() {
        WaypointsPayload.register();
    }
}
