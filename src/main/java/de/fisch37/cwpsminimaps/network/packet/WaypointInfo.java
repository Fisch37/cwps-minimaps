package de.fisch37.cwpsminimaps.network.packet;

import de.fisch37.cwpsminimaps.network.Waypoint;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record WaypointInfo(Waypoint waypoint, boolean accessible) {
    public static final PacketCodec<RegistryByteBuf, WaypointInfo> PACKET_CODEC = PacketCodec.tuple(
            Waypoint.PACKET_CODEC, WaypointInfo::waypoint,
            PacketCodecs.BOOL, WaypointInfo::accessible,
            WaypointInfo::new
    );
}
