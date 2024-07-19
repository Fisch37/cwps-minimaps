package de.fisch37.cwpsminimaps.network.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static de.fisch37.cwpsminimaps.network.packet.PacketTypes.MOD_ID;

public record WaypointsPayload(List<WaypointInfo> waypoints) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, WaypointsPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.collection(size -> new ArrayList<>(), WaypointInfo.PACKET_CODEC),
            WaypointsPayload::waypoints,
            WaypointsPayload::new
    );
    public static final Id<WaypointsPayload> ID = new Id<>(Identifier.of(MOD_ID, "waypoints"));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, PACKET_CODEC);
    }
}
