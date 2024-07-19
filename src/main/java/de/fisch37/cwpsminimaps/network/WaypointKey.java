package de.fisch37.cwpsminimaps.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record WaypointKey(@Nullable UUID owner, String name, @Nullable String ownerName) {
    public static final PacketCodec<RegistryByteBuf, WaypointKey> PACKET_CODEC = PacketCodec.tuple(
            new NullableCodec<>(Uuids.PACKET_CODEC), WaypointKey::owner,
            PacketCodecs.STRING, WaypointKey::name,
            new NullableCodec<>(PacketCodecs.STRING), WaypointKey::ownerName,
            WaypointKey::new
    );

    public String toString() {
        if (owner == null) return name;
        else return name + " (" + (ownerName == null ? owner : ownerName) + ")";
    }
}
