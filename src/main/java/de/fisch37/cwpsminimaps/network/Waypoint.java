package de.fisch37.cwpsminimaps.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public record Waypoint(WaypointKey key, BlockPos pos, RegistryKey<World> world, Integer yaw, AccessLevel access) {
    public static final PacketCodec<RegistryByteBuf, Waypoint> PACKET_CODEC = PacketCodec.tuple(
            WaypointKey.PACKET_CODEC, Waypoint::key,
            BlockPos.PACKET_CODEC, Waypoint::pos,
            RegistryKey.createPacketCodec(RegistryKeys.WORLD), Waypoint::world,
            PacketCodecs.INTEGER, Waypoint::yaw,
            AccessLevel.PACKET_CODEC, Waypoint::access,
            Waypoint::new
    );
}
